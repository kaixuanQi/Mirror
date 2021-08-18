package com.yyong.mirror.producer;

import android.app.Application;
import android.os.Build;

import com.zero.support.common.AppGlobal;
import com.zero.support.common.util.Preferences;
import com.zero.support.common.util.Singleton;

import java.io.File;

public class ProducerManager {
    private static Singleton<ProducerManager> singleton = new Singleton<ProducerManager>() {
        @Override
        protected ProducerManager create() {
            return new ProducerManager(AppGlobal.getApplication());
        }
    };
    private File root;
    private File produce;
    private File material;
    private Preferences preferences;

    public static ProducerManager getDefault() {
        return singleton.get();
    }

    public ProducerManager(Application application) {
        if (Build.VERSION.SDK_INT>24){
            this.root = new File(application.getFilesDir(), "producer");
        }else {
            this.root = new File(application.getExternalFilesDir("producer"), "producer");
        }
        this.produce = new File(root, "produce");
        this.material = new File(root, "material");
    }

    public Producer getProducer(String packageName) {
        File root = new File(produce, packageName);
        root.mkdirs();
        return new Producer(root,packageName);
    }


    public File getRoot() {
        return root;
    }
}
