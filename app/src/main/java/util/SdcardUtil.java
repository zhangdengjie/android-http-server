package util;

import android.os.Environment;

import java.io.File;

public class SdcardUtil {
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) { //判断sd卡是否存在
            return true;
        }
        return false;
    }

    /**
     * 判断sd卡是否存在以及返回SD卡路径 <br/>
     * 作者 ：dengjie <br/>
     * created at 2015/11/04 10:20
     */
    public static File getSDPath() {
        File sdDir = null;
        boolean sdCardExist = isExternalStorageWritable();// 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        return sdDir;
    }
}
