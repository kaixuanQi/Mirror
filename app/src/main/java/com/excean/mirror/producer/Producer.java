package com.excean.mirror.producer;

import com.excean.mirror.producer.util.MirrorHelper;
import com.excean.virutal.api.virtual.FileUtils;
import com.zero.support.common.AppGlobal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Producer {
    private File root;
    private File src;
    private File out;
    private File material;
    private File keystore;
    private String packageName;
    private String targetPackageName;


    public Producer(File root, String packageName) {
        this.root = root;
        this.material = new File(root, "material.apk");
        this.src = new File(root, "source.apk");
        this.out = new File(root, "target.apk");
        this.keystore = new File(root, "signer.bks");
        this.packageName = packageName;
    }

    public String getTargetPackageName() {
        return targetPackageName;
    }

    public void produce(File icon, String title, int userId, String abiName) throws IOException {
        this.targetPackageName = packageName + ".mirror" + userId;
        FileUtils.extractAsset(AppGlobal.getApplication(), "resource.apk", material);
        FileUtils.extractAsset(AppGlobal.getApplication(), "target.bks", keystore);
        generateSource(packageName, icon, title, userId, abiName);
        FileUtils.deleteQuietly(material);
        MirrorHelper.sign(keystore, src, out);
        FileUtils.deleteQuietly(keystore);
        FileUtils.deleteQuietly(src);
    }

    private void generateSource(String packageName, File icon, String title, int userId, String abiName) throws IOException {
        ZipFile materialZip = new ZipFile(material);
        Enumeration<ZipEntry> enumeration = (Enumeration<ZipEntry>) materialZip.entries();
        FileOutputStream source = new FileOutputStream(src);
        ZipOutputStream sourceZip = new ZipOutputStream(source);

        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();
            ZipEntry outEntry;
            String name = entry.getName();
            if (name.startsWith("lib/")) {
                if (!name.startsWith("lib/" + abiName)) {
                    continue;
                }
            }
            InputStream is = materialZip.getInputStream(entry);
            if (name.equals("AndroidManifest.xml")) {
                outEntry = new ZipEntry(entry.getName());
                sourceZip.putNextEntry(outEntry);
                MirrorHelper.fixManifest(is, packageName, userId, sourceZip);
            } else if (name.equals("resources.arsc")) {
                outEntry = new ZipEntry(name);
                sourceZip.putNextEntry(outEntry);
                MirrorHelper.fixResource(is, title, sourceZip);
            } else if (name.equals("res/D2.png")) {
                outEntry = new ZipEntry(name);
                FileInputStream iconStream = new FileInputStream(icon);
                sourceZip.putNextEntry(outEntry);
                FileUtils.copy(iconStream, sourceZip);
                iconStream.close();
            } else {
                if (entry.getMethod() == ZipEntry.STORED) {
                    outEntry = new ZipEntry(entry);
                } else {
                    outEntry = new ZipEntry(name);
                }
                sourceZip.putNextEntry(outEntry);
                FileUtils.copy(is, sourceZip);
            }
            is.close();
        }
        sourceZip.close();
    }

    public File getTarget() {
        return out;
    }
}
