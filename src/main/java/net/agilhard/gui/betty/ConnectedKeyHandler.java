package net.agilhard.gui.betty;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.agilhard.terminal.emulation.Emulator;
import net.agilhard.terminal.emulation.StoredCursor;

/**
 * The Class ConnectedKeyHandler.
 */
public class ConnectedKeyHandler implements KeyListener {

    /** The Logger. */
    private final Logger log = LoggerFactory.getLogger(ConnectedKeyHandler.class);

    /** The emulator. */
    private final Emulator emulator;

    /** Dos cmd.exe mode flag. */
    private boolean dosCmdFlag;

    /** The cmd buf. */
    private StringBuffer cmdBuf = new StringBuffer();

    /** The stored cursor. */
    private StoredCursor storedCursor;

    /** cmd Index;. */
    private int cmdIdx;

    /**
     * Instantiates a new connected key handler.
     *
     * @param emu
     *            the emu
     */
    public ConnectedKeyHandler(final Emulator emu) {
        this.emulator = emu;
    }

    /**
     * Instantiates a new connected key handler.
     *
     * @param emu
     *            the emu
     * @param dosCmdFlag
     *            the dos cmd.exe flag
     */
    public ConnectedKeyHandler(final Emulator emu, final boolean dosCmdFlag) {
        this.emulator = emu;
        this.dosCmdFlag = dosCmdFlag;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(final KeyEvent e) {
        try {
            final int keycode = e.getKeyCode();
            // this.log.debug("keyPressed keycode=" + keycode + ", dosCmdFlag = " + this.dosCmdFlag);

            final byte[] code = this.emulator.getCode(keycode);
            if (this.dosCmdFlag) {
                if (keycode == 10) {
                    final byte[] obuffer = new byte[2];

                    this.emulator.sendBytes(this.cmdBuf.toString().getBytes("UTF-8"));
                    if (this.storedCursor != null) {
                        this.emulator.getTerminalWriter().restoreCursor(this.storedCursor);
                    }
                    this.storedCursor = null;
                    this.cmdBuf = new StringBuffer();
                    this.cmdIdx = 0;

                    obuffer[0] = (byte) '\r';
                    obuffer[1] = (byte) '\n';
                    this.emulator.getTerminalWriter().carriageReturn();
                    this.emulator.getTerminalWriter().newLine();
                    this.emulator.sendBytes(obuffer);
                } else if (keycode == 8) {
                    if (this.cmdIdx > 0) {
                        this.emulator.getTerminalWriter().backspace();
                        this.emulator.getTerminalWriter().writeChar(' ');
                        this.emulator.getTerminalWriter().backspace();
                        this.cmdBuf.deleteCharAt(--this.cmdIdx);
                    }
                } else if (code == null) {
                    final char keychar = e.getKeyChar();
                    if ((keychar & 0xff00) == 0) {
                        this.cmdBuf.append(keychar);
                        this.cmdIdx++;
                        if (this.storedCursor == null) {
                            this.storedCursor = new StoredCursor();
                            this.emulator.getTerminalWriter().storeCursor(this.storedCursor);
                        }
                        this.emulator.getTerminalWriter().writeChar(keychar);
                    }
                }
            } else if (code != null) {
                //if ((e.getModifiers() & java.awt.event.InputEvent.ALT_DOWN_MASK) == 0) {
                this.emulator.sendBytes(code);
                //}
            } else {
                //if ((e.getModifiers() & java.awt.event.InputEvent.ALT_DOWN_MASK) == 0) {

                final char keychar = e.getKeyChar();

                final byte[] obuffer = new byte[1];
                if ((keychar & 0xff00) == 0) {
                    obuffer[0] = (byte) e.getKeyChar();
                    this.emulator.sendBytes(obuffer);
                }
                //}
            }
        }
        catch (final IOException ex) {
            this.log.error("Error sending key to emulator", ex);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        final char keychar = e.getKeyChar();
        if ((keychar & 0xff00) != 0) {
            final char[] foo = new char[1];
            foo[0] = keychar;
            this.log.warn("keyTyped keychar=" + keychar);
            try {
                final byte[] bytes = new String(foo).getBytes("EUC-JP");
                this.emulator.sendBytes(bytes);
            }
            catch (final IOException ex) {
                this.log.error("Error sending key to emulator", ex);
            }
        }
    }

    // Ignore releases
    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        // .
    }
}
