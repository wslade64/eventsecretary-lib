package au.com.eventsecretary;

/**
 * @author wslad
 */
public class LineBuilder {
    private final String separator;

    private StringBuilder builder = new StringBuilder();

    public LineBuilder() {
        separator = "\n";
    }

    public LineBuilder(String separator) {
        this.separator = separator;
    }

    public int length() {
        return builder.length();
    }

    public LineBuilder append(String text) {
        if (text == null) {
            return this;
        }
        if (builder.length() > 0) {
            builder.append(separator);
        }
        builder.append(text);
        return this;
    }

    public String toString() {
        return builder.toString();
    }
}
