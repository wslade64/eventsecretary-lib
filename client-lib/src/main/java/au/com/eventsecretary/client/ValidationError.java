package au.com.eventsecretary.client;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class ValidationError {
    public static class ErrorSource {
        private String pointer;
        private String parameter;

        public String getPointer() {
            return pointer;
        }

        public void setPointer(String pointer) {
            this.pointer = pointer;
        }

        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }
    }

    private String code;
    private String title;
    private ErrorSource source;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ErrorSource getSource() {
        return source;
    }

    public void setSource(ErrorSource source) {
        this.source = source;
    }
}
