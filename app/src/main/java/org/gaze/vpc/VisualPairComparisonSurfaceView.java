package org.gaze.vpc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.gaze.vpc.listener.ExperimentListener;
import org.gaze.vpc.utils.DataUtils;

enum ExperimentPhase {
    FAMILIAR, TEST
}

enum ExpIntro {
    FAMILIAR, TEST
}

public class VisualPairComparisonSurfaceView extends ExperimentSurfaceView implements SurfaceHolder.Callback, Runnable, View.OnClickListener {
    // Debug相关的变量
    private final String TAG = getClass().getSimpleName();
    private final float centerTextSize = 64;
    private final float fixationSize = 64;
    private final float textSize = 64;
    private final float pictureDuration = 5;
    private final float fixationDuration = 1;
    List<String> pathList = new ArrayList<>();
    //    MediaPlayer mediaPlayer;
    // 位置
    private float x;
    private float y;
    private volatile boolean debugAble;
    private SurfaceHolder holder;
    private volatile boolean isDraw = false;// 控制绘制的开关
    private Paint gazePaint;
    private Canvas mCanvas;
    private Paint cameraImgPaint;
    private Paint debugTextPaint;
    private Paint centerTextPaint;
    private Paint fixationPaint;
    private Paint fpsTextPaint;
    private long lastTimeStamp;
    //    private Bitmap mCacheBitmap;
    //    private float mScale;
    private volatile boolean hasGaze = false;
    private volatile float fps;
    private volatile long startTime;
    // 每张图片的停留的时间
    private volatile float interval;
    private Queue<Long> timeStampRecord;
    private float familiarBlockDuration;
    //    private List<Bitmap> familiarizationRightBitmaps;
    private float testBlockDuration;
    private List<Bitmap> familiarizationBitmaps;
    private List<Bitmap> testLeftBitmaps;
    private List<Bitmap> testRightBitmaps;
    private Boolean[] familiarizationInLeft;
    private int selfWidth;
    private int selfHeight;
    //    private long lastTrialStartTimeStamp;
    private int testTrialId = 0;
    private int familiarizationTrialId = 0;
    // 实验时间相关的变量
    private long expStartTimeStamp;
    private long expEndTimeStamp;
    private long familiarizationIntroStartTimeStamp;
    private long familiarizationIntroEndTimeStamp;
    private long familiarizationTrialStartTimeStamp;
    private long familiarizationTrialEndTimeStamp;
    private long calibrationIntroStartTimeStamp;
    private long calibrationIntroEndTimeStamp;
    private long testIntroStartTimeStamp;
    private long testIntroEndTimeStamp;
    private long testTrialStartTimeStamp;
    private long testTrialEndTimeStamp;
    private Bitmap familiarizationIntroBitmap;
    private Bitmap calibrationIntroBitmap;
    private Bitmap testIntroBitmap;
    private Bitmap endExpBitmap;
    // save dir
    private String saveDir;
    private ExperimentListener experimentListener;
    private volatile boolean expOver;
    private List<Long> familiarizationStimuliShowTimeStampList;
    private List<Long> testStimuliShowTimeStampList;
    private List<Long> familiarizationFixationShowTimeStampList;
    private List<Long> testFixationShowTimeStampList;

    public VisualPairComparisonSurfaceView(Context context) throws IOException {
        super(context);
        init();
        initPictures();

    }


    public VisualPairComparisonSurfaceView(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        init();
        initPictures();
    }


    private void init() {
        Log.d(TAG, "init");
        holder = this.getHolder();
        holder.addCallback(this);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int stroke = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics);
        gazePaint = new Paint();
        gazePaint.setAntiAlias(true);
        gazePaint.setDither(true);
        gazePaint.setColor(Color.GREEN);
        gazePaint.setStrokeWidth(stroke);
        gazePaint.setStyle(Paint.Style.STROKE);

        cameraImgPaint = new Paint();
        cameraImgPaint.setAlpha(255 * 2 / 10);

        debugTextPaint = new Paint();
        debugTextPaint.setColor(Color.RED);
        debugTextPaint.setAntiAlias(true);
        debugTextPaint.setStrokeWidth(50);
        debugTextPaint.setTextSize(textSize);

