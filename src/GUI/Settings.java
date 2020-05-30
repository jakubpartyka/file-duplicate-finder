package GUI;

import javax.swing.*;

public class Settings {
    private JPanel settingsPanel;
    private JButton saveButton;
    private JTextArea acceptPatterns;
    private JTextArea ignorePatterns;
    private JCheckBox acceptFilesThatMatchCheckBox;
    private JCheckBox ignoreFilesThatMatchCheckBox;
    private JTextField smallerTextField;
    private JCheckBox ignoreFilesSmallerThanCheckBox;
    private JCheckBox ignoreFilesBiggerThanCheckbox;
    private JTextField biggerTextField;
    private JRadioButton doNothingRadioButton;
    private JRadioButton keepOnlyNewestRadioButton;
    private JRadioButton askMeRadioButton;
    private JTextField logOutputTextField;
    private JButton browseButton;
    private JCheckBox saveReportToFileCheckBox;
    private JButton cancelButton;

    public Settings() {
        initComponents();
        addActionListeners();
    }

    private void initComponents() {
//        NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
//        formatter.setValueClass(Integer.class);
//        formatter.setMinimum(0);
//        formatter.setMaximum(Integer.MAX_VALUE);
//        formatter.setAllowsInvalid(false);
        ButtonGroup group = new ButtonGroup();
        group.add(doNothingRadioButton);
        group.add(keepOnlyNewestRadioButton);
        group.add(askMeRadioButton);
    }

    /**
     * add action listeners to UI components
     */
    private void addActionListeners() {
        //enabling / disabling input fields on checkbox click
        acceptFilesThatMatchCheckBox.addActionListener(e -> acceptPatterns.setEnabled(acceptFilesThatMatchCheckBox.isSelected()));
        ignoreFilesThatMatchCheckBox.addActionListener(e -> ignorePatterns.setEnabled(ignoreFilesThatMatchCheckBox.isSelected()));
        ignoreFilesSmallerThanCheckBox.addActionListener(e -> smallerTextField.setEnabled(ignoreFilesSmallerThanCheckBox.isSelected()));
        ignoreFilesBiggerThanCheckbox.addActionListener(e -> biggerTextField.setEnabled(ignoreFilesBiggerThanCheckbox.isSelected()));
        saveReportToFileCheckBox.addActionListener(e -> logOutputTextField.setEnabled(saveReportToFileCheckBox.isSelected()));
    }

    /**
     * @return returns settings content panel
     */
    JPanel getSettingsPanel() {
        return settingsPanel;
    }

    /**
     * @return returns save button object
     */
    JButton getSaveButton() {
        return saveButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }
}
