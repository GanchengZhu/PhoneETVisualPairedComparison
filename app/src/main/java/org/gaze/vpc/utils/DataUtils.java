package org.gaze.vpc.utils;

import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class DataUtils {

    public static void saveTextToPath(String path, String string) {
        File file = new File(path);
        Log.i(DataUtils.class.getSimpleName() + ": Receive saved path is", file.getAbsolutePath());
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
                fos = new FileOutputStream(file);
                osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                osw.write(string);
                osw.flush();
                osw.close();
                fos.close();
            } catch (IOException e) {
                Looper.prepare();
                e.printStackTrace();
                Log.e(DataUtils.class.getSimpleName(), "" + e.getMessage());
                Log.e(DataUtils.class.getSimpleName(), "Fail to create file: " + file.getAbsolutePath());
//                Toast.makeText(MyApp.getInstance(), "Fail to create file: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
