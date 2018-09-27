package net.agilhard.gui.betty;

import static net.agilhard.terminal.emulation.TerminalEmulationConstants.LOCAL_EXEC;
import static net.agilhard.terminal.emulation.TerminalEmulationConstants.SHELL;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import net.agilhard.gui.framework.swing.app.AbstractAppPanel;
import net.agilhard.gui.framework.swing.app.AppManager;
import net.agilhard.gui.framework.swing.edit.EditSupport;
import net.agilhard.gui.framework.swing.edit.EditorMenuHelper;
import net.agilhard.jschutil.JSchConfiguration;
import net.agilhard.jschutil.JSchConfigurationRepository;
import net.agilhard.terminal.emulation.SelectionListener;

/**
 * The Class TerminalPanel.
 */
public class TerminalPanel extends AbstractAppPanel<TerminalController>
    implements EditSupport, SelectionListener, FocusListener {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8213232075937432833L;

    /** The scroll bar. */
    private final JScrollBar scrollBar;

    /** The editor menu helper. */
    private EditorMenuHelper editorMenuHelper;

    /** The selection available. */
    private boolean selectionAvailable;

    /**
     * Instantiates a new terminal panel.
     *
     * @param manager
     *            the manager
     */
    public TerminalPanel(final AppManager manager) {
        this(manager, SHELL);
    }

    /**
     * Instantiates a new terminal panel.
     *
     * @param manager
     *            the manager
     * @param mode
     *            the mode
     */
    public TerminalPanel(final AppManager manager, final int mode) {
        this(manager, null, null, null, mode);
    }

    /**
     * Instantiates a new terminal panel.
     *
     * @param manager
     *            the manager
     * @param userhost
     *            the userhost
     */
    public TerminalPanel(final AppManager manager, final String userhost) {
        this(manager, userhost, SHELL);
    }

    /**
     * Instantiates a new terminal panel.
     *
     * @param manager
     *            the manager
     * @param userhost
     *            the userhost
     * @param mode
     *            the mode
     */
    public TerminalPanel(final AppManager manager, final String userhost, final int mode) {
        super(manager, new GridLayout());
        String user = null;
        String host = null;

        if (userhost != null) {
            final int i = userhost.indexOf('@');
            user = userhost.substring(0, i > -1 ? i : userhost.length());
            if (i > -1 && i + 1 < userhost.length()) {
                host = userhost.substring(i + 1);
            }
        }

        this.setController(new TerminalController(this, host, user, null, mode));

        final JPanel parentPanel = new JPanel(new BorderLayout());
        this.setName(TerminalMessages.getI18n("TERMINAL_TITLE"));
        this.scrollBar = new JScrollBar();

        parentPanel.add(this.getController().getTermPanel(), BorderLayout.CENTER);
        parentPanel.add(this.scrollBar, BorderLayout.EAST);
        this.scrollBar.setModel(this.getController().getTermPanel().getBoundedRangeModel());

        this.getController().getTermPanel().addSelectionListener(this);

        this.add(parentPanel);
    }

    /**
     * Instantiates a new terminal panel.
     *
     * @param manager
     *            the manager
     * @param host
     *            the host
     * @param user
     *            the user
     * @param password
     *            the password
     * @param mode
     *            the mode
     */
    public TerminalPanel(final AppManager manager, final String host, final String user, final String password,
        final int mode) {
        this(manager, host, user, password, null, null, mode);
    }

    /**
     * Instantiates a new terminal panel.
     *
     * @param manager
     *            the manager
     * @param host
     *            the host
     * @param user
     *            the user
     * @param password
     *            the password
     * @param command
     *            the command
     * @param mode
     *            the mode
     */
    public TerminalPanel(final AppManager manager, final String host, final String user, final String password,
        final String command, final int mode) {
        this(manager, host, user, password, command, null, mode);
    }

    /**
     * Instantiates a new terminal panel.
     *
     * @param manager
     *            the manager
     * @param host
     *            the host
     * @param user
     *            the user
     * @param password
     *            the password
     * @param command
     *            the command
     * @param directory
     *            the directory
     * @param mode
     *            the mode
     */
    public TerminalPanel(final AppManager manager, final String host, final String user, final String password,
        final String command, final String directory, final int mode) {
        super(manager, new BorderLayout());
        this.setController(new TerminalController(this, host, user, password, command, directory, mode));

        this.getController().setCloseOnExit(!(mode == LOCAL_EXEC));
        this.getController().setCloseOnError(!(mode == LOCAL_EXEC));

        this.setName(TerminalMessages.getI18n("TERMINAL_TITLE"));
        this.scrollBar = new JScrollBar();

        this.add(this.getController().getTermPanel(), BorderLayout.CENTER);
        this.getController().getTermPanel().addSelectionListener(this);

        this.add(this.scrollBar, BorderLayout.EAST);
        this.scrollBar.setModel(this.getController().getTermPanel().getBoundedRangeModel());
    }

    /**
     * Instantiates a new terminal panel.
     *
     * @param manager
     *            the manager
     * @param command
     *            the command
     * @param directory
     *            the directory
     */
    public TerminalPanel(final AppManager manager, final String command, final String directory) {
        this(manager, null, null, null, command, directory, LOCAL_EXEC);
    }

    /** The default cr. */
    private static JSchConfigurationRepository defaultCR = new JSchConfigurationRepository() {

        private final JSchConfiguration conf = new JSchConfiguration();

        @Override
        public JSchConfiguration load(final String name) {
            return this.conf;
        }

        @Override
        public void save(final JSchConfiguration csConf) {}
    };

    /* (non-Javadoc)
     * @see net.agilhard.guk.framework.swing.app.AppPanel#getController()
     */
    /** {@inheritDoc} */
    @Override
    public TerminalController getController() {
        return super.getController();
    }

    /**
     * Gets the scroll bar.
     *
     * @return the scroll bar
     */
    public JScrollBar getScrollBar() {
        return this.scrollBar;
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#getPreferredSize()
     */
    /** {@inheritDoc} */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
            this.getController().getTermPanel().getPixelWidth() + this.scrollBar.getPreferredSize().width,
            this.getController().getTermPanel().getPixelHeight());
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#requestFocusInWindow()
     */
    /** {@inheritDoc} */
    @Override
    public boolean requestFocusInWindow() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                TerminalPanel.this.getController().getTermPanel().requestFocusInWindow();
            }
        });
        return super.requestFocusInWindow();
    }

    /**
     * Size panel for term.
     */
    public void sizePanelForTerm() {
        final Dimension d = this.getPreferredSize();
        this.setSize(d);
    }

    /** {@inheritDoc} */
    @Override
    public void configureEditMenu(final EditorMenuHelper helper) {
        if (helper == null) {
            return;
        }
        this.editorMenuHelper = helper;
        helper.showCopyPasteEditMenu();
        helper.getCopyMenuItem().setEnabled(this.selectionAvailable);
    }

    /** {@inheritDoc} */
    @Override
    public void selectionChanged(final Point selectionStart, final Point selectionEnd) {
        this.selectionAvailable = selectionStart != null && selectionEnd != null;
        this.configureEditMenu(this.editorMenuHelper);
    }

    /**
     * Get default configurationRepository.
     *
     * @return
     */
    JSchConfigurationRepository getDefaultConfigurationRepository() {
        return defaultCR;
    }

    /** {@inheritDoc} */
    @Override
    public void focusGained(final FocusEvent e) {
        this.getController().getTermPanel().requestFocusInWindow();
    }

    /** {@inheritDoc} */
    @Override
    public void focusLost(final FocusEvent e) {}

}
