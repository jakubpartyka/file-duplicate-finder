package gui.manualSelectionUI;

import file_management.FileObject;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class TableModel extends AbstractTableModel {
    private String [] columnNames = {
            "group no.",
            "size",
            "location",
    };

    private List<FileObject> allFiles = new ArrayList<>();
    private List<Integer>    groups   = new ArrayList<>();

    TableModel(List<List<File>> duplicates) {
        int counter = 0;
        for (List<File> fileList : duplicates) {
            counter++;
            groups.add(counter);
            for (File file : fileList) {
                allFiles.add(new FileObject(file,counter));
            }
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
            case 1: return row.size;
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
        return groups.size();
    }

    String totalSizeAsString() {
        long total = getTotalSize();
        long totalkB = (total/1000);
        long totalMB = (totalkB/1000);
        long totalGB = (totalMB/1000);
        if(total < 1000 ) return totalkB + " kB";
        if(totalMB < 1000) return totalMB + " MB";
        else return totalGB + " GB";

    }
}
