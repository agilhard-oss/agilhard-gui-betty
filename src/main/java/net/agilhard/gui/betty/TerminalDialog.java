package net.agilhard.gui.betty;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.agilhard.jsch.UIKeyboardInteractive;
import net.agilhard.jsch.UserInfo;

import net.agilhard.terminal.emulation.Questioner;

/**
 * The Class TerminalDialog.
 */
public class TerminalDialog implements UserInfo, UIKeyboardInteractive, Questioner {

    /** The parent. */
    private final Component parent;

    /**
     * Instantiates a new terminal dialog.
     *
     * @param parent
     *            the parent
     */
    public TerminalDialog(final Component parent) {
        this.parent = parent;
    }

    /* (non-Javadoc)
     * @see net.agilhard.jsch.UserInfo#promptYesNo(java.lang.String)
     */
    @Override
    public boolean promptYesNo(final String str) {
        final Object[] options = { "yes", "no" };
        final int foo = JOptionPane.showOptionDialog(this.parent, str, "Warning", JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        return foo == 0;
    }

    /** The passwd. */
    private String passwd;

    /** The passphrase. */
    private String passphrase;

    /** The pword. */
    private final JTextField pword = new JPasswordField(20);

    /* (non-Javadoc)
     * @see net.agilhard.jsch.UserInfo#getPassword()
     */
    @Override
    public String getPassword() {
        return this.passwd;
    }

    /* (non-Javadoc)
     * @see net.agilhard.jsch.UserInfo#getPassphrase()
     */
    @Override
    public String getPassphrase() {
        return this.passphrase;
    }

    /* (non-Javadoc)
     * @see net.agilhard.jsch.UserInfo#promptPassword(java.lang.String)
     */
    @Override
    @SuppressWarnings("serial")
    public boolean promptPassword(final String message) {
        final JPanel aPanel = new JPanel();
        aPanel.add(this.pword);
        this.pword.requestFocusInWindow();
        final JOptionPane pane = new JOptionPane(aPanel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {

            @Override
            public void selectInitialValue() {
                //.
            }
        };

        final JDialog dialog = pane.createDialog(this.parent, message);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        final Object o = pane.getValue();

        if (o != null && ((Integer) o).intValue() == JOptionPane.OK_OPTION) {
            this.passwd = this.pword.getText();
            return true;
        }
        return false;

    }

    /* (non-Javadoc)
     * @see net.agilhard.jsch.UserInfo#promptPassphrase(java.lang.String)
     */
    @Override
    public boolean promptPassphrase(final String message) {
        return true;
    }

    /** The gbc. */
    private final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST,
        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

    /** The panel. */
    private Container panel;

    /* (non-Javadoc)
     * @see net.agilhard.jsch.UIKeyboardInteractive#promptKeyboardInteractive(java.lang.String, java.lang.String, java.lang.String, java.lang.String[], boolean[])
     */
    @Override
    @SuppressWarnings("serial")
    public String[] promptKeyboardInteractive(final String destination, final String name, final String instruction,
        final String[] prompt, final boolean[] echo) {
        this.panel = new JPanel();
        this.panel.setLayout(new GridBagLayout());

        this.gbc.weightx = 1.0;
        this.gbc.gridwidth = GridBagConstraints.REMAINDER;
        this.gbc.gridx = 0;
        this.panel.add(new JLabel(instruction), this.gbc);
        this.gbc.gridy++;

        this.gbc.gridwidth = GridBagConstraints.RELATIVE;

        final JTextField[] texts = new JTextField[prompt.length];
        for (int i = 0; i < prompt.length; i++) {
            this.gbc.fill = GridBagConstraints.NONE;
            this.gbc.gridx = 0;
            this.gbc.weightx = 1;
            this.panel.add(new JLabel(prompt[i]), this.gbc);

            this.gbc.gridx = 1;
            this.gbc.fill = GridBagConstraints.HORIZONTAL;
            this.gbc.weighty = 1;
            if (echo[i]) {
                texts[i] = new JTextField(20);
            } else {
                texts[i] = new JPasswordField(20);
                texts[i].requestFocusInWindow();
            }
            this.panel.add(texts[i], this.gbc);
            this.gbc.gridy++;
        }
        for (int i = prompt.length - 1; i > 0; i--) {
            texts[i].requestFocusInWindow();
        }
        final JOptionPane pane =
            new JOptionPane(this.panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {

                @Override
                public void selectInitialValue() {
                    //.
                }
            };
        final JDialog dialog = pane.createDialog(this.parent, destination + ": " + name);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        final Object o = pane.getValue();
        if (o != null && ((Integer) o).intValue() == JOptionPane.OK_OPTION) {
            final String[] response = new String[prompt.length];
            for (int i = 0; i < prompt.length; i++) {
                response[i] = texts[i].getText();
            }
            return response;
        }
        return null; // cancel

    }

    /* (non-Javadoc)
     * @see net.agilhard.terminal.emulation.Questioner#question(java.lang.String, java.lang.String)
     */
    @Override
    public String question(final String question, final String defValue) {
        return JOptionPane.showInputDialog(this.parent, question, defValue);
    }

    /* (non-Javadoc)
     * @see net.agilhard.jsch.UserInfo#showMessage(java.lang.String)
     */
    @Override
    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(this.parent, message, "", JOptionPane.INFORMATION_MESSAGE);
    }

    /* (non-Javadoc)
     * @see net.agilhard.terminal.emulation.Questioner#showError(java.lang.String)
     */
    @Override
    public void showError(final String message) {
        JOptionPane.showMessageDialog(this.parent, message, "", JOptionPane.ERROR_MESSAGE);
    }
}
