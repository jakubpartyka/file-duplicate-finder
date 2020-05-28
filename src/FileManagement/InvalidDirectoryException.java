package FileManagement;

import java.io.File;

public class InvalidDirectoryException extends Throwable {
    InvalidDirectoryException(File directory) {
        super("Incorrect initial directory provided: " + directory.getAbsolutePath());
    }

    InvalidDirectoryException() {
        super("\"Incorrect initial directory provided: directory is NULL");
    }
}
