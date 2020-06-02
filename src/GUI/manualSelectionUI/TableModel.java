package gui.manualSelectionUI;

import file_management.FileObject;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class TableModel extends AbstractTableModel {
    private String [] columnNames = {
            "group no.",
            "size",
            "location",
    };

    private List<FileObject> allFiles = new ArrayList<>();

    private HashMap<Integer,Integer> groupCounts = new HashMap<>();

    TableModel(List<List<File>> duplicates) {
        int counter = 0;
        for (List<File> fileList : duplicates) {
            counter++;
            int finalCounter = counter;

            groupCounts.put(counter,fileList.size());
            fileList.forEach(file -> allFiles.add(new FileObject(file, finalCounter)));
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
            case 1: return sizeAsString(row.size);
            case 2: return row.path;
            default:return 0;
        }
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 2)
            return String.class;
        else return Integer.class;
    }

    long getTotalSize() {
        AtomicLong result = new AtomicLong();
        allFiles.forEach(fileObject -> result.addAndGet(fileObject.size));
        return result.get();
    }

    int getFilesCount() {
        return allFiles.size();
    }

    int getGroupsCount() {
        for (Integer key : groupCounts.keySet()) {
            if(groupCounts.get(key) < 1)
                groupCounts.remove(key);
        }
        return groupCounts.size();
    }

    String sizeAsString(long size) {
        double totalkB = (double)(size*100/1000)/100;
        double totalMB = (totalkB/1000);
        double totalGB = (totalMB/1000);
        if(totalkB < 1000 ) return totalkB + " kB";
        if(totalMB < 1000) return totalMB + " MB";      //fixme
        else return totalGB + " GB";
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

    void skip(int[] selectedRows) {
        for (int selectedRow : selectedRows) {
            FileObject fileObject = allFiles.get(selectedRow);
            int group = fileObject.group;
            groupCounts.put(group, groupCounts.get(group) - 1);
            allFiles.remove(fileObject);
        }
        fireTableDataChanged();
    }
}
