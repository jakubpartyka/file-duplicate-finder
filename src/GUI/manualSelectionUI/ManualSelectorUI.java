package gui.manualSelectionUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ManualSelectorUI {
    private JPanel manualPanel;
    private JButton deleteButton;
    private JScrollPane tablePane;
    private JTable table;
    private JButton showButton;
    private JLabel left;
    private JLabel groupsLeft;
    private JLabel sizeTaken;

    private TableModel model;


    public void initiate(List<List<File>> duplicates){
        initTable(duplicates);
        initActionListeners();


        left.setText("Duplicates left: " + model.getFilesCount());
        groupsLeft.setText("Duplicate groups left: " + model.getGroupsCount());
        sizeTaken.setText("Size taken by duplicates: " + model.totalSizeAsString());

    }

    private void initActionListeners() {
        showButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File("/Users/admin/"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void initTable(List<List<File>> duplicates) {
        model = new TableModel(duplicates);
        //noinspection BoundFieldAssignment
        table.setModel(model);
        table.repaint();
        table.revalidate();
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
    }

    public JPanel getManualPanel() {
        return manualPanel;
    }
}
