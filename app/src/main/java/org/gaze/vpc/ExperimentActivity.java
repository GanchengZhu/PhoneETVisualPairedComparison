/*******************************************************************************
 * Copyright (C) 2023 Gancheng Zhu
 * Email: psycho@zju.edu.cn
 ******************************************************************************/

package org.gaze.vpc;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import org.gaze.vpc.listener.ExperimentListener;
import org.gaze.vpc.utils.FileUtil;
import org.gaze.vpc.utils.SharedPreferencesUtils;
import org.gaze.tracker.bean.FaceSample;
import org.gaze.tracker.bean.GazeSample;
import org.gaze.tracker.core.GazeTracker;
import org.gaze.tracker.enumeration.TrackingState;
import org.gaze.tracker.helper.IOUtils;
import org.gaze.tracker.listener.FaceCallback;
import org.gaze.tracker.listener.GazeCallback;
import org.gaze.tracker.ui.BaseActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExperimentActivity extends BaseActivity implements GazeCallback, ExperimentListener, FaceCallback {

    private final String TAG = getClass().getSimpleName();
    private final SimpleDateFormat msDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.CHINA);
    private final SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.CHINA);
    float prepareInterval;
    ExperimentSurfaceView surfaceView;
    StringBuilder dataBuffer;
    private String userId;
    private String saveDir;
    //    private StringBuilder sbTrialInfo;
//    private StringBuilder sbBtnData;
    private GazeTracker gazeTracker;
    private ExecutorService executorService;

    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mConstraintLayout = findViewById(R.id.constraint_layout);
        userId = SharedPreferencesUtils.getString("user_id", "ABC");
        prepareInterval = 2.0f;
//        sbTrialInfo = new StringBuilder();
//        sbBtnData = new StringBuilder();
//        sbBtnData.append("trial_id").append(",")
//                .append("marker_digit").append(",")
//                .append("response_digit").append(",")
//                .append("response_time").append("\n");


        long trialSessionTime = System.currentTimeMillis();
        // init surfaceView
        switch (SharedPreferencesUtils.getString("task_name", "ABC")) {
            case "VPCTask":
                try {
                    surfaceView = new VisualPairComparisonSurfaceView(this);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }

        saveDir = getExternalFilesDir("").getAbsolutePath() + "/data/" + userId + "/" +
                SharedPreferencesUtils.getString("task_name", "ABC") + "_"
                + fileNameDateFormat.format(trialSessionTime);

        FileUtil.createOrExistsDir(saveDir);
        surfaceView.setSaveDir(saveDir);
        surfaceView.setExperimentListener(this);
        surfaceView.setDebugAble(SharedPreferencesUtils.getBoolean("enable_debug", true));

        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout_container);
        surfaceView.setId(View.generateViewId());
        constraintLayout.addView(surfaceView);

        // 创建约束，使SurfaceView填充整个父布局
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        // 设置 SurfaceView 的约束，使其填充整个父布局
        constraintSet.constrainWidth(surfaceView.getId(), ConstraintSet.MATCH_CONSTRAINT);
        constraintSet.constrainHeight(surfaceView.getId(), ConstraintSet.MATCH_CONSTRAINT);

        constraintSet.connect(surfaceView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(surfaceView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(surfaceView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(surfaceView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.applyTo(constraintLayout);

        gazeTracker = GazeTracker.getInstance();
//        gazeTracker.setErrorBarVisible(SharedPreferencesUtils.getBoolean("enable_debug", true));
        gazeTracker.addCallbacks(this);
        gazeTracker.startSampling();

        dataBuffer = new StringBuilder();
        dataBuffer.append("timestamp").append(",")
                .append("trackingState")
                .append(",")
                .append("hasCalibrated")
                .append(",").append("rawX")
                .append(",").append("rawY")
                .append(",").append("calibratedX")
                .append(",").append("calibratedY")
                .append(",").append("filteredX")
                .append(",").append("filteredY")
                .append(",").append("leftDistance")
                .append(",").append("rightDistance")
                .append("\n");

        executorService = Executors.newCachedThreadPool();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gazeTracker.stopSampling();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onExpEnd() {
        gazeTracker.stopSampling();
        surfaceView.setHasGaze(false);
        IOUtils.stringToFile(dataBuffer.toString(), saveDir + "/MobileEyeTrackingRecord.csv");

        String jsonSubjectInfo = "{\"subject_id\": \"%s\", \"subject_age\":\"%s\"," + "\"subject_gender\": \"%s\", \"subject_group\": \"%s\"}";
        jsonSubjectInfo = String.format(jsonSubjectInfo, SharedPreferencesUtils.getString("user_id", "ABC"), SharedPreferencesUtils.getString("subject_age", "20"), SharedPreferencesUtils.getString("subject_gender", "male"), SharedPreferencesUtils.getString("subject_group", "control"));

        IOUtils.stringToFile(jsonSubjectInfo, saveDir + "/SubjectInformation.json");
        gazeTracker.removeCallbacks(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_experiment;
    }

    @Override
    public void onGaze(GazeSample gazeSample) {
        Log.w(TAG, "face timestamp: " + gazeSample.getTimestamp());
        if (gazeSample.getTrackingState() == TrackingState.SUCCESS) {
            surfaceView.setHasGaze(true);
            surfaceView.setGaze(gazeSample.getFilteredX(), gazeSample.getFilteredY());
        } else {
            surfaceView.setHasGaze(false);
        }
        dataBuffer.append(gazeSample.getTimestamp()).append(",")
                .append(gazeSample.getTrackingState().getValue())
                .append(",").append(gazeSample.isHasCalibrated() ? 1 : 0)
                .append(",").append(gazeSample.getRawX())
                .append(",").append(gazeSample.getRawY())
                .append(",").append(gazeSample.getCalibratedX())
                .append(",").append(gazeSample.getCalibratedY())
                .append(",").append(gazeSample.getFilteredX())
                .append(",").append(gazeSample.getFilteredY())
                .append(",").append(gazeSample.getLeftDistance())
                .append(",").append(gazeSample.getRightDistance())
                .append("\n");
    }

    @Override
    public void onFace(FaceSample faceSample) {
        Log.d(TAG, "face timestamp: " + faceSample.getTimestamp());
    }


}

