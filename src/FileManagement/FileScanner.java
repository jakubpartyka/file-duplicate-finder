package FileManagement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import javax.swing.*;

@SuppressWarnings("ConstantConditions")
public class FileScanner extends SwingWorker {
    private boolean recursive;
    private boolean active = true;
    private String status = "not started";

    //all matching files
    private List<ScannerFile> allFiles     = new ArrayList<>();

    //duplicate list
    private List<List<ScannerFile>> duplicates   = new ArrayList<>();

    //directories to scan
    private List<ScannerFile> directoriesToScan = new ArrayList<>();
    
    //output
    private String output = "";

    public FileScanner(List<ScannerFile> initialDirectories, boolean recursive) throws InvalidDirectoryException {
        if(initialDirectories.contains(null))
            throw new InvalidDirectoryException();
        else if(initialDirectories.isEmpty())
            throw new InvalidDirectoryException("empty");
        directoriesToScan.addAll(initialDirectories);
        this.recursive = recursive;
    }

    @Override
    protected Object doInBackground() throws Exception {
        //find all files to compare
        firePropertyChange("status",null,"Preparing files ...");
        while (!directoriesToScan.isEmpty() && active)
            getFilesFromDirectory();

        //search for duplicates
        firePropertyChange("status",null,"Searching for duplicates ...");
        findDuplicates();

        if(duplicates.isEmpty())
            appendToOutput("no duplicates");
        else {
            for (List<ScannerFile> duplicateList : duplicates) {
                appendToOutput("following files are duplicates:");
                for (ScannerFile file : duplicateList) {
                    appendToOutput(file.getAbsolutePath());
                }
                appendToOutput("\n");
            }
        }

        System.out.println(output);

        //set status on exit
        if(!status.equals("cancelled"))
        firePropertyChange("status",null,"completed successfully");

        return output;
    }

    private void findDuplicates() {
        //vars for stats and progress
        int counter = 0;
        int total = allFiles.size();

        //main loop
        while (!allFiles.isEmpty() && active){
            ScannerFile currentFile = allFiles.remove(0);
            List<File> toRemove = new ArrayList<>();

            for (ScannerFile checkedFile : allFiles) {
                firePropertyChange("currentlyChecking",null,checkedFile.getName());
                try {
                    boolean equals = FileUtils.contentEquals(currentFile, checkedFile);

                    //duplicate found!
                    if (equals){
                        addDuplicates(currentFile,checkedFile);
                        toRemove.add(checkedFile);
                        firePropertyChange("duplicatesFound",null, getDuplicatesCount());
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

    private int getDuplicatesCount() {
        int count = 0;
        for (List<ScannerFile> fileList : duplicates) count += fileList.size();
        count -= duplicates.size();
        return count;
    }

    private void addDuplicates(ScannerFile file1, File file2) {
        boolean alreadyExists = false;

        for (List<ScannerFile> duplicateListElement : duplicates) {
            if(duplicateListElement.contains(file1) || duplicateListElement.contains(file2)) {
                alreadyExists = true;
                if (!duplicateListElement.contains(file1)) duplicateListElement.add(file1);
                if (!duplicateListElement.contains(file2)) duplicateListElement.add(file2);
            }
        }

        if(!alreadyExists)
            duplicates.add(new ArrayList<>(Arrays.asList(file1,file2)));
    }

    private void getFilesFromDirectory() {
        ScannerFile currentDir = directoriesToScan.remove(0);

        if(currentDir.listFiles() == null)
            return;

        List<ScannerFile> filesFromDir = convertToScannerFiles(currentDir.listFiles());

        for (ScannerFile file : filesFromDir){
            if(file.isDirectory() && recursive)
                directoriesToScan.add(file);
            else if(file.isFile())
                allFiles.add(file);
        }
    }

    /**
     * converts an array of File objects into an ArrayList of FileScanner objects
     * @param listFiles array containing files from handled directory
     * @return returns an ArrayList of FileScanner objects
     */
    private List<ScannerFile> convertToScannerFiles(File[] listFiles) {
        List<ScannerFile> scannerFiles = new ArrayList<>();
        for (File listFile : listFiles) {
            scannerFiles.add(new ScannerFile(listFile.getAbsolutePath()));
        }
        return scannerFiles;
    }

    private void appendToOutput(String message){
        output += message + "\n";
    }

    public void setActive(boolean active) {
        this.active = active;
        status = "cancelled";
        firePropertyChange("status",null, status);
    }
}
