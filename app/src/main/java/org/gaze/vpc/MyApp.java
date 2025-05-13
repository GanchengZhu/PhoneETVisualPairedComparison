/*******************************************************************************
 * Copyright (C) 2023 Gancheng Zhu
 * Email: psycho@zju.edu.cn
 ******************************************************************************/

package org.gaze.vpc;

import android.app.Application;


public class MyApp extends Application {
    private static MyApp app = null;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
//        CrashReport.initCrashReport(getApplicationContext(), "da5697849b", true);
    }

    public static MyApp getInstance(){
        return app;
    }
}

