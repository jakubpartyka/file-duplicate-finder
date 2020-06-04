package file_management;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.io.FileUtils;
import javax.swing.*;

@SuppressWarnings("ConstantConditions")
public class FileScanner extends SwingWorker {
    private boolean recursive;
    private boolean active = true;
    private String status = "not started";

    //VALUES PASSED FROM SETTINGS VIEW
    private static boolean compareMode = false;

    /**
     * all files that passed filtering (compare mode disabled)
     */
    private List<File> allFiles = new ArrayList<>();

    /**
     * all files that passed filtering (divided in lists by directories for compare mode)
     */
    private List<List<File>> filesCompareMode = new ArrayList<>();

    //duplicate list
    private List<List<File>> duplicates = new ArrayList<>();

    //directories to scan
    private List<File> directoriesToScan = new ArrayList<>();
    
    //output
    private String output = "";

    public FileScanner(List<File> initialDirectories, boolean recursive) throws InvalidDirectoryException {
        if(initialDirectories.contains(null))
            throw new InvalidDirectoryException();
        else if(initialDirectories.isEmpty())
            throw new InvalidDirectoryException("empty");
        directoriesToScan.addAll(initialDirectories);
        this.recursive = recursive;
    }

    @Override
    protected Object doInBackground() {
        firePropertyChange("status", null, "Preparing files ...");

        //find files to compare in compare mode
        if(compareMode){
            if(directoriesToScan.size() != 2){
                JOptionPane.showMessageDialog(null,"You have chosen Directory Compare Mode. Exactly 2 directories have to be chosen to use it." +
                        "\nNumber of currently chosen directories: " + directoriesToScan.size(),"Wrong parameters",JOptionPane.WARNING_MESSAGE);
                firePropertyChange("end",null,null);
                return "wrong input parameters";
            }

            //find files to compare in compare mode
            getFilesFromDirectoryCompareMode();

            //search for duplicates (compare mode)
            findDuplicatesCompareMode();

        }
        else {
            //find all files to compare
            while (!directoriesToScan.isEmpty() && active)
                getFilesFromDirectory();

            //search for duplicates
            findDuplicates();
        }

        //prepare output
        if (duplicates.isEmpty())
            appendToOutput("no duplicates");
        else
            for (List<File> duplicateList : duplicates) {
                appendToOutput("following files are duplicates:");
                for (File file : duplicateList) appendToOutput(file.getAbsolutePath());
                appendToOutput("\n");
            }
        System.out.println(output);

        //set status on exit
        if (!status.equals("cancelled"))
            firePropertyChange("status", null, "completed successfully");
        firePropertyChange("done", null, "done");

        return duplicates;
    }

