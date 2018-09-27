package net.agilhard.gui.betty;

import static net.agilhard.gui.betty.TerminalConstants.ACTION_COMPRESSION;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_LOCAL_SESSION;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_LOCAL_SESSION_NEWFRAME;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_LOCAL_SESSION_NEWTAB;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_SSH_SESSION;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_SSH_SESSION_NEWFRAME;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_SSH_SESSION_NEWTAB;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_SSH;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import net.agilhard.gui.framework.swing.app.AppIcon;
import net.agilhard.gui.framework.swing.app.AppMenuHelper;
import net.agilhard.gui.framework.swing.app.AppMessages;
import net.agilhard.terminal.emulation.shell.LocalPtyFactory;

/**
 * The Class TerminalMenuHelper.
 */
public class TerminalMenuHelper extends AppMenuHelper {

    /** The terminal menu. */
    private JMenu terminalMenu;

    /** The ssh menu. */
    private JMenu sshMenu;

    /** The ssh menu. */
    private JMenu sshMenuTray;

    /** The Constant REMOTE_SHELL_PNG. */
    private static final String REMOTE_SHELL_PNG = "remote-shell.png";

    /** The Constant LOCAL_SHELL_PNG. */
    private static final String LOCAL_SHELL_PNG = "local-shell.png";

    /**
     * Gets the app menu bar.
     *
     * @param actionListener
     *            the action listener
     * @param forFrame
     *            the for frame
     * @return the app menu bar
     */
    @Override
    public JMenuBar getAppMenuBar(final ActionListener actionListener, final boolean forFrame) {
        final JMenuBar mainMenu = new JMenuBar();
        mainMenu.setFocusable(true);
        final JMenu fileMenu = new JMenu(AppMessages.getI18n("MENU_FILE"));
        fileMenu.setIcon(AppIcon.getIcon("new.png"));
        this.setFileMenu(fileMenu);
        mainMenu.add(fileMenu);

        this.addSshMenu(mainMenu, actionListener);

        this.addAppMenuItems(actionListener, mainMenu, fileMenu, forFrame);

        this.addCloseQuitAttachDetach(actionListener, fileMenu, forFrame);

        return mainMenu;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.agilhard.guk.framework.swing.app.AppMenuHelper#addAppMenutems(java.awt.event.
     * ActionListener, javax.swing.JMenuBar, javax.swing.JMenu, boolean)
     */
    /** {@inheritDoc} */
    @Override
    public void addAppMenuItems(final ActionListener actionListener, final JMenuBar mainMenu, final JMenu fileMenu,
        final boolean forFrame) {
        JMenuItem mi = new JMenuItem(TerminalMessages.getI18n("MENU_OPEN_SSH_SESSION"));
        mi.addActionListener(actionListener);
        mi.setActionCommand(ACTION_OPEN_SSH_SESSION);
        mi.setIcon(AppIcon.getIcon(REMOTE_SHELL_PNG));
        mi.setAccelerator(KeyStroke.getKeyStroke('T', java.awt.event.InputEvent.ALT_DOWN_MASK));

        fileMenu.add(mi);

        if (!forFrame) {
            mi = new JMenuItem(TerminalMessages.getI18n("MENU_OPEN_SSH_SESSION_NEWFRAME"));
            mi.addActionListener(actionListener);
            mi.setActionCommand(ACTION_OPEN_SSH_SESSION_NEWFRAME);
            mi.setIcon(AppIcon.getIcon(REMOTE_SHELL_PNG));

            fileMenu.add(mi);
        } else {
            mi = new JMenuItem(TerminalMessages.getI18n("MENU_OPEN_SSH_SESSION_NEWTAB"));
            mi.addActionListener(actionListener);
            mi.setActionCommand(ACTION_OPEN_SSH_SESSION_NEWTAB);
            mi.setIcon(AppIcon.getIcon(REMOTE_SHELL_PNG));

            fileMenu.add(mi);
        }

        if (LocalPtyFactory.isInteractiveSupported()) {

            mi = new JMenuItem(TerminalMessages.getI18n("MENU_OPEN_LOCAL_SESSION"));
            mi.addActionListener(actionListener);
            mi.setActionCommand(ACTION_OPEN_LOCAL_SESSION);
            mi.setIcon(AppIcon.getIcon(LOCAL_SHELL_PNG));
            mi.setAccelerator(KeyStroke.getKeyStroke('L', java.awt.event.InputEvent.ALT_DOWN_MASK));

            fileMenu.add(mi);

            if (!forFrame) {
                mi = new JMenuItem(TerminalMessages.getI18n("MENU_OPEN_LOCAL_SESSION_NEWFRAME"));
                mi.addActionListener(actionListener);
                mi.setActionCommand(ACTION_OPEN_LOCAL_SESSION_NEWFRAME);
                mi.setIcon(AppIcon.getIcon(LOCAL_SHELL_PNG));

                fileMenu.add(mi);
            } else {
                mi = new JMenuItem(TerminalMessages.getI18n("MENU_OPEN_LOCAL_SESSION_NEWTAB"));
                mi.addActionListener(actionListener);
                mi.setActionCommand(ACTION_OPEN_LOCAL_SESSION_NEWTAB);
                mi.setIcon(AppIcon.getIcon(LOCAL_SHELL_PNG));

                fileMenu.add(mi);
            }
        }

        if (mainMenu != null) {
            /*
             * mi = new
             * JMenuItem(TerminalMessages.getI18n("MENU_OPEN_SFTP_SESSION"));
             * mi.addActionListener(this.controller);
             * mi.setActionCommand(ACTION_OPEN_SFTP_SESSION); m.add(mi);
             */

            this.terminalMenu = new JMenu(TerminalMessages.getI18n("MENU_TERMINAL"));
            this.terminalMenu.setIcon(AppIcon.getIcon("terminal.png"));
            /**
             * TODO JMenu portForwardingMenu = new
             * JMenu(TerminalMessages.getI18n("MENU_PORT_FORWARDING")); mi = new
             * JMenuItem(TerminalMessages.getI18n("MENU_LOCAL_PORT"));
             * mi.addActionListener(actionListener);
             * mi.setActionCommand(ACTION_LOCAL_PORT);
             * portForwardingMenu.add(mi);
             * mi = new JMenuItem(TerminalMessages.getI18n("MENU_REMOTE_PORT"));
             * mi.addActionListener(actionListener);
             * mi.setActionCommand(ACTION_REMOTE_PORT);
             * portForwardingMenu.add(mi);
             * mi = new JMenuItem(getI18n("MENU_X11_FORWARDING"));
             * mi.addActionListener(this.controller);
             * mi.setActionCommand(ACTION_X11_FORWARDING);
             * portForwardingMenu.add(mi);
             * terminalMenu.add(portForwardingMenu);
             */

            mi = new JMenuItem(TerminalMessages.getI18n("MENU_COMPRESSION"));
            mi.addActionListener(actionListener);
            mi.setActionCommand(ACTION_COMPRESSION);
            this.terminalMenu.add(mi);

            mainMenu.add(this.terminalMenu);
        }
    }

