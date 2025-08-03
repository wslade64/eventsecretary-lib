package au.com.eventsecretary.inport;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class FileError {
    private int row;
    private String message;

    public FileError(int row, String message) {
        this.row = row;
        this.message = message;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
