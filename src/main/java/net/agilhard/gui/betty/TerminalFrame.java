package net.agilhard.gui.betty;

import static net.agilhard.terminal.emulation.TerminalEmulationConstants.SHELL;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JMenuBar;

import net.agilhard.gui.framework.swing.app.AppFrame;
import net.agilhard.gui.framework.swing.app.AppManager;
import net.agilhard.gui.framework.swing.edit.EditorMenuHelper;

/**
 * The Class TerminalFrame.
 */
public class TerminalFrame extends AppFrame<TerminalController, TerminalPanel> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2401044503049118784L;

    /** The factory. */
    private final TerminalHandler handler;

    /**
     * Instantiates a new terminal frame.
     *
     * @param manager
     *            the manager
     */
    public TerminalFrame(final AppManager manager) {
        this(manager, SHELL);
    }

    /**
     * Instantiates a new terminal frame.
     *
     * @param manager
     *            the manager
     * @param mode
     *            the mode
     */
    public TerminalFrame(final AppManager manager, final int mode) {
        this(manager, mode, false);
    }

    /**
     * Instantiates a new terminal frame.
     *
     * @param manager
     *            the manager
     * @param mode
     *            the mode
     * @param openSession
     *            the open session
     */
    public TerminalFrame(final AppManager manager, final int mode, final boolean openSession) {
        super(TerminalMessages.getI18n("TERMINAL_TITLE"));
        this.setPanel(new TerminalPanel(manager, mode));
        this.setController(this.getPanel().getController());
        this.handler = new TerminalHandler(manager);
        this.init(openSession);
    }

    /**
     * Instantiates a new terminal frame.
     *
     * @param panel
     *            the panel
     */
    public TerminalFrame(final TerminalPanel panel) {
        super();
        this.setPanel(panel);
        this.setTitle(panel.getName());
        this.setController(panel.getController());
        this.handler = new TerminalHandler(this.getController().getManager());

        this.init(false);
    }

    /**
     * Instantiates a new terminal frame.
     *
     * @param manager
     *            the manager
     * @param userhost
     *            the userhost
     */
    public TerminalFrame(final AppManager manager, final String userhost) {
        super(TerminalMessages.getI18n("TERMINAL_TITLE"));
        this.setPanel(new TerminalPanel(manager, SHELL));
        this.setController(this.getPanel().getController());
        this.handler = new TerminalHandler(manager);
        this.init(true, userhost);
    }

    /**
     * Inits the.
     *
     * @param openSession
     *            the open session
     */
    private void init(final boolean openSession) {
        this.init(openSession, null);
    }

    /**
     * Inits the.
     *
     * @param openSession
     *            the open session
     * @param userhost
     *            the userhost
     */
    private void init(final boolean openSession, final String userhost) {
        this.getController().setCloseOnExit(true);

        this.enableEvents(AWTEvent.KEY_EVENT_MASK);

        this.getContentPane().add("Center", this.getPanel());

        final TerminalMenuHelper terminalMenuHelper = new TerminalMenuHelper();
        final JMenuBar mainMenu = terminalMenuHelper.getAppMenuBar(this, true);

        final EditorMenuHelper editorMenuHelper = new EditorMenuHelper();
        editorMenuHelper.createEditorMenu(this, false, false, true);
        editorMenuHelper.hideAllEditMenu();
        mainMenu.add(editorMenuHelper.getEditorMenu());

        this.getPanel().configureEditMenu(editorMenuHelper);

        this.setJMenuBar(mainMenu);

        this.pack();
        this.setVisible(true);
        this.setResizable(true);

        this.getController().setFrame(this);
        // controller.setXForwarding(false);

        if (userhost != null) {
            String user = null;
            String host = null;

            final int i = userhost.indexOf('@');
            user = userhost.substring(0, i > -1 ? i : userhost.length());
            if (i > -1 && i + 1 < userhost.length()) {
                host = userhost.substring(i + 1);
            }
            this.getController().setUser(user);
            this.getController().setHost(host);
        }

        if (openSession) {
            this.getController().openSession();
        }
    }

    /**
     * Size frame for term.
     */
    public void sizeFrameForTerm() {
        final Dimension d = this.getPanel().getPreferredSize();

        d.width += this.getWidth() - this.getContentPane().getWidth();
        d.height += this.getHeight() - this.getContentPane().getHeight();
        this.setSize(d);
    }

    /* (non-Javadoc)
     * @see net.agilhard.guk.framework.swing.app.AppFrame#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (this.handler != null && !this.handler.handleActionPerformed(e, true)) {
            this.getController().actionPerformed(e);
        }
    }

}
