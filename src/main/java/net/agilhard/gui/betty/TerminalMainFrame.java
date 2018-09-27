package net.agilhard.gui.betty;

import java.awt.event.ActionEvent;

import javax.swing.JMenuBar;

import net.agilhard.gui.framework.swing.app.AppMainFrame;
import net.agilhard.gui.framework.swing.edit.EditSupport;
import net.agilhard.gui.framework.swing.edit.EditorMenuHelper;

/**
 * The Class TerminalMainFrame.
 */
@SuppressWarnings("serial")
public class TerminalMainFrame extends AppMainFrame<TerminalManager> {

    /** The factory. */
    private TerminalHandler handler;

    /** The terminal menu helper. */
    private TerminalMenuHelper terminalMenuHelper;

    /** The editor menu helper. */
    private EditorMenuHelper editorMenuHelper;

    /**
     * Instantiates a new terminal main frame.
     */
    public TerminalMainFrame() {
        this(TerminalMessages.getI18n("TERMINAL_APP_TITLE"));
    }

    /**
     * Instantiates a new terminal main frame.
     *
     * @param name
     *            the name
     */
    public TerminalMainFrame(final String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see net.agilhard.guk.framework.swing.app.AppMainFrame#init()
     */
    @Override
    protected void init() {
        this.terminalMenuHelper = new TerminalMenuHelper();
        final JMenuBar mainMenu = this.terminalMenuHelper.getAppMenuBar(this, false);

        this.editorMenuHelper = new EditorMenuHelper();
        this.editorMenuHelper.createEditorMenu(this, false, false, true);
        this.editorMenuHelper.hideAllEditMenu();
        mainMenu.add(this.editorMenuHelper.getEditorMenu());

        this.setJMenuBar(mainMenu);

        this.setAppManager(new TerminalManager(this.getTabbedPane(), this.terminalMenuHelper));
        this.handler = new TerminalHandler(this.getAppManager());
    }

    /* (non-Javadoc)
     * @see net.agilhard.guk.framework.swing.app.AppMainFrame#updateUI()
     */
    @Override
    protected void updateApp() {
        if (this.terminalMenuHelper != null) {
            if (this.getTabbedPane() != null && this.getTabbedPane().getTabCount() > 0) {
                final Object selected = this.getTabbedPane().getSelectedComponent();
                if (this.terminalMenuHelper.getTerminalMenu() != null) {
                    this.terminalMenuHelper.getTerminalMenu().setEnabled(selected instanceof TerminalPanel);
                }
                if (this.editorMenuHelper.getEditorMenu() != null) {
                    if (selected instanceof EditSupport) {
                        this.editorMenuHelper.getEditorMenu().setEnabled(true);
                        ((EditSupport) selected).configureEditMenu(this.editorMenuHelper);
                    } else {
                        this.editorMenuHelper.getEditorMenu().setEnabled(false);
                    }
                }
            } else {
                if (this.terminalMenuHelper.getTerminalMenu() != null) {
                    this.terminalMenuHelper.getTerminalMenu().setEnabled(false);
                    this.editorMenuHelper.getEditorMenu().setEnabled(false);
                }
            }
        }
        super.updateApp();
    }

    /* (non-Javadoc)
     * @see net.agilhard.guk.framework.swing.app.AppMainFrame#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        if (!this.handler.handleActionPerformed(e, false)) {
            super.actionPerformed(e);
        }

    }
}
