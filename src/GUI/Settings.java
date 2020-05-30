package gui;

import file_management.FileValidator;

import javax.swing.*;

public class Settings {
    private JPanel settingsPanel;
    private JButton applyButton;
    private JTextArea acceptPatterns;
    private JTextArea ignorePatterns;
    private JCheckBox acceptFilesThatMatchCheckBox;
    private JCheckBox ignoreFilesThatMatchCheckBox;
    private JTextField smallerTextField;
    private JCheckBox ignoreFilesSmallerThanCheckBox;
    private JCheckBox ignoreFilesBiggerThanCheckbox;
    private JTextField biggerTextField;
    private JRadioButton doNothingRB;
    private JRadioButton symbolicLinksRB;
    private JRadioButton decideManuallyRB;
    private JTextField logOutputTextField;
    private JButton browseButton;
    private JCheckBox saveReportToFileCheckBox;
    private JButton cancelButton;
    private JRadioButton mergeNewFolderRB;

    private ButtonGroup group = new ButtonGroup();

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
        group.add(decideManuallyRB);
        group.add(symbolicLinksRB);
        group.add(mergeNewFolderRB);
        group.add(doNothingRB);
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

        //SAVE SETTINGS
        applyButton.addActionListener(e -> {
            FileValidator.setAccept(acceptPatterns.getText());
            FileValidator.setIgnore(ignorePatterns.getText());
            FileValidator.setAcceptFilterOn(acceptFilesThatMatchCheckBox.isSelected());
            FileValidator.setIgnoreFilterOn(ignoreFilesThatMatchCheckBox.isSelected());
        });
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
    JButton getApplyButton() {
        return applyButton;
    }

    JButton getCancelButton() {
        return cancelButton;
    }
}
