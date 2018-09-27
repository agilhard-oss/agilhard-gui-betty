package net.agilhard.gui.betty;

import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_LOCAL_SESSION;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_LOCAL_SESSION_NEWFRAME;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_LOCAL_SESSION_NEWTAB;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_SFTP_SESSION;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_SFTP_SESSION_NEWFRAME;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_SFTP_SESSION_NEWTAB;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_SSH_SESSION;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_SSH_SESSION_NEWFRAME;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_SSH_SESSION_NEWTAB;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_SSH;
import static net.agilhard.terminal.emulation.TerminalEmulationConstants.LOCAL_SHELL;
import static net.agilhard.terminal.emulation.TerminalEmulationConstants.SFTP;
import static net.agilhard.terminal.emulation.TerminalEmulationConstants.SHELL;

import java.awt.event.ActionEvent;

import net.agilhard.gui.framework.swing.app.AppManager;

/**
 * A handler for the Terminal.
 */
public class TerminalHandler {

    /** The app manager. */
    private final AppManager appManager;

    /**
     * Instantiates a new terminal handler.
     *
     * @param appManager
     *            the app manager
     */
    public TerminalHandler(final AppManager appManager) {
        this.appManager = appManager;
    }

    /**
     * Open terminal frame.
     *
     * @param mode
     *            the mode
     */
    @SuppressWarnings("unused")
    public void openTerminalFrame(final int mode) {
        final TerminalFrame c = new TerminalFrame(this.appManager, mode, true);
    }

    /**
     * Open terminal tab.
     *
     * @param mode
     *            the mode
     */
    public void openTerminalTab(final int mode) {
        if (this.appManager.hasTabs()) {
            final TerminalPanel panel = new TerminalPanel(this.appManager, mode);
            this.appManager.addTab(panel);
            panel.getController().openSession();
        }
    }

    /**
     * Open terminal frame.
     *
     * @param userhost
     *            the userhost
     */
    @SuppressWarnings("unused")
    public void openTerminalFrame(final String userhost) {
        final TerminalFrame c = new TerminalFrame(this.appManager, userhost);
    }

    /**
     * Open terminal tab.
     *
     * @param userhost
     *            the userhost
     */
    public void openTerminalTab(final String userhost) {
        if (this.appManager.hasTabs()) {
            final TerminalPanel panel = new TerminalPanel(this.appManager, userhost);
            this.appManager.addTab(panel);
            panel.getController().openSession();
        }
    }

    /**
     * Handle action performed.
     *
     * @param e
     *            the e
     * @param useFrame
     *            the use frame
     * @return true, if successful
     */
    public boolean handleActionPerformed(final ActionEvent e, final boolean useFrame) {
        final String action = e.getActionCommand();

        int myMode = SHELL;
        if (action.equals(ACTION_OPEN_SSH_SESSION) || action.equals(ACTION_OPEN_SSH_SESSION_NEWFRAME)
            || action.equals(ACTION_OPEN_SSH_SESSION_NEWTAB)) {
            myMode = SHELL;
        } else if (action.equals(ACTION_OPEN_SFTP_SESSION) || action.equals(ACTION_OPEN_SFTP_SESSION_NEWFRAME)
            || action.equals(ACTION_OPEN_SFTP_SESSION_NEWTAB)) {
            myMode = SFTP;
        }
        if (action.equals(ACTION_OPEN_LOCAL_SESSION) || action.equals(ACTION_OPEN_LOCAL_SESSION_NEWFRAME)
            || action.equals(ACTION_OPEN_LOCAL_SESSION_NEWTAB)) {
            myMode = LOCAL_SHELL;
        }

        if (action.equals(ACTION_OPEN_SSH_SESSION) || action.equals(ACTION_OPEN_SSH_SESSION_NEWFRAME)
            || action.equals(ACTION_OPEN_SSH_SESSION_NEWTAB) || action.equals(ACTION_OPEN_SFTP_SESSION)
            || action.equals(ACTION_OPEN_SFTP_SESSION_NEWFRAME) || action.equals(ACTION_OPEN_SFTP_SESSION_NEWTAB)) {

            if (useFrame && (action.equals(ACTION_OPEN_SSH_SESSION) || action.equals(ACTION_OPEN_SFTP_SESSION))) {
                this.openTerminalFrame(myMode);
            } else if (action.equals(ACTION_OPEN_SSH_SESSION_NEWFRAME)
                || action.equals(ACTION_OPEN_SFTP_SESSION_NEWFRAME)) {
                this.openTerminalFrame(myMode);
            } else {
                this.openTerminalTab(myMode);
            }
            return true;

        } else if (action.startsWith(ACTION_SSH) && action.length() > ACTION_SSH.length()) {
            final String userHost = action.substring(ACTION_SSH.length());
            if (useFrame) {
                this.openTerminalFrame(userHost);
            } else {
                this.openTerminalTab(userHost);
            }
            return true;

        } else if (action.equals(ACTION_OPEN_LOCAL_SESSION) || action.equals(ACTION_OPEN_LOCAL_SESSION_NEWFRAME)
            || action.equals(ACTION_OPEN_LOCAL_SESSION_NEWTAB)) {

            if (useFrame && action.equals(ACTION_OPEN_LOCAL_SESSION)) {
                this.openTerminalFrame(myMode);
            } else if (action.equals(ACTION_OPEN_LOCAL_SESSION_NEWFRAME)) {
                this.openTerminalFrame(myMode);
            } else {
                this.openTerminalTab(myMode);
            }
            return true;
        }
        return false;
    }
}
