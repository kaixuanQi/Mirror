package com.zero.support.common.toolbox;


import android.net.Uri;

import com.zero.common.archive.BoundInputStream;
import com.zero.common.archive.Md5CheckSum;
import com.zero.common.archive.SeekInputStream;
import com.zero.common.archive.SeekOutputStream;
import com.zero.support.common.util.FileUtils;
import com.zero.support.common.util.Preferences;
import com.zero.support.common.util.ProgressListener;
import com.zero.support.work.AppExecutor;
import com.zero.support.work.Observer;
import com.zero.support.work.Progress;
import com.zero.support.work.PromiseTask;
import com.zero.support.work.Response;
import com.zero.support.work.SnapShotTask;
import com.zero.support.work.exception.FileVerifyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.zip.CheckedOutputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FileDownloadTask extends SnapShotTask<FileRequest, File> {
    private static final String suffix = "";

    @Override
    protected File process(FileRequest input) throws Throwable {
        File output = input.output();

        Preferences preferences = new Preferences(new File(output.getParent(), output.getName() + suffix + ".json"));
        final Progress progress = new Progress();
        if (input.parallel() == 1) {
            progress.init(FileRequest.TYPE_DOWNLOADING, 0, input.size(), 0, 1, 1);
            Block block = new Block(input, preferences);
            block.start(AppExecutor.current(), new Observer<Progress>() {
                @Override
                public void onChanged(Progress value) {
                    progress.init(value.handled());
                    progress.speed = value.speed;
                    publishProgressChanged(progress);
                }
            });
            Response<Boolean> response = block.task.getResult();
            if (!response.isSuccessful()) {
                throw response.getCause();
            }
            if (input.md5() != null) {
                String md5 = FileUtils.md5File(output);
                if (!md5.equals(input.md5())) {
                    preferences.edit().clear().commit();
                    output.delete();
                    throw new FileVerifyException("md5 mismatch " + md5 + "--" + input.md5());
                }
            }
        } else {
            int totalStage;
            if (input.md5() != null && input.md5().length() != 0) {
                totalStage = 2;
            } else {
                totalStage = 1;
            }
            progress.init(FileRequest.TYPE_DOWNLOADING, 0, input.size(), 0, 1, totalStage);
            List<Block> blocks = build(preferences, input);
            for (Block block : blocks) {
                block.start(AppExecutor.async(), new Observer<Progress>() {
                    private long handled;

                    @Override
                    public void onChanged(Progress value) {
                        progress.init(progress.handled() + value.handled() - handled);
                        progress.speed = value.speed;
                        handled = progress.handled();
                        publishProgressChanged(progress);
                    }
                });
            }
            for (Block block : blocks) {
                block.task.getResult();
            }
            for (Block block : blocks) {
                Response<Boolean> response = block.task.getResult();
                if (!response.isSuccessful()) {
                    throw response.getCause();
                }
            }

            if (input.size() != -1 && input.size() != progress.handled()) {
                throw new FileNotFoundException("file size is mismatch ");
            }
            if (totalStage == 2) {
                progress.init(FileRequest.TYPE_VERIFYING, 0, progress.handled(), 0, totalStage, totalStage);
                InputStream inputStream = new SeekInputStream(output, input.offset());
                FileUtils.md5File(inputStream, progress.handled(), progress, new ProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        progress.init(progress.handled(), progress.total());
                        publishProgressChanged(progress);
                    }
                });
            }


        }
        return input.output();
    }

    private List<Block> build(Preferences preferences, FileRequest input) {
        long blockSize = input.size() / input.parallel();

        return Collections.emptyList();
    }

    private static class Block {
        Preferences preferences;
        String url;
        int index;
        long begin;
        long length;
        String md5;
        File file;
        OkHttpClient client;

        BlockTask task;

        public Block(FileRequest request, Preferences preferences, int index, long begin, long length) {
            this.preferences = preferences;
            this.url = request.url();
            this.index = index;
            this.begin = begin + request.offset();
            this.length = length;
            this.client = request.client();
            this.file = request.output();
        }


        public Block(FileRequest request, Preferences preferences) {
            this.preferences = preferences;
            this.url = request.url();
            this.index = 0;
            this.begin = request.offset();
            this.length = request.size();
            this.client = request.client();
            this.file = request.output();
            this.md5 = request.md5();
        }

        public void start(Executor executor, Observer<Progress> progress) {
            if (task != null && !task.isFinished()) {
                return;
            }
            task = new BlockTask();
            task.progress().observe(progress);
            task.input(this).run(executor);
        }

        public long getHandled() {
            return preferences.getLong(String.valueOf(index), 0);
        }

        public void setHandled(long handled) {
            preferences.putLong(String.valueOf(index), handled);
        }
    }

    private static class BlockTask extends PromiseTask<Block, Boolean> {

        @Override
        protected Boolean process(Block block) throws Exception {
            long downloadSize = block.preferences.getLong(String.valueOf(block.index), 0L);

            InputStream input;
            if (block.url.startsWith("file://")) {
                File file = new File(Uri.parse(block.url).getPath());
                input = new BoundInputStream(new SeekInputStream(file, block.begin + downloadSize), block.length - block.begin);
            } else {
                Request request = new Request.Builder().url(block.url).addHeader("Accept-Encoding", "identity")
                        .addHeader("Range", "bytes=" + (block.begin + downloadSize) + "-" + (block.begin + block.length - 1))
                        .build();
                input = block.client.newCall(request).execute().body().byteStream();
            }

            OutputStream output;
            Md5CheckSum checkSum = null;
            if (block.md5 != null) {
                checkSum = new Md5CheckSum();
                output = new CheckedOutputStream(new SeekOutputStream(block.file, block.begin + downloadSize), checkSum);
            } else {
                output = new SeekOutputStream(block.file, block.begin + downloadSize);
            }
            Progress progress = new Progress();
            progress.init(downloadSize, block.length);
            publishProgressChanged(progress);
            int n;
            long handled = progress.handled();
            long lashHandled = handled;
            byte[] bytes = new byte[8192];
            long lastTime = System.currentTimeMillis();
            long currentTime;
            long duration;
            while (-1 != (n = input.read(bytes))) {
                output.write(bytes, 0, n);
                handled += n;
                progress.init(handled);
                currentTime = System.currentTimeMillis();
                duration = currentTime - lastTime;
                if (duration > 200) {
                    lastTime = currentTime;
                    progress.speed = (handled - lashHandled) * 1000 / duration;
                    lashHandled = handled;
                    publishProgressChanged(progress);
                    block.setHandled(progress.handled());
                }
            }
            input.close();
            output.close();
            publishProgressChanged(progress);
            return true;
        }
    }
}
