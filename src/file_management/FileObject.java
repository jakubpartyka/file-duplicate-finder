package file_management;

import java.io.File;

public class FileObject {
    public File source;
    public long size;
    public int group;
    public String path;

    public FileObject(File source, int group) {
        this.source = source;
        this.group = group;
        size = source.length();
        path = source.getAbsolutePath();
    }
}
