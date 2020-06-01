package file_management.optimizers;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HardLinkCreator extends SwingWorker {
    private List<List<File>> duplicates;

    public HardLinkCreator(List<List<File>> duplicates) {
        this.duplicates = duplicates;
    }

    @Override
    protected Object doInBackground() throws Exception {
        AtomicInteger total = new AtomicInteger();
        duplicates.forEach(files -> files.forEach(file -> total.getAndIncrement()));    //count total size

        int progress = 0;
        for (List<File> fileList : duplicates) {
            File original = fileList.remove(0);
            progress++;

            for (File file : fileList) {
                Path target = Paths.get(original.getAbsolutePath());
                Path link = Paths.get(file.getAbsolutePath());
                if (Files.exists(link)) {
                    Files.delete(link);
                }
                Files.createLink(link, target);
                int prg = ++progress*100/total.get();
                firePropertyChange("prg",null,prg);
            }
        }

        firePropertyChange("end",null,null);
        return null;
    }
}
