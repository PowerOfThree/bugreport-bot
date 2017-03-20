import java.util.Date;

/**
 * Created by sagdatk on 3/18/17.
 */
public class Report {
    private long date;
    private String thread;
    private String message;
    private String stacktrace;

    public Report(long date, String thread, String message, String stacktrace) {
        this.date = date;
        this.thread = thread;
        this.message = message;
        this.stacktrace = stacktrace;
    }

    public long getDate() {
        return date;
    }

    public String getThread() {
        return thread;
    }

    public String getMessage() {
        return message;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    @Override
    public String toString() {
        return String.format("%s%n%s%n%s%n%s",
                date, thread, message, stacktrace);
    }
}
