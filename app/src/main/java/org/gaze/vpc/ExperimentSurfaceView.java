/*******************************************************************************
 * Copyright (C) 2023 Gancheng Zhu
 * Email: psycho@zju.edu.cn
 ******************************************************************************/

package org.gaze.vpc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import org.gaze.vpc.listener.ExperimentListener;
import org.gaze.tracker.bean.GazeSample;
import org.gaze.tracker.core.GazeTracker;

public class ExperimentSurfaceView extends SurfaceView {
    volatile boolean debugAble = true, hasGaze = false, isDraw = false;
    float x, y;
    String saveDir;
    ExperimentListener experimentListener;

    GazeTracker tracker;

    public ExperimentSurfaceView(Context context) {
        this(context, null);
    }

    public ExperimentSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExperimentSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ExperimentSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        tracker = GazeTracker.getInstance();
    }

    public boolean isHasGaze() {
        return hasGaze;
    }

    public void setHasGaze(boolean hasGaze) {
        this.hasGaze = hasGaze;
    }

    public void setGaze(float filteredX, float filteredY) {
        x = filteredX;
        y = filteredY;
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public void setExperimentListener(ExperimentListener experimentListener) {
        this.experimentListener = experimentListener;
    }

    public ExperimentListener getExperimentListener() {
        return experimentListener;
    }

    public void setDebugAble(boolean debugAble) {
        this.debugAble = debugAble;
    }

    public boolean isDebugAble() {
        return debugAble;
    }
}
