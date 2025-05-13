package org.gaze.vpc.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;
import java.util.List;

public class ImgParseUtils {

    @SuppressLint("DefaultLocale")
    public static void parseImg(String absDir, List<Integer> frameList, List<String> positionList, float duration) {
        int total = new File(absDir).listFiles().length; // 文件夹下总的图片数量。
        Log.i("ImgParseUtils", String.valueOf(total));

        int validNum; // 有效帧的数量
        float rate = 30f; // 如果没有传入视频的持续时间，就按每秒三十帧
        if (duration != 0f) {
            rate = total / duration; // 算出视频的帧率
        }

        validNum = Math.round(frameList.get(frameList.size() - 1) * rate);// 96 * 帧率得出有效帧的大小

        int end = total - Math.round(Float.parseFloat(SharedPreferencesUtils.getString(Constants.FINISH_DELAY_FIXED, "3")) * rate);
        int start = end - validNum; // 再减去有效帧的大小，得出marker出现的那一帧

        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilderOld = new StringBuilder();
        String tmp = null;
        int count = 1;
        int frame = 1;
        int second = 0;
        File newFile, oldFile;
//        int myEnd = start + 96 * 30; // 这个地方我定义了30帧每秒，因为给保存图片不可能是小数
        for (int i = start ; i < end; i++) { // 这个
            //$absDir/frame_$frame_$position.jpg
            stringBuilder.append(absDir);
            stringBuilder.append("/s_");
            stringBuilder.append(String.format("%03d", second));
            stringBuilder.append("_f_");
            stringBuilder.append(String.format("%02d",frame));

            stringBuilder.append("_");
            stringBuilder.append(String.format("%05d",i));
            stringBuilder.append("_");
            tmp = searchPosition(second, frameList, positionList);
            if (tmp == null) {
                stringBuilder.append(searchPosition(second - 1, frameList, positionList));
                stringBuilder.append("_to_");
                stringBuilder.append(searchPosition(second + 1, frameList, positionList));
            } else {
                stringBuilder.append(tmp);
            }
            stringBuilder.append(".jpg");

            stringBuilderOld.append(absDir);
            stringBuilderOld.append("/discard_");
            stringBuilderOld.append(i);
            stringBuilderOld.append(".jpg");

            oldFile = new File(stringBuilderOld.toString());
            newFile = new File(stringBuilder.toString());
            if (oldFile.renameTo(newFile))
                Log.i("ImgParseUtils", stringBuilderOld + " 改名为 " + stringBuilder);
            frame++;
            stringBuilderOld.setLength(0);
            stringBuilder.setLength(0);
            if(count == Math.round((float) (second + 1) * rate)){
                second += 1;
                frame = 1;
            }
            count++;
//            if (frame == 31) frame = 1;
        }
    }


    public static String searchPosition(int id, List<Integer> frameList, List<String> positionList) {
        for (int i = 0; i < frameList.size() - 1; i++) {
            if (id >= frameList.get(i) && id < frameList.get(i + 1)) {
                return frameList.get(i + 1) - frameList.get(i) >= 3 ? positionList.get(i / 2) : null;
            }
        }return null;
    }
}
