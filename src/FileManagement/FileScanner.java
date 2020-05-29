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
    //COMPONENTS TO UPDATE

    //all matching files
    private List<File> allFiles     = new ArrayList<>();

    //duplicate list
    private List<List<File>> duplicates   = new ArrayList<>();

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
    protected Object doInBackground() throws Exception {
        //find all files to compare
//        firePropertyChange("currentTask",null,"Preparing files ...");
        while (!directoriesToScan.isEmpty())
            getFilesFromDirectory();

        //search for duplicates
//        firePropertyChange("currentTask",null,"Searching for duplicates ...");
        findDuplicates();

        if(duplicates.isEmpty())
            appendToOutput("no duplicates");
        else
            for (List<File> duplicateList : duplicates) {
                appendToOutput("following files are duplicates:");
                for (File file : duplicateList) {
                    appendToOutput(file.getAbsolutePath());
                }
                appendToOutput("\n");
            }
        System.out.println(output);
        return output;
    }

    private void findDuplicates() {
        int counter = 0;
        int total = allFiles.size();
        while (!allFiles.isEmpty()){
            File currentFile = allFiles.remove(0);

            List<File> toRemove = new ArrayList<>();
            for (File checkedFile : allFiles) {
                try {
                    boolean equals = FileUtils.contentEquals(currentFile, checkedFile);
                    if(equals){
                        addDuplicates(currentFile,checkedFile);
                        toRemove.add(checkedFile);
                    }
                } catch (IOException e) {
                    //todo handle exception
                }
            }
            allFiles.removeAll(toRemove);           //remove from allFiles

            //set properties
            setProgress((total-allFiles.size())*100/total);
            firePropertyChange("filesScanned",null,++counter);
        }
    }

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

    private void getFilesFromDirectory() {
        File currentDir = directoriesToScan.remove(0);

        if(currentDir.listFiles() == null)
            return;

        for (File file : currentDir.listFiles()){
            if(file.isDirectory() && recursive)
                directoriesToScan.add(file);
            else if(file.isFile())
                allFiles.add(file);
        }
    }
    
    private void appendToOutput(String message){
        output += message + "\n";
    }
}
