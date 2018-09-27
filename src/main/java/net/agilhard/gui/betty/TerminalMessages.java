package net.agilhard.gui.betty;

import java.util.ResourceBundle;

import net.agilhard.gui.framework.swing.app.AppMessages;

/**
 * The Class TerminalMessages.
 */
public final class TerminalMessages {

    /**
     * The resource bundle.
     */
    static final ResourceBundle bundle = ResourceBundle.getBundle("net.agilhard.gui.betty.TerminalMessages");

    /**
     * Private constructor for utility class.
     */
    private TerminalMessages() {
        // .
    }

    /**
     * Gets the i18n.
     *
     * @param key
     *            the key
     * @param args
     *            the args
     * @return the i18n
     */
    public static String getI18n(final String key, final Object... args) {
        return AppMessages.getI18n(bundle, key, args);
    }
}
