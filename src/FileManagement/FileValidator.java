package FileManagement;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class FileValidator {
    /**
     * Set of patters that a file has to match to be included in a scan
     */
    private static HashSet<String> accept = new HashSet<>();
    /**
     * Set of patterns that a file can not match to be included in a scan. "." matches any hidden file
     */
    private static HashSet<String> ignore = new HashSet<>();

    private static boolean acceptFilterOn = false;
    private static boolean ignoreFilterOn = false;



    /**
     * Checks if specific file should be added to scan queue regarding filters set by user.
     * <b>
     * <li>Ignore patterns have higher priority than accept patterns!</li>
     * <li>If one of the filters (ignore/accept) is inactive other patters are applied</li>
     * <li>If both filters are inactive in Settings view all files are accepted.</li>
     * </b>
     * @param file file to validate
     * @return true if file should be scanner, false otherwise
     */
    static boolean validate(File file){
        if(!ignoreFilterOn && !acceptFilterOn)  //filters not applied, all files accepted
            return true;

        String filename = file.getName();
        if(ignoreFilterOn && matchesIgnorePatterns(filename))
            return false;                       //if matches ignore patters return false

        if(!acceptFilterOn) return true;                //accept filter inactive but passed ignore filter -> return true
        else return matchesAcceptPatterns(filename);    //return true if matches accepted pattern
    }

    /**
     * Sets a set of patters that file has to match to be validated.
     * Whitespaces occurring in pattern are ignored.
     * @param rawInput raw text from "files to accept" GUI
     */
    public static void setAccept(String rawInput){
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(rawInput.split("\n")));
        //remove empty lines and unwanted input
        for (String line : lines) {
            line = line.replaceAll("\\s\\+", "");
            accept.add(line);
        }
    }

    /**
     * Sets a set of patters that file can <b>not</b> match to be validated.
     * Whitespaces occurring in pattern are ignored.
     * @param rawInput raw text from "files to ignore" GUI
     */
    public static void setIgnore(String rawInput){
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(rawInput.split("\n")));
        //remove empty lines and unwanted input
        for (String line : lines) {
            line = line.replaceAll("\\s\\+", "");
            ignore.add(line);
        }
    }

    /**
     * check if specific file matches any of ignore patterns
     * @param filename file to check
     * @return true if file matches any ignore pattern (should not be included in the scan), false otherwise
     */
    private static boolean matchesIgnorePatterns(String filename){
        boolean isHidden = filename.startsWith(".");
        if(isHidden){
            if(ignore.contains(".")) return true;   //all hidden files ignored -> ignore
            return ignore.contains(filename);
        }
        else {
            //check if extension present
            boolean extensionPresent = filename.contains(".");
            if (!extensionPresent)
                return ignore.contains(filename);
            else {
                //get extension and see if matches any pattern
                String extension = filename.split("\\.")[filename.split("\\.").length-1];
                return ignore.contains("." + extension);
            }
        }
    }

    /**
     * check if specific file matches any of accept patterns
     * @param filename file to check
     * @return true if file matches any accept pattern (should be included in the scan), false otherwise
     */
    private static boolean matchesAcceptPatterns(String filename) {
        boolean isHidden = filename.startsWith(".");
        if(isHidden) return accept.contains(filename);      //file hidden -> return true if matches pattern
        else {
            //check if extension present
            boolean extensionPresent = filename.contains(".");
            if (!extensionPresent)
                return accept.contains(filename);           //no extension -> return true if matches pattern
            else {
                //get extension and see if matches any pattern
                String extension = filename.split("\\.")[filename.split("\\.").length-1];
                return accept.contains("." + extension);
            }
        }
    }

    public static void setAcceptFilterOn(boolean acceptFilterOn) {
        FileValidator.acceptFilterOn = acceptFilterOn;
    }

    public static void setIgnoreFilterOn(boolean ignoreFilterOn) {
        FileValidator.ignoreFilterOn = ignoreFilterOn;
    }
}
