package FileManagement;

public class InvalidDirectoryException extends Exception {
    InvalidDirectoryException() {
        super("\"Incorrect initial directory provided: directory is NULL");
    }

    InvalidDirectoryException(String empty) {
        super("\"Incorrect initial directory provided: no directories provided");
    }
}
