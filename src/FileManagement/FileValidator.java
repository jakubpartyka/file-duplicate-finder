package FileManagement;

import java.io.File;
import java.util.HashSet;

public class FileValidator {
    HashSet<String> accept = new HashSet<>();
    HashSet<String> ignore = new HashSet<>();



    /**
     * Checks if specific file should be added to scan queue regarding filters set by user.
     * <b>Ignore patterns have higher priority than accept patterns!</b>
     * @param file file to validate
     * @return true if file should be scanner, false otherwise
     */
    static boolean validate(File file){

        //set file extension


        return true;
    }

    /**
     * Sets a set of patters that file has to match to be validated
     * @param input raw text from "files to accept" UI component
     */
    public void setAccept(String input){}

    public void setIgnore(String input){}

    /**
     * check if specific file matches any of ignore patterns
     * @param filename file to check
     * @return true if file matches any ignore pattern (should not be included in the scan), false otherwise
     */
    private boolean fileMatchesIgnorePatterns(String filename){
        String [] split = filename.split("\\.");
        String extension;

        if (split.length == 0 && ignore.contains(filename)) return true;  //not a hidden file, no extension -> match by name
//        if(filename.sta)


        return false;
    }
}
