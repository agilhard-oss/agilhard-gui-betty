package net.agilhard.gui.betty;

import java.awt.Dimension;

import javax.swing.JTabbedPane;

import net.agilhard.gui.framework.swing.app.AppManager;
import net.agilhard.gui.framework.swing.app.AppMenuHelper;
import net.agilhard.terminal.emulation.RequestOrigin;
import net.agilhard.terminal.emulation.ResizePanelDelegate;

/**
 * The Class TerminalManager.
 */
public class TerminalManager extends AppManager {

    /**
     * Instantiates a new terminal manager.
     */
    public TerminalManager() {
        // .
    }

    /**
     * Instantiates a new terminal manager.
     *
     * @param tabbedPane
     *            the tabbed pane
     * @param menuHelper
     *            the menu helper
     */
    public TerminalManager(final JTabbedPane tabbedPane, final AppMenuHelper menuHelper) {
        super(tabbedPane, menuHelper);
    }

    /**
     * Adds the tab.
     *
     * @param panel
     *            the panel
     */
    void addTab(final TerminalPanel panel) {
        this.addTab(panel, false);
    }

    /**
     * Adds the tab.
     *
     * @param panel
     *            the panel
     * @param openSession
     *            the open session
     */
    void addTab(final TerminalPanel panel, final boolean openSession) {
        final Dimension d = panel.getPreferredSize();
        this.getTabbedPane().add(panel);
        this.getTabbedPane().setSelectedComponent(panel);
        panel.setSize(d);
        panel.getController().getTermPanel().setResizePanelDelegate(new ResizePanelDelegate() {

            @Override
            public void resizedPanel(final Dimension pixelDimension, final RequestOrigin origin) {
                if (origin == RequestOrigin.Remote) {
                    panel.sizePanelForTerm();
                }
            }
        });

        this.addApp(panel.getController());

        if (openSession) {
            panel.getController().openSession();
        }
    }

}
