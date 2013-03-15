package pacandroid.util;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

public class AppLog {

    private static Log log;

    private static void init() {
        if (Gdx.app.getType().equals(ApplicationType.Android)) {
            try {
                log = (Log) Class
                        .forName(
                        "pacandroidandroid.AndroidLog")
                        .newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            log = new DesktopLog();
        }
    }

    public static void l(String text) {
        if (log == null)
            init();
        log.log(text);
    }

    public static interface Log {

        public void log(String text);
    }

    static class DesktopLog implements Log {

        @Override
        public void log(String text) {
            System.out.println(text);
        }
    }
}
