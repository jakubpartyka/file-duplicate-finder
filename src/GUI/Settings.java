package GUI;

import javax.swing.*;

public class Settings {
    private JPanel settingsPanel;
    private JButton saveButton;
    private JTextArea acceptPatterns;
    private JTextArea ignorePatterns;
    private JCheckBox acceptOnlyFilesThatCheckBox;
    private JCheckBox ignoreFilesThatMatchCheckBox;

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
}
