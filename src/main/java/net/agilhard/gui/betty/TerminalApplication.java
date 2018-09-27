package net.agilhard.gui.betty;

import java.util.Locale;

import javax.swing.SwingUtilities;
// import javax.swing.UIManager;
// import javax.swing.UnsupportedLookAndFeelException;

import net.agilhard.gui.framework.swing.app.AppUtil;
import net.agilhard.jschutil.JSchUtil;

/**
 * The Class TerminalApplication.
 */
public final class TerminalApplication {

    /**
     * Private Constructor.
     */
    private TerminalApplication() {
        // .
    }

    /**
     * The main method.
     *
     * @param arg
     *            the arguments
     */
    public static void main(final String[] arg) {

        Locale.setDefault(Locale.ENGLISH);

        AppUtil.quickAndDirtyFixForProblemWithWebStartInJava7u25();

        JSchUtil.useSSHAgent(true);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new TerminalMainFrame();
            }
        });
    }
}
