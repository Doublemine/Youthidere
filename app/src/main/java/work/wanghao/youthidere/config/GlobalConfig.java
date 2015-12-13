package work.wanghao.youthidere.config;

import android.os.Environment;

import java.io.File;

/**
 * Created by wangh on 2015-12-11-0011.
 */
public class GlobalConfig {
    public static final int PAHTO_WITH_CAMERA = 123;
    public static final int CHOOSE_PHOTO = 124;
    public static final String DIR_NAME = "XiaMo";
    public static final String TEMP_CAMERA_IMG="tempImage.jpg";
    public static final int RESULT_OK = -1;
    public static final String APP_IMGAGE_STORE_DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DIR_NAME+File.separator;
    public static final String BROWSER_UA = "Qingniantuzhai/com.qingniantuzhai.ios (3; OS Version 9.1 (Build 13B143))";

    
}
