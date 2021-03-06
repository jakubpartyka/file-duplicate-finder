package gui.manualSelectionUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ManualSelectorUI {
    private JPanel manualPanel;
    private JButton deleteButton;
    @SuppressWarnings("unused")
    private JScrollPane tablePane;
    private JTable table;
    private JButton showButton;
    private JLabel left;
    private JLabel groupsLeft;
    private JLabel sizeTaken;
    private JButton skipButton;
    private JButton goBackToMainButton;
    private JButton refreshButton;

    private TableModel model;

    public void initiate(List<List<File>> duplicates){
        initTable(duplicates);
        initActionListeners();
        updateStats();
    }

    private void updateStats() {
        left.setText("Duplicates left: " + model.getDuplicatesCount());
        groupsLeft.setText("Duplicate groups left: " + model.getGroupsCount());
        sizeTaken.setText("Size taken by duplicates: " + model.sizeAsString(model.getSizeOfDuplicates()));
    }

    private void initActionListeners() {
        showButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File(model.getFilePath(table.getSelectedRow())).getParentFile());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        deleteButton.addActionListener(e -> {
            model.deleteFiles(table.getSelectedRows());
            updateStats();
        });

        skipButton.addActionListener(e -> {
                model.skip(table.getSelectedRows());
                updateStats();
        });

        refreshButton.addActionListener(e -> {
            model.refresh();
            updateStats();
        });
    }

    private void initTable(List<List<File>> duplicates) {
        model = new TableModel(duplicates);
        //noinspection BoundFieldAssignment
        table.setModel(model);

        table.setAutoCreateRowSorter(false);

        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(model.getColorByRow(row));
                return this;
            }
        });

        //set columns
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setWidth(60);
        table.getColumnModel().getColumn(0).setMaxWidth(100);
        table.getColumnModel().getColumn(1).setMaxWidth(150);
        table.getColumnModel().getColumn(1).setWidth(100);
        table.getColumnModel().getColumn(2).setMaxWidth(150);

        //reload table
        table.repaint();
        table.revalidate();
    }

    public JPanel getManualPanel() {
        return manualPanel;
    }

    public JButton getGoBackToMainButton() {
        return goBackToMainButton;
    }
}
