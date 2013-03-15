package pacandroid.android;

import android.util.Log;
import pacandroid.AppLog;

public class AndroidLog implements AppLog.Log {

    @Override
    public void log(String text) {
        Log.i("log", text);
    }
}
