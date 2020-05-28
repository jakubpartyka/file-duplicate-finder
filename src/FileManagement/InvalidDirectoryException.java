package FileManagement;

public class InvalidDirectoryException extends Throwable {
    InvalidDirectoryException() {
        super("\"Incorrect initial directory provided: directory is NULL");
    }

    InvalidDirectoryException(String empty) {
        super("\"Incorrect initial directory provided: no directories provided");
    }
}