    /**
     * Adds the ssh.
     *
     * @param menu
     *            the menu
     * @param actionListener
     *            the action listener
     * @param title
     *            the title
     * @param userhost
     *            the userhost
     */
    private void addSSH(final JMenu menu, final ActionListener actionListener, final String title,
        final String userhost) {
        final JMenuItem mi = new JMenuItem(title);
        mi.setIcon(AppIcon.getIcon(REMOTE_SHELL_PNG));
        mi.setActionCommand(ACTION_SSH + userhost);
        mi.addActionListener(actionListener);
        menu.add(mi);
    }

    /**
     * Adds the seastep ssh.
     *
     * @param sshParent
     *            the ssh parent
     * @param actionListener
     *            the action listener
     */
    @SuppressWarnings("unused")
    private void addApplicationSshMenu(final JMenu sshParent, final ActionListener actionListener) {
        // TODO!
    }

    /**
     * Gets the terminal menu.
     *
     * @return the terminal menu
     */
    public JMenu getTerminalMenu() {
        return this.terminalMenu;
    }

    /**
     * Gets the ssh menu.
     *
     * @return the sshMenu
     */
    public JMenu getSshMenu() {
        return this.sshMenu;
    }

    /**
     * Gets the ssh menu tray.
     *
     * @return the sshMenuTray
     */
    public JMenu getSshMenuTray() {
        return this.sshMenuTray;
    }

    /**
     * Gets the ssh menu.
     *
     * @param parentMenu
     *            the parent menu
     * @param actionListener
     *            the action listener
     */
    public void addSshMenu(final JMenuBar parentMenu, final ActionListener actionListener) {
        if (this.sshMenu == null) {
            this.sshMenu = new JMenu(TerminalMessages.getI18n("MENU_SSH"));
            this.sshMenu.setIcon(AppIcon.getIcon(REMOTE_SHELL_PNG));
            this.addApplicationSshMenu(this.sshMenu, actionListener);

            final JMenu sshMisc = new JMenu(TerminalMessages.getI18n("MENU_SSH_MISC"));
            sshMisc.setIcon(AppIcon.getIcon(REMOTE_SHELL_PNG));

            this.addSSH(sshMisc, actionListener, "root@localhost", "root@localhost");
            this.sshMenu.add(sshMisc);
        }
        parentMenu.add(this.sshMenu);
    }

    /**
     * Adds the ssh menu.
     *
     * @param parentMenu
     *            the parent menu
     * @param actionListener
     *            the action listener
     */
    public void addSshMenuTray(final JPopupMenu parentMenu, final ActionListener actionListener) {
        if (this.sshMenuTray == null) {
            this.sshMenuTray = new JMenu(TerminalMessages.getI18n("MENU_SSH"));
            this.sshMenuTray.setIcon(AppIcon.getIcon(REMOTE_SHELL_PNG));
            this.addApplicationSshMenu(this.sshMenuTray, actionListener);

            final JMenu sshMisc = new JMenu(TerminalMessages.getI18n("MENU_SSH_MISC"));
            sshMisc.setIcon(AppIcon.getIcon(REMOTE_SHELL_PNG));

            this.addSSH(sshMisc, actionListener, "root@localhost", "root@localhost");

            this.sshMenuTray.add(sshMisc);
        }
        parentMenu.add(this.sshMenuTray);
    }
}
