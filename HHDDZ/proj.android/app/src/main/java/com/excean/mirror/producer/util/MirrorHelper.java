package com.excean.mirror.producer.util;

import com.android.apksigner.ApkSignerTool;
import com.excean.mirror.AppHolder;
import com.excean.mirror.producer.resource.ARSCDecoder;
import com.excean.mirror.producer.resource.XMLDecoder;
import com.excean.mirror.producer.resource.util.DataUtil;
import com.zero.support.common.AppGlobal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MirrorHelper {
    public static final String PACKAGE_HOLDER = "mirror.package.holder";
    public static final String MIRROR_HOLDER = "mirror.plugin.package.holder";
    public static final String USER_ID_HOLDER = "mirror.user.holder";
    public static final String NAME_HOLDER = "mirror.name.holder";
    public static final String ATTRIBUTE_HOLDER = "mirror.attribute.holder";
    public static final String SERVICE_HOLDER = "mirror.service.holder";
    public static final String LABEL_HOLDER = "mirror.label.holder";
    public static final String OBB_HOLDER = "mirror.obb.holder";
    /**
     * v2签名使用了 java 8的代码，不要打开
     */
    static String[] CMD = new String[]{"sign", "--ks=", "--ks-pass=pass:signer", "--ks-key-alias=signer", "--key-pass=pass:signer", "--in=", "--out=", "--ks-type=bks", "--v2-signing-enabled=false", "--v3-signing-enabled=false"};
    static String[] CMD2 = new String[]{"sign", "--ks=", "--ks-pass=pass:gkh5izzs2tqoa5z", "--ks-key-alias=yessc61z1", "--key-pass=pass:gkh5izzs2tqoa5z", "--in=", "--out=", "--ks-type=bks", "--v2-signing-enabled=false", "--v3-signing-enabled=false"};
    static String[] CMD3 = new String[]{"sign", "--ks=", "--ks-pass=pass:lupp845a5dmfi50", "--ks-key-alias=lp0d0qge4", "--key-pass=pass:lupp845a5dmfi50", "--in=", "--out=", "--ks-type=bks", "--v2-signing-enabled=true", "--v3-signing-enabled=false"};

    public static byte[] fixManifest(InputStream stream, String packageName, boolean hasObb, int userId) throws IOException {
        XMLDecoder decoder = new XMLDecoder(ByteBuffer.wrap(DataUtil.toBytes(stream)).order(ByteOrder.LITTLE_ENDIAN));
        decoder.readStringBlock();
        String prefix = MIRROR_HOLDER + ":";
        List<String> strings = decoder.getStringBlock().getStringPool();
        String newPackage = packageName + ".mirror" + userId;
        Map<String, String> map = new HashMap<>();
        for (String key : strings) {
            if (key.startsWith(prefix)) {
                map.put(key, key.replace(MIRROR_HOLDER, newPackage));
            }
        }
        map.put(PACKAGE_HOLDER, packageName);
        map.put(USER_ID_HOLDER, String.valueOf(userId));
        map.put(MIRROR_HOLDER, newPackage);
        map.put(ATTRIBUTE_HOLDER, String.valueOf(AppHolder.getVirtualAttribute(packageName)));
        map.put(SERVICE_HOLDER, AppGlobal.getApplication().getPackageName());
        map.put(OBB_HOLDER, String.valueOf(hasObb));
        ByteBuffer byteBuffer = (ByteBuffer) decoder.write(map);
        byteBuffer.position(0);
        return byteBuffer.array();
    }

    public static byte[] fixResource(InputStream inputStream, String title,String name) throws IOException {
        ARSCDecoder decoder = new ARSCDecoder(ByteBuffer.wrap(DataUtil.toBytes(inputStream)).order(ByteOrder.LITTLE_ENDIAN));
        decoder.readStringBlock();
        Map<String, String> map = new HashMap<>();
        map.put(LABEL_HOLDER, name);
        map.put(NAME_HOLDER, title);
        ByteBuffer byteBuffer = decoder.write(map);
        byteBuffer.position(0);
        return byteBuffer.array();
    }

    public static boolean sign(File key, File src, File output) {
        String[] cmd = CMD2.clone();
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1);
            byteBuffer.limit(1);
            cmd[1] += key.getCanonicalPath();
            cmd[5] += src.getCanonicalPath();
            cmd[6] += output.getCanonicalPath();
            ApkSignerTool.main(cmd);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