        centerTextPaint = new Paint();
        centerTextPaint.setColor(Color.BLACK);
        centerTextPaint.setAntiAlias(true);
        centerTextPaint.setTextAlign(Paint.Align.CENTER);
        centerTextPaint.setStrokeWidth(50);
        centerTextPaint.setTextSize(centerTextSize);

        fixationPaint = new Paint();
        fixationPaint.setColor(Color.WHITE);
        fixationPaint.setAntiAlias(true);
        fixationPaint.setStrokeWidth(50);
        fixationPaint.setTextSize(fixationSize);
//        fixationPaint.setTextAlign(Paint.Align.CENTER);

        fpsTextPaint = new Paint();
        fpsTextPaint.setColor(Color.WHITE);
        fpsTextPaint.setAntiAlias(true);
        fpsTextPaint.setTextSize(36);

        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);
        holder.setFormat(PixelFormat.TRANSPARENT);
        timeStampRecord = new LinkedList<>();
        expStartTimeStamp = System.nanoTime();
        setOnClickListener(this);

    }


    private void initPictures() throws IOException {
        familiarizationBitmaps = new ArrayList<>();

        testLeftBitmaps = new ArrayList<>();
        testRightBitmaps = new ArrayList<>();

        String[] familiarizationStimuli = {"stims/russian.jpg", "stims/thread.png", "stims/weight.png", "stims/mellon.jpg", "stims/pan.png", "stims/box.jpg", "stims/brush.png", "stims/lemon.png", "stims/tools.jpg", "stims/book.jpg", "stims/toycat.jpg", "stims/timer.jpg", "stims/toytruck.jpg", "stims/balloon.png", "stims/toydino.jpg", "stims/cookie.jpg", "stims/bear.png", "stims/tincan.png", "stims/blanket.png", "stims/basket.png"};


        for (String stimulusPath : familiarizationStimuli) {
            familiarizationBitmaps.add(BitmapFactory.decodeStream(getContext().getAssets().open(stimulusPath)));
//            familiarizationRightBitmaps.add(BitmapFactory.decodeStream(getContext().getAssets().open(stimulusPath)));
        }

        String[] testStimuli = {"stims/basket.png", "stims/cupwhite.jpg", "stims/blanket.png", "stims/coin.jpg", "stims/tincan.png", "stims/clock.jpg", "stims/bear.png", "stims/paper.png", "stims/cookie.jpg", "stims/unameable-3.jpg", "stims/toydino.jpg", "stims/hat.png", "stims/balloon.png", "stims/unamable-1.jpg", "stims/toytruck.jpg", "stims/plant.png", "stims/timer.jpg", "stims/bird.jpg", "stims/toycat.jpg", "stims/toymice.jpg", "stims/book.jpg", "stims/drum.png", "stims/tools.jpg", "stims/threads.jpg", "stims/lemon.png", "stims/shell.png", "stims/brush.png", "stims/candle.jpg", "stims/box.jpg", "stims/duck.png", "stims/pan.png", "stims/jenga.png", "stims/mellon.jpg", "stims/stapler.png", "stims/weight.png", "stims/tape.jpg", "stims/thread.png", "stims/cup.png", "stims/russian.jpg", "stims/dice.png"};


        familiarizationInLeft = new Boolean[testStimuli.length / 2];

        long seed = 2023L; // Fixed seed for reproducibility
        Random random = new Random(seed);
        int[] targetArray = {-1, 1};

        for (int i = 0; i < testStimuli.length / 2; i++) {
            String familiarizationStimuliPath = testStimuli[i * 2];
            String novelStimuliPath = testStimuli[i * 2 + 1];

            int randomIndex = random.nextInt(2);
            int targetSide = targetArray[randomIndex];

            Log.i(TAG, familiarizationStimuliPath);
            Log.i(TAG, novelStimuliPath);
            if (targetSide == -1) {
                familiarizationInLeft[i] = true;
                testLeftBitmaps.add(BitmapFactory.decodeStream(getContext().getAssets().open(familiarizationStimuliPath)));
                testRightBitmaps.add(BitmapFactory.decodeStream(getContext().getAssets().open(novelStimuliPath)));
            } else {
                familiarizationInLeft[i] = false;
                testLeftBitmaps.add(BitmapFactory.decodeStream(getContext().getAssets().open(novelStimuliPath)));
                testRightBitmaps.add(BitmapFactory.decodeStream(getContext().getAssets().open(familiarizationStimuliPath)));
            }

        }

        familiarizationIntroBitmap = BitmapFactory.decodeStream(getContext().getAssets().open("instruction/familiar_intro.png"));
        testIntroBitmap = BitmapFactory.decodeStream(getContext().getAssets().open("instruction/test_intro.png"));
        endExpBitmap = BitmapFactory.decodeStream(getContext().getAssets().open("instruction/exp_end.png"));
        calibrationIntroBitmap = BitmapFactory.decodeStream(getContext().getAssets().open("instruction/calibration_intro.png"));

        familiarBlockDuration = familiarizationBitmaps.size() * (pictureDuration + fixationDuration);
        testBlockDuration = testLeftBitmaps.size() * (pictureDuration + fixationDuration);

//        familiarizationTrialId = familiarizationBitmaps.size();
//        testTrialId = testLeftBitmaps.size();

        familiarizationStimuliShowTimeStampList = new ArrayList<>();
        testStimuliShowTimeStampList = new ArrayList<>();

        familiarizationFixationShowTimeStampList = new ArrayList<>();
        testFixationShowTimeStampList = new ArrayList<>();

        for (int i = 0; i < familiarizationBitmaps.size(); i++) {
            familiarizationStimuliShowTimeStampList.add(0L);
            familiarizationFixationShowTimeStampList.add(0L);
        }
        for (int i = 0; i < testLeftBitmaps.size(); i++) {
            testStimuliShowTimeStampList.add(0L);
            testFixationShowTimeStampList.add(0L);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDraw = true;
        selfHeight = getHeight();
        selfWidth = getWidth();
        new Thread(this).start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDraw = false;
//        mediaPlayer.release();
//        release();
    }

    @Override
    public void run() {
        Looper.prepare();
        while (isDraw) {
            drawUI();
        }
    }

    public void drawUI() {
        try {
            mCanvas = holder.lockCanvas();
            if (mCanvas != null) {
                // Draw Background
                mCanvas.drawColor(Color.GRAY);
                drawExp();
                drawDebug();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (holder != null) {
                    holder.unlockCanvasAndPost(mCanvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void drawDebug() {
        if (debugAble) {
            if (expEndTimeStamp == 0L) {
                mCanvas.drawText(
                        "Delta Time: " + String.format(Locale.CHINA, "%.4f", (System.nanoTime() - expStartTimeStamp) / 1E9F),
                        selfWidth * 1f / 4f, selfHeight * 1f / 2f, debugTextPaint);
            }
            if (timeStampRecord != null && fps != 0f) {
                mCanvas.drawText(String.format(Locale.CHINA, "FPS: %.2f", fps),
                        50, 50, fpsTextPaint);
            }
            if (hasGaze) mCanvas.drawCircle(x, y, 10, gazePaint);
        }
    }

    private void drawIntroduction(ExpIntro expIntro) {
        switch (expIntro) {
            case FAMILIAR:
                drawIntroWithBitmap(familiarizationIntroBitmap);
                break;
            case TEST:
                drawIntroWithBitmap(testIntroBitmap);
                break;
            default:
                break;
        }
    }


    private void drawIntroWithBitmap(Bitmap bitmap) {
        int canvasWidth = mCanvas.getWidth();
        int canvasHeight = mCanvas.getHeight();

        // 计算缩放比例，选择宽度和高度的较小值作为基准
        float scale = Math.min((float) canvasWidth / bitmap.getWidth(), (float) canvasHeight / bitmap.getHeight());

        // 创建源矩形，用于指定要绘制的位图的区域（整个位图）
        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        // 创建目标矩形，用于指定位图在画布上的位置和大小
        Rect dstRect = new Rect(
                (canvasWidth - (int) (scale * bitmap.getWidth())) / 2,
                (canvasHeight - (int) (scale * bitmap.getHeight())) / 2,
                (canvasWidth + (int) (scale * bitmap.getWidth())) / 2,
                (canvasHeight + (int) (scale * bitmap.getHeight())) / 2
        );

        // 使用画布对象绘制缩放后的位图
        mCanvas.drawBitmap(bitmap, srcRect, dstRect, null);
    }


    public synchronized void drawTrial(ExperimentPhase experimentPhase) {
        int trialId = getTrialId(false, experimentPhase);
//        Log.d(TAG, "trial id: " + trialId);
        List<Long> showTimeStamp = getShowTimeStampList(experimentPhase);
        List<Bitmap> bitmaps;
        if (experimentPhase == ExperimentPhase.FAMILIAR) {
            bitmaps = familiarizationBitmaps;
        } else {
            bitmaps = testLeftBitmaps;
        }

        List<Long> fixationShowTimeStamp = getFixationShowTimeStampList(experimentPhase);

        if (trialId == bitmaps.size() && experimentPhase == ExperimentPhase.TEST) {
//            Log.d(TAG, "TEST trial shows");
            testTrialEndTimeStamp = System.nanoTime();
            return;
        } else if (trialId == bitmaps.size() && experimentPhase == ExperimentPhase.FAMILIAR) {
//            Log.d(TAG, "FAMILIAR trial ends");
//            Log.d(TAG, "bitmap size: " + bitmaps.size());
            familiarizationTrialEndTimeStamp = System.nanoTime();
            return;
        }

        long nowTime = System.nanoTime();
        long lastTrialStartTimeStamp = experimentPhase == ExperimentPhase.TEST ?
                testTrialStartTimeStamp : familiarizationTrialStartTimeStamp;

        float elapsedTime = (nowTime - lastTrialStartTimeStamp) / 1E9F;
//        Log.d(TAG, "elapsed time: " + elapsedTime);
//        Log.d(TAG, "condition: " + (elapsedTime - (trialId + 1) * (pictureDuration + fixationDuration)));
        if (elapsedTime - (trialId + 1) * (pictureDuration + fixationDuration) > 0) {
//            Log.d(TAG, "condition: YES");
            trialId = getTrialId(true, experimentPhase);
        }
        float trialElapsedTime = elapsedTime - trialId * (pictureDuration + fixationDuration);
//        Log.d(TAG, "trial elapsed time: " + trialElapsedTime);
        if (trialElapsedTime <= fixationDuration) {
            // draw fixation
            if (trialId != fixationShowTimeStamp.size() && fixationShowTimeStamp.get(trialId) == 0) {
                fixationShowTimeStamp.set(trialId, nowTime);
//                mediaPlayer.start();
            }

            String text = "+";
            float textWidth = fixationPaint.measureText(text);
            float textHeight = fixationPaint.getTextSize();
            mCanvas.drawText(text, selfWidth / 2f - textWidth / 2, selfHeight / 2f - textHeight / 2, fixationPaint);
        }

        if (trialElapsedTime <= (pictureDuration + fixationDuration) && trialElapsedTime > fixationDuration) {
            if (trialId != showTimeStamp.size() && showTimeStamp.get(trialId) == 0) {
                showTimeStamp.set(trialId, nowTime);
            }
            // draw picture
            if (experimentPhase == ExperimentPhase.FAMILIAR) {
                Bitmap bitmap = bitmaps.get(trialId);
                mCanvas.drawBitmap(bitmap, selfWidth / 2f - bitmap.getWidth() / 2f, selfHeight * 1f / 10f, null);
                mCanvas.drawBitmap(bitmap, selfWidth / 2f - bitmap.getWidth() / 2f, selfHeight * 6f / 10f, null);
            } else {
                Bitmap leftBitmap = testLeftBitmaps.get(trialId);
                Bitmap rightBitmap = testRightBitmaps.get(trialId);
                mCanvas.drawBitmap(leftBitmap, selfWidth / 2f - leftBitmap.getWidth() / 2f, (selfHeight / 2f - leftBitmap.getHeight()) / 2f , null);
                mCanvas.drawBitmap(rightBitmap, selfWidth / 2f - rightBitmap.getWidth() / 2f, selfHeight / 2f + (selfHeight / 2f - leftBitmap.getHeight()) / 2f , null);
            }
        }
    }

    private List<Long> getShowTimeStampList(ExperimentPhase experimentPhase) {
        if (experimentPhase == ExperimentPhase.FAMILIAR) {
            return familiarizationStimuliShowTimeStampList;
        } else {
            return testStimuliShowTimeStampList;
        }
    }

    private List<Long> getFixationShowTimeStampList(ExperimentPhase experimentPhase) {
        if (experimentPhase == ExperimentPhase.FAMILIAR) {
            return familiarizationFixationShowTimeStampList;
        } else {
            return testFixationShowTimeStampList;
        }
    }

    private int getTrialId(boolean increment, ExperimentPhase experimentPhase) {
        int trialId;
        if (experimentPhase == ExperimentPhase.FAMILIAR) {
            trialId = !increment ? familiarizationTrialId : ++familiarizationTrialId;
        } else {
            trialId = !increment ? testTrialId : ++testTrialId;
        }
        return trialId;
    }

//    private List<Bitmap> getTrialBitmaps(ExperimentPhase experimentPhase) {
//        if (experimentPhase == ExperimentPhase.test) {
////            return testBitmaps;
//        } else {
////            return familiarizationizationBitmaps;
//        }
//    }

    private void drawExp() {
        if (expStartTimeStamp == 0L) {
            return;
        }

        if (expStartTimeStamp != 0L && familiarizationIntroStartTimeStamp == 0L) {
//            Log.d(TAG, "init familiarization intro");
            familiarizationIntroStartTimeStamp = System.nanoTime();
        }

        // familiarization intro show
        if (familiarizationIntroStartTimeStamp != 0L && familiarizationIntroEndTimeStamp == 0L) {
            drawIntroduction(ExpIntro.FAMILIAR);
            return;
        }

        //init familiarization trial
        if (familiarizationIntroStartTimeStamp != 0L && familiarizationIntroEndTimeStamp != 0L && familiarizationTrialStartTimeStamp == 0L) {
//            Log.d(TAG, "init familiarization trial");
            familiarizationTrialStartTimeStamp = System.nanoTime();
        }

        // familiarization trial show
        if (familiarizationTrialStartTimeStamp != 0L && familiarizationTrialEndTimeStamp == 0L) {
//            Log.d(TAG, "FAMILIAR trial shows");
            drawTrial(ExperimentPhase.FAMILIAR);
            return;
        }

        // init test intro
        if (familiarizationTrialStartTimeStamp != 0L && familiarizationTrialEndTimeStamp != 0L && testIntroStartTimeStamp == 0L) {
//            Log.d(TAG, "init test intro");
            testIntroStartTimeStamp = System.nanoTime();
        }

        // test intro show
        if (testIntroStartTimeStamp != 0L && testIntroEndTimeStamp == 0L) {
            drawIntroduction(ExpIntro.TEST);
            return;
        }

        // init test trial
        if (testIntroStartTimeStamp != 0L && testIntroEndTimeStamp != 0L && testTrialStartTimeStamp == 0L) {
            testTrialStartTimeStamp = System.nanoTime();
        }

        // test trial show
        if (testTrialStartTimeStamp != 0L && testTrialEndTimeStamp == 0L) {
            drawTrial(ExperimentPhase.TEST);
            return;
        }

        // end the exp
        if (testTrialStartTimeStamp != 0L && testTrialEndTimeStamp != 0L && expEndTimeStamp == 0L) {
            expEndTimeStamp = System.nanoTime();

            saveExpData();
//            expOver = true;
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(() -> {
                if (experimentListener != null) experimentListener.onExpEnd();
                expOver = true;
                Log.d(TAG, "expOver is true");

            }, 2, TimeUnit.SECONDS);
            release();
        }

        if (expOver) {
            Log.d(TAG, "exp is ending");
            drawIntroWithBitmap(endExpBitmap);
//            float textWidth = fixationPaint.measureText("保持头部不动，");
//            float textHeight = fixationPaint.getTextSize();
//            mCanvas.drawText("保持头部不动，", mCanvas.getWidth() / 2f - textWidth / 2, mCanvas.getHeight() / 2f - textHeight / 2, fixationPaint);
//            mCanvas.drawText("按空格键进行下一步。", mCanvas.getWidth() / 2f - textWidth / 2 - 100, mCanvas.getHeight() / 2f + textHeight / 2, fixationPaint);
        }
    }

    private void saveExpData() {
        String saveData = "{" +
                "\"expStartTimeStamp\": " + expStartTimeStamp + ", " +
                "\"expEndTimeStamp\": " + expEndTimeStamp + ", " +
                "\"familiarizationIntroStartTimeStamp\": " + familiarizationIntroStartTimeStamp + ", " +
                "\"familiarizationIntroEndTimeStamp\": " + familiarizationIntroEndTimeStamp + ", " +
                "\"familiarizationTrialStartTimeStamp\": " + familiarizationTrialStartTimeStamp + ", " +
                "\"familiarizationTrialEndTimeStamp\": " + familiarizationTrialEndTimeStamp + ", " +
                "\"testIntroStartTimeStamp\": " + testIntroStartTimeStamp + ", " +
                "\"testIntroEndTimeStamp\": " + testIntroEndTimeStamp + ", " +
                "\"testTrialStartTimeStamp\": " + testTrialStartTimeStamp + ", " +
                "\"testTrialEndTimeStamp\": " + testTrialEndTimeStamp + ", " +
                "\"testShowTimeStampList\":" + testStimuliShowTimeStampList.toString() + "," +
                "\"familiarizationShowTimeStampList\":" + familiarizationStimuliShowTimeStampList.toString() + "," +
                "\"testFixationShowTimeStampList\":" + testFixationShowTimeStampList.toString() + "," +
                "\"familiarizationFixationShowTimeStampList\":" + familiarizationFixationShowTimeStampList.toString() +
//                "," +
//                "\"imgPathList\":" + JSONObject.toJSONString(pathList) +
                "}";
        DataUtils.saveTextToPath(saveDir + "/vpc_timestamps.json", saveData);
    }

    public SurfaceHolder getSurfaceHolder() {
        return holder;
    }

    public void setExperimentListener(ExperimentListener experimentListener) {
        this.experimentListener = experimentListener;
    }

    public void setXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setTimeStamp(long timeStamp) {
        interval = timeStamp - lastTimeStamp;
        lastTimeStamp = timeStamp;
    }

    public boolean isDraw() {
        return isDraw;
    }

    public void setDraw(boolean draw) {
        isDraw = draw;
    }

    public void setGaze(float[] gazeResult) {
        if (gazeResult == null) {
            hasGaze = false;
            return;
        }
        hasGaze = true;
        x = gazeResult[0];
        y = gazeResult[1];
    }

    public void addTimeStamp(long timeStamp) {
        if (timeStampRecord.size() == 60) {
            timeStampRecord.poll();
            timeStampRecord.offer(timeStamp);
            long interval = timeStampRecord.peek() - ((LinkedList<Long>) timeStampRecord).getLast();
            fps = -timeStampRecord.size() / (interval / 1e9f);
        } else {
            timeStampRecord.offer(timeStamp);
        }
    }

//    public void setMat(Mat imgMat) {
//        new Thread(() -> {
//            if (mCacheBitmap != null) {
//                Utils.matToBitmap(imgMat, mCacheBitmap);
//                imgMat.release();
//            }
//        }).start();
//    }

//    public Bitmap getCacheBitmap() {
//        return mCacheBitmap;
//    }
//
//    public void setCacheBitmap(Bitmap mCacheBitmap) {
//        this.mCacheBitmap = mCacheBitmap;
//    }

//    public void setCacheBitmap(Mat mat) {
//        if (mat != null && mat.cols() != 0 && mat.rows() != 0) {
//            Utils.matToBitmap(mat, this.mCacheBitmap);
//            mat.release();
//        }
//    }

//    public float getScale() {
//        return mScale;
//    }
//
//    public void setScale(float mScale) {
//        this.mScale = mScale;
//    }

    public void release() {
        for (Bitmap bitmap : familiarizationBitmaps) {
            bitmap.recycle();
        }
        for (Bitmap bitmap : testLeftBitmaps) {
            bitmap.recycle();
        }

        for (Bitmap bitmap : testRightBitmaps) {
            bitmap.recycle();
        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "view was clicked!");
        synchronized (this) {
            if (familiarizationIntroStartTimeStamp != 0L && familiarizationIntroEndTimeStamp == 0L) {
                familiarizationIntroEndTimeStamp = System.nanoTime();
                Log.d(TAG, "set familiarizationIntroEndTimeStamp!");
            }

            if (testIntroStartTimeStamp != 0L && testIntroEndTimeStamp == 0L) {
                testIntroEndTimeStamp = System.nanoTime();
                Log.d(TAG, "set testIntroEndTimeStamp!");
            }

            if (expEndTimeStamp != 0L) {
                // 实验做完了，点击事件
//                GazeTracker.getInstance().gazeSetting();
            }
        }
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }

    public void setHasGaze(boolean hasGaze) {
        this.hasGaze = hasGaze;
    }
}