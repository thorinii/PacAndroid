package pacandroid;

public class AppLog {

    private static Log log;

    public static void init(Log log) {
        AppLog.log = log;
    }

    public static void l(String text) {
        if (log == null)
            throw new IllegalStateException("Not Initialised");
        log.log(text);
    }

    public static interface Log {

        public void log(String text);
    }
}
