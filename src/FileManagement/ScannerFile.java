package FileManagement;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ScannerFile extends File {
    public ScannerFile(String pathname) {
        super(pathname);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != this.getClass()) return false;
        ScannerFile file = (ScannerFile) obj;
        try {
            return FileUtils.contentEquals(this,file);
        } catch (IOException e) {
            return false;
        }
    }
}
