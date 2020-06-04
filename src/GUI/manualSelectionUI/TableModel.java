package gui.manualSelectionUI;

import file_management.FileObject;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

//todo sort after all updates (by group count descending)

public class TableModel extends AbstractTableModel {
    private String [] columnNames = {
            "no.",
            "gr size",
            "size",
            "location",
    };

    private List<FileObject> allFiles = new ArrayList<>();

    private HashMap<Integer,Integer> groupCounts = new HashMap<>();

    /**
     * HashMap that holds colors for duplicate groups. Key (Integer) represents group number, and value (Color) assigned color.
     */
    private HashMap<Integer,Color> colors = new HashMap<>();

    TableModel(List<List<File>> duplicates) {
        int counter = 0;
        for (List<File> fileList : duplicates) {
            counter++;
            int finalCounter = counter;

            groupCounts.put(counter,fileList.size());
            fileList.forEach(file -> allFiles.add(new FileObject(file, finalCounter)));
        }
        for (Integer group : groupCounts.keySet()) {
            Random random = new Random();
            colors.put(group,new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255),50));
        }
    }

    @Override
    public int getRowCount() {
        return allFiles.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FileObject row = allFiles.get(rowIndex);
        switch (columnIndex){
            case 0: return row.group;
            case 1: return sizeAsString(groupSize(rowIndex));
            case 2: return sizeAsString(row.size);
            case 3: return row.path;
            default:return 0;
        }
    }

    private int groupSize(int rowIndex) {
        FileObject file = allFiles.get(rowIndex);
        int groupCount = groupCounts.get(file.group);
        return (int) (groupCount * file.size);
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    long getSizeOfDuplicates() {
        long result = 0;

        for (Integer group : groupCounts.keySet()) {
            int count = groupCounts.get(group);
            if(count > 1) result += (count - 1) * getFileSizeByGroup(group);
        }

        return result;
    }

    private long getFileSizeByGroup(int group){
        for (FileObject file : allFiles) {
            if(file.group == group)
                return file.size;
        }
        return 0;
    }

    int getFilesCount() {
        return allFiles.size();
    }

    int getDuplicatesCount(){
        int count = 0;
        for (Integer key : groupCounts.keySet()) {
            count += groupCounts.get(key) - 1;
        }
        return count;
    }

    int getGroupsCount() {
        List<Integer> keysToRemove = new ArrayList<>();
        for (Integer key : groupCounts.keySet()) {
            if(groupCounts.get(key) < 1)
                keysToRemove.add(key);
        }
        groupCounts.keySet().removeAll(keysToRemove);
        return groupCounts.size();
    }

    String sizeAsString(long size) {
        double totalkB = round((double)size/1000);
        double totalMB = (totalkB/1000);
        double totalGB = (totalMB/1000);
        if(totalkB < 1000 ) return round(totalkB) + " kB";
        if(totalMB < 1000) return round(totalMB) + " MB";      //fixme
        else return round(totalGB) + " GB";
    }

    void deleteFiles(int[] selectedRows) {
        HashMap<Integer, Integer> groupControl = new HashMap<>();
        List<FileObject> filesToDelete = new ArrayList<>();

        for (int selectedRow : selectedRows) {
            FileObject fileObject = allFiles.get(selectedRow);
            filesToDelete.add(fileObject);

            //increment count in group object
            if(!groupControl.containsKey(fileObject.group))
                groupControl.put(fileObject.group,1);
            else
                groupControl.put(fileObject.group,groupControl.get(fileObject.group)+1);

        }

        //group deletion check
        for (Integer group : groupControl.keySet()) {
            if(groupCounts.get(group).equals(groupControl.get(group))){
                JOptionPane.showMessageDialog(null,"Warning. You tried to delete all elements of group: "
                        + group + "\nThis operation is not allowed","Deletion blocked",JOptionPane.WARNING_MESSAGE);

                //remove files matching this group
                List<FileObject> toRemove = new ArrayList<>();
                for (FileObject fileObject1 : filesToDelete) {
                    if(fileObject1.group == group)
                        toRemove.add(fileObject1);
                }
                filesToDelete.removeAll(toRemove);
            }
        }

        //delete files
        for (FileObject file : filesToDelete) {
            int group = file.group;

            if(!file.source.exists())
                continue;

            if(file.source.delete()) {
                groupCounts.put(group, groupCounts.get(group) - 1);
                allFiles.remove(file);
            }
            else
                System.out.println("cannot delete file: " + file.source.getAbsolutePath());
        }

        fireTableDataChanged();


    }

    void skip(int[] selectedRows) {     //fixme somethings wrong
        List<FileObject> toSkip = new ArrayList<>();
        for (int selectedRow : selectedRows) {
            try {
                FileObject fileObject = allFiles.get(selectedRow);
                int group = fileObject.group;
                groupCounts.put(group, groupCounts.get(group) - 1);
                toSkip.add(fileObject);
            }
            catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
        allFiles.removeAll(toSkip);
        fireTableDataChanged();
    }

    private static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    String getFilePath(int selectedRow) {
        return allFiles.get(selectedRow).source.getAbsolutePath();
    }

    Color getColorByRow(int rowIndex){
        int group = allFiles.get(rowIndex).group;
        return colors.get(group);
    }
}
