package GUI;

import javax.swing.*;

public class ProgressFrame extends SwingWorker {
    private JProgressBar progressBar;
    private JLabel currentlyProcessing;
    private JButton cancelButton;

    @Override
    protected Object doInBackground() throws Exception {
        return null;
    }
}
