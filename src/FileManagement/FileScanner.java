package FileManagement;

import java.io.File;

public class FileScanner {
    private File initialDirectory;
    private boolean recursive;

    public FileScanner(File initialDirectory, boolean recursive) {
        this.initialDirectory = initialDirectory;
        this.recursive = recursive;
    }

    public void scan() {
    }
}
