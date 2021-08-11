package com.excean.mirror;

import com.excean.mirror.producer.resource.AppResource;
import com.excean.mirror.producer.resource.StringBlock;
import com.excean.mirror.producer.resource.XMLDecoder;
import com.excean.mirror.producer.resource.util.DataUtil;
import com.excean.mirror.producer.util.MirrorHelper;
import com.excean.virutal.api.virtual.FileUtils;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        File file = new File("./src/main/assets/resource.apk");
        File target = new File("./src/main/assets/target.apk");
        ZipFile zipFile = new ZipFile(file);

        System.out.println(file.getCanonicalPath());

        try {
            ZipEntry entry = zipFile.getEntry("resources.arsc");
            AppResource resource = new AppResource(zipFile.getInputStream(entry));
            resource.readStringBlock();
            StringBlock block = resource.getDecoder().getTableStrings();
            System.out.println(block.getStringPool());
            entry = zipFile.getEntry("AndroidManifest.xml");
            XMLDecoder decoder = new XMLDecoder(ByteBuffer.wrap(DataUtil.toBytes(zipFile.getInputStream(entry))).order(ByteOrder.LITTLE_ENDIAN));
            decoder.readStringBlock();
            System.out.println(decoder.getStringBlock().getStringPool());
//            generateSource(file,target,"fuck.fuck ",null,"xxx",2);
        }finally {
            zipFile.close();
        }

    }

    private void generateSource(File material,File target,String packageName, File icon, String title, int userId) throws IOException {
        ZipFile materialZip = new ZipFile(material);
        Enumeration<ZipEntry> enumeration = (Enumeration<ZipEntry>) materialZip.entries();
        FileOutputStream source = new FileOutputStream(target);
        ZipOutputStream sourceZip = new ZipOutputStream(source);
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();
            ZipEntry outEntry;
            InputStream is = materialZip.getInputStream(entry);
            if (entry.getName().equals("AndroidManifest.xml")) {
                outEntry = new ZipEntry(entry.getName());
                sourceZip.putNextEntry(outEntry);
                MirrorHelper.fixManifest(is, packageName, userId, sourceZip);
            } else if (entry.getName().equals("resources.arsc")) {
                outEntry = new ZipEntry(entry.getName());
                sourceZip.putNextEntry(outEntry);
                MirrorHelper.fixResource(is,title, sourceZip);
            } else {
                if (entry.getMethod() == ZipEntry.STORED) {
                    outEntry = new ZipEntry(entry);
                } else {
                    outEntry = new ZipEntry(entry.getName());
                }
                sourceZip.putNextEntry(outEntry);
                FileUtils.copy(is, sourceZip);
            }
        }
        sourceZip.close();
    }
}