    /**
     * Finds duplicated files in Compare Mode (duplicates appearing in same tree are not marked as duplicates)
     * compares every file from a directory to every other file from other directories but not against files
     * from the same parent directory.
     */
    private void findDuplicatesCompareMode() {
        if(active) firePropertyChange("status",null,"Searching for duplicates ...");

        //vars for stats and progress
        int counter = 0;
        int total = filesCompareMode.stream().mapToInt(List::size).sum();           //calculate total number of files in all directories

        for (int i = 0; i < filesCompareMode.size(); i++) {
            for (File file : filesCompareMode.get(i)) {
                firePropertyChange("nowChecking",null, file.getName());
                counter++;
                for (int j = i+1; j < filesCompareMode.size(); j++) {
                    List<File> duplicates = new ArrayList<>();
                    List<File> toRemove = new ArrayList<>();
                    for (File file2 : filesCompareMode.get(j)) {
                        if(!active) return;
                        try {
                            if(FileUtils.contentEquals(file,file2)){
                                counter++;
                                duplicates.add(file);
                                duplicates.add(file2);
                                toRemove.add(file2);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //add found duplicates and remove found files from current comparing list
                    if(duplicates.size() > 1) {
                        this.duplicates.add(duplicates);
                        filesCompareMode.get(j).removeAll(toRemove);
                    }
                }

                //update stats after every file checked
                firePropertyChange("duplicatesFound",null, getDuplicatesCount());
                firePropertyChange("totalSize",null, getDuplicatesSize());
                setProgress((counter*100/total));
                firePropertyChange("filesScanned",null,counter);
            }
        }
    }

    /**
     * compares files to each other to find duplicates
     */
    private void findDuplicates() {
        //set status
        if(active) firePropertyChange("status",null,"Searching for duplicates ...");

        //vars for stats and progress
        int counter = 0;
        int total = allFiles.size();

        //main loop
        while (!allFiles.isEmpty() && active){
            File currentFile = allFiles.remove(0);
            List<File> toRemove = new ArrayList<>();

            firePropertyChange("nowChecking",null, currentFile.getName());
            for (File checkedFile : allFiles) {
                try {
                    boolean equals = FileUtils.contentEquals(currentFile, checkedFile);

                    //duplicate found!
                    if (equals){
                        addDuplicates(currentFile,checkedFile);
                        toRemove.add(checkedFile);
                        firePropertyChange("duplicatesFound",null, getDuplicatesCount());
                        firePropertyChange("totalSize",null, getDuplicatesSize());
                    }
                } catch (IOException e) {
                    //todo handle exception
                }
            }

            allFiles.removeAll(toRemove);           //remove from allFiles found duplicates

            //set properties
            setProgress((total-allFiles.size())*100/total);
            firePropertyChange("filesScanned",null,++counter);
        }
    }

    /**
     * returns disk space size used by duplicated files
     * @return total size of duplicated files in MB
     */
    private String getDuplicatesSize() {
        AtomicLong size = new AtomicLong();
        duplicates.forEach(files -> files.forEach(file -> size.addAndGet(file.length())));
        double totalkB = round((double) size.get()/1000);
        double totalMB = (totalkB/1000);
        double totalGB = (totalMB/1000);
        if(totalkB < 1000 ) return round(totalkB) + " kB";
        if(totalMB < 1000) return round(totalMB) + " MB";      //fixme
        else return round(totalGB) + " GB";
    }

    /**
     * returns number of duplicated files (not counting the "original" file)
     * @return number of duplicated files
     */
    private int getDuplicatesCount() {
        int count = 0;
        for (List<File> fileList : duplicates) count += fileList.size();
        count -= duplicates.size();
        return count;
    }

    /**
     * adds found duplicated file to corresponding ArrayList
     * @param file1 first duplicated file copy
     * @param file2 second duplicated file copy
     */
    private void addDuplicates(File file1, File file2) {
        boolean alreadyExists = false;

        for (List<File> duplicateListElement : duplicates) {
            if(duplicateListElement.contains(file1) || duplicateListElement.contains(file2)) {
                alreadyExists = true;
                if (!duplicateListElement.contains(file1)) duplicateListElement.add(file1);
                if (!duplicateListElement.contains(file2)) duplicateListElement.add(file2);
            }
        }

        if(!alreadyExists)
            duplicates.add(new ArrayList<>(Arrays.asList(file1,file2)));
    }

    /**
     * scans next directory and adds matching files to scan queue. File accepting is done via FileValidator class
     */
    private void getFilesFromDirectory() {
        File currentDir = directoriesToScan.remove(0);

        if(currentDir.listFiles() == null)
            return;

        for (File file : currentDir.listFiles()){
            if(file.isDirectory() && recursive)
                directoriesToScan.add(file);
            else if(file.isFile() && FileValidator.validateExtension(file) && FileValidator.validateSize(file))
                allFiles.add(file);
        }
    }

    private void getFilesFromDirectoryCompareMode() {
        //create correct number of File lists
        for (File ignored : directoriesToScan) filesCompareMode.add(new ArrayList<>());

        List<File> innerDirectories = new ArrayList<>();
        int directoryCounter = 0;
        for (File directory : directoriesToScan) {
            //clear inner directories and add current parent dir to the queue
            innerDirectories.clear();
            innerDirectories.add(directory);

            while (!innerDirectories.isEmpty()){
                File currentDir = innerDirectories.remove(0);
                for (File file : currentDir.listFiles()){
                    if(file.isDirectory() && recursive)
                        innerDirectories.add(file);
                    else if(file.isFile() && FileValidator.validateExtension(file) && FileValidator.validateSize(file))
                        filesCompareMode.get(directoryCounter).add(file);
                }
            }
            directoryCounter++;
        }
    }

    private void appendToOutput(String message){
        output += message + "\n";
    }

    public void setActive(boolean active) {
        this.active = active;
        status = "cancelled";
        firePropertyChange("status",null, status);
    }

    private static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public String getOutput() {
        return output;
    }

    public static void setCompareMode(boolean compareMode) {
        FileScanner.compareMode = compareMode;
    }
}
