/*
* author: Bartlomiej Kierys
* date: 2017-02-06
* email: bskierys@gmail.com
*/
package com.suredigit.inappfeedback;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

public class Installation {
    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";

    public Installation() {
    }

    public static synchronized String id(Context context) {
        if(sID == null) {
            File installation = new File(context.getFilesDir(), "INSTALLATION");

            try {
                if(!installation.exists()) {
                    writeInstallationFile(installation);
                }

                sID = readInstallationFile(installation);
            } catch (Exception var3) {
                throw new RuntimeException(var3);
            }
        }

        return sID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int)f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }
}
