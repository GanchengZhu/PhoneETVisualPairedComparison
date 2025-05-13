package org.gaze.vpc.utils;

import android.Manifest;


public class Constants {

    public static final String TRIAL_ID = "trial_id";
    public static final String CONFIG = "preferences";

    public static final String PIVOT_Y = "pivot_y";
    public static final String PIVOT_X = "pivot_x";
    public static final String SCALE = "scale";
    public static final String RESOLUTION_HEIGHT = "resolution_height";
    public static final String ASPECT_RATIO = "aspect_ratio";
    public static final String PORT = "port";
    public static final String PROTOCOL = "protocol";
    public static final String USER_ID = "user_id";
    public static final String RESOLUTION_WIDTH = "resolution_width";
    public static final String DEBUG_STATE = "debug_state";
    public static final String DEBUG_FRONT_SIZE = "debug_front_size";
    //    public static final String CALIBRATION_STATE = "calibration_state";
    public static final String FINISH_DELAY = "finish_delay";
    //    public static final String DELAY_FRAMES = "delay_frames";
    public static final String CA_FRAME_STATE = "ca_frame_state";
    public static final String FINISH_DELAY_FIXED = "finish_delay_fixed";
    public static final String PREVIEW_STATE = "preview_state";
    public static final String PARSE_WAY = "parse_way";
    public static final String IS_PORTRAIT = "is_portrait";
    public static final String DEVICE_DISTANCE = "device_distance";
    public static final String MOTION_TYPE = "motion_type";
    public static final String COLLECT_UDP_DATA = "collect_upd_data";
    public static final int REQUEST_CODE_PERMISSIONS = 101;

    public static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    public static final String FRAME_DROP_FREE_VIEWING = "frame_drop_free_viewing";
    public static final String PICTURE_DURATION_FREE_VIEWING = "picture_duration_free_viewing";
    public static final String STAY_INTERVAL_FREE_VIEWING = "stay_interval_free_viewing";
    public static final String FRAME_DROP_FIXATION_STABILITY = "frame_drop_fixation_stability";
    public static final String RANDOM_COUNT_FIXATION_STABILITY = "random_count_fixation_stability";
    public static final String STAY_INTERVAL_FIXATION_STABILITY = "stay_interval_fixation_stability";
    public static final String FRAME_DROP_HORIZONTAL_SINUSOID = "frame_drop_horizontal_sinusoid";
    public static final String FRAME_DROP_VERTICAL_SINUSOID = "frame_drop_vertical_sinusoid";
    public static final String FREQ_X_HORIZONTAL_SINUSOID = "freq_X_horizontal_sinusoid";
    public static final String FREQ_Y_HORIZONTAL_SINUSOID = "freq_Y_horizontal_sinusoid";
    public static final String PHASE_X_HORIZONTAL_SINUSOID = "phase_X_horizontal_sinusoid";
    public static final String FREQ_X_VERTICAL_SINUSOID = "freq_X_vertical_sinusoid";
    public static final String FREQ_Y_VERTICAL_SINUSOID = "freq_Y_vertical_sinusoid";
    public static final String PHASE_Y_VERTICAL_SINUSOID = "phase_Y_vertical_sinusoid";
    public static final String STAY_INTERVAL_HORIZONTAL_SINUSOID = "stay_interval_horizontal_sinusoid";
    public static final String STAY_INTERVAL_VERTICAL_SINUSOID = "stay_interval_vertical_sinusoid";
    public static final String FRAME_DROP_SMOOTH_PURSUIT = "frame_drop_smooth_pursuit";
    public static final String FREQ_X_SMOOTH_PURSUIT = "freq_X_smooth_pursuit";
    public static final String FREQ_Y_SMOOTH_PURSUIT = "freq_Y_smooth_pursuit";
    public static final String PHASE_X_SMOOTH_PURSUIT = "phase_X_smooth_pursuit";
    public static final String PHASE_Y_SMOOTH_PURSUIT = "phase_Y_smooth_pursuit";
    public static final String STAY_INTERVAL_SMOOTH_PURSUIT = "stay_interval_smooth_pursuit";
    public static final String FIXATION_DURATION_FREE_VIEWING = "fixation_duration_free_viewing";
}