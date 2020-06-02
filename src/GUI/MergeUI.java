package gui;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class MergeUI extends SwingWorker {
    private JPanel mergePanel;
    private JProgressBar progressBar1;
    private JLabel processed;
    private JLabel output;
    private JLabel current;

    private List<List<File>> duplicates;
    private File outputDirectory;


    @Override
    protected Object doInBackground() {
        int total = duplicates.size();
        int counter = 1;

        //count size
        total += duplicates.stream().mapToInt(List::size).sum();

        for (List<File> fileList : duplicates) {
            //copy
            File source = fileList.get(0);
            if( source.renameTo(new File(outputDirectory.getAbsolutePath() + "/" + source.getName())))
                System.out.println(source.getName() + " OK");
            setProgress(counter*100/total);

            //remove duplicates
            for (File duplicate : fileList) {
                //noinspection ResultOfMethodCallIgnored
                duplicate.delete();
            }
        }
        firePropertyChange("end",null,null);
        return null;
    }

    JProgressBar getProgressBar1() {
        return progressBar1;
    }

    void setDuplicates(List<List<File>> duplicates) {
        this.duplicates = duplicates;
    }

    void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.output.setText(outputDirectory.getAbsolutePath());
    }

    JPanel getMergePanel() {
        return mergePanel;
    }
}
