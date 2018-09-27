package net.agilhard.gui.betty;

/**
 * The Enum BufferType.
 */
public enum BufferType {

    /** The Back. */
    Back() {

        @Override
        String getValue(final TerminalPanel term) {
            return term.getController().getTermPanel().getTermBackBuffer().getLines();
        }
    },

    /** The Back style. */
    BackStyle() {

        @Override
        String getValue(final TerminalPanel term) {
            return term.getController().getTermPanel().getTermBackBuffer().getStyleLines();
        }
    },

    /** The Damage. */
    Damage() {

        @Override
        String getValue(final TerminalPanel term) {
            return term.getController().getTermPanel().getTermBackBuffer().getDamageLines();
        }
    },

    /** The Scroll. */
    Scroll() {

        @Override
        String getValue(final TerminalPanel term) {
            return term.getController().getTermPanel().getScrollBuffer().getLines();
        }
    };

    /**
     * Gets the value.
     *
     * @param term
     *            the term
     * @return the value
     */
    abstract String getValue(TerminalPanel term);
}