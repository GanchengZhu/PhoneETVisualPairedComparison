package org.gaze.vpc.utils;

public interface MotionListener {
    void onMotionEnd();

    void onMotionStart();

    void onMotionInterrupt();
}
