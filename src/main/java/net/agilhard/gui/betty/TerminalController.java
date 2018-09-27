package net.agilhard.gui.betty;

import static net.agilhard.gui.betty.TerminalConstants.ACTION_COMPRESSION;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_LOCAL_PORT;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_SFTP_SESSION;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_OPEN_SSH_SESSION;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_REMOTE_PORT;
import static net.agilhard.gui.betty.TerminalConstants.ACTION_X11_FORWARDING;
import static net.agilhard.gui.framework.swing.edit.TextEditorConstants.ACTION_COPY;
import static net.agilhard.gui.framework.swing.edit.TextEditorConstants.ACTION_PASTE;
import static net.agilhard.terminal.emulation.TerminalEmulationConstants.LOCAL_EXEC;
import static net.agilhard.terminal.emulation.TerminalEmulationConstants.LOCAL_SHELL;
import static net.agilhard.terminal.emulation.TerminalEmulationConstants.SFTP;
import static net.agilhard.terminal.emulation.TerminalEmulationConstants.SHELL;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.agilhard.gui.framework.swing.app.AppController;
import net.agilhard.jsch.JSchException;
import net.agilhard.jsch.Session;
import net.agilhard.terminal.emulation.BackBuffer;
import net.agilhard.terminal.emulation.Emulator;
import net.agilhard.terminal.emulation.RequestOrigin;
import net.agilhard.terminal.emulation.ResizePanelDelegate;
import net.agilhard.terminal.emulation.ScrollBuffer;
import net.agilhard.terminal.emulation.StyleState;
import net.agilhard.terminal.emulation.TerminalEmulationController;
import net.agilhard.terminal.emulation.TerminalWriter;
import net.agilhard.terminal.emulation.Tty;
import net.agilhard.terminal.emulation.TtyChannel;
import net.agilhard.terminal.emulation.jsch.JSchTty;
import net.agilhard.terminal.emulation.shell.LocalPtyFactory;
import net.agilhard.terminal.emulation.shell.PtyWindowsCmd;
import net.agilhard.terminal.emulation.swing.TermPanel;

/**
 * The Class TerminalController.
 */
public class TerminalController extends AppController<TerminalPanel, TerminalFrame>
    implements TerminalEmulationController {

    /** The Constant EXCEPTION. */
    private static final String EXCEPTION = "EXCEPTION:";

    /** The Logger. */
    private final Logger log = LoggerFactory.getLogger(TerminalController.class);

    /** The mode. */
    private int mode = SHELL;

    /** The xhost. */
    private String xhost = "127.0.0.1";

    /** The xport. */
    private int xport;

    /** The xforwarding. */
    private boolean xforwarding;

    /** The tty. */
    private Tty tty;

    /** The tty channel. */
    private TtyChannel ttyChannel;

    /** The terminal writer. */
    private final TerminalWriter terminalWriter;

    /** The emulator. */
    private Emulator emulator;

    /** The term panel. */
    private final TermPanel termPanel;

    /** The emu thread. */
    private Thread emuThread;

    /** The terminal dialog. */
    private final TerminalDialog terminalDialog;

    /** The style state. */
    private final StyleState styleState;

    /** The back buffer. */
    private final BackBuffer backBuffer;

    /** The scroll buffer. */
    private final ScrollBuffer scrollBuffer;

    /** The compression. */
    private int compression;

    /** The close on error. */
    private boolean closeOnError = true;

    /** The user. */
    private String user;

    /** The host. */
    private String host;

    /** The password. */
    private final String password;

    /** The command. */
    private final String command;

    /** The directory. */
    private final String directory;

    /** The exit status. */
    private int exitStatus;

    /**
     * Instantiates a new terminal controller.
     *
     * @param panel
     *            the panel
     * @param mode
     *            the mode
     */
    public TerminalController(final TerminalPanel panel, final int mode) {
        this(panel, null, null, null, mode);
    }

    /**
     * Instantiates a new terminal controller.
     *
     * @param panel
     *            the panel
     * @param host
     *            the host
     * @param user
     *            the user
     * @param password
     *            the password
     * @param mode
     *            the mode
     */
    public TerminalController(final TerminalPanel panel, final String host, final String user, final String password,
        final int mode) {
        this(panel, host, user, password, null, null, mode);
    }

    /**
     * Instantiates a new terminal controller.
     *
     * @param panel
     *            the panel
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
    public TerminalController(final TerminalPanel panel, final String host, final String user, final String password,
        final String command, final int mode) {
        this(panel, host, user, password, command, null, mode);
    }

    /**
     * Instantiates a new terminal controller.
     *
     * @param panel
     *            the panel
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
    public TerminalController(final TerminalPanel panel, final String host, final String user, final String password,
        final String command, final String directory, final int mode) {

        super(panel);
        this.host = host;
        this.user = user;
        this.password = password;
        this.command = command;
        this.directory = directory;

        this.mode = mode;

        this.styleState = new StyleState();
        this.backBuffer = new BackBuffer(80, 24, this.styleState);
        this.scrollBuffer = new ScrollBuffer();
        this.termPanel = new TermPanel(this.backBuffer, this.scrollBuffer, this.styleState);
        this.terminalWriter = new TerminalWriter(this.termPanel, this.backBuffer, this.styleState);
        this.terminalDialog = new TerminalDialog(panel);

        this.getManager().addApp(this);
    }

    /**
     * Sets the tty.
     *
     * @param tty
     *            the new tty
     */
    public void setTty(final Tty tty) {
        this.tty = tty;
        this.ttyChannel = new TtyChannel(tty);

        this.emulator = new Emulator(this.terminalWriter, this.ttyChannel, this);
        this.termPanel.setEmulator(this.emulator);
    }

    /**
     * Gets the term panel.
     *
     * @return the term panel
     */
    public TermPanel getTermPanel() {
        return this.termPanel;
    }

    /**
     * Send command.
     *
     * @param string
     *            the string
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void sendCommand(final String string) throws IOException {
        if (this.isSessionRunning()) {
            this.ttyChannel.sendBytes(string.getBytes());
        }
    }

    /**
     * Start.
     */
    public void start() {
        if (!this.isSessionRunning()) {
            this.exitStatus = 0;
            this.emuThread = new Thread(new EmulatorTask());
            this.emuThread.start();
        } else {
            this.log.error("Should not try to start session again at this point... ");
        }
    }

    /**
     * Stop.
     */
    public void stop() {
        if (this.isSessionRunning() && this.emuThread != null) {
            this.emuThread.interrupt();
        }
        this.getManager().removeApp(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.agilhard.guk.framework.swing.app.AppController#detach()
     */
    /** {@inheritDoc} */
    @Override
    public void detach() {
        if (this.getFrame() == null) {
            this.setFrame(new TerminalFrame(this.getPanel()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see net.agilhard.guk.framework.swing.app.AppController#close()
     */
    /** {@inheritDoc} */
    @Override
    public void close() {
        this.stop();
    }

    /**
     * Checks if is session running.
     *
     * @return true, if is session running
     */
    public boolean isSessionRunning() {
        return this.emulator == null ? false : this.emulator.getSessionRunning().get();
    }

    /**
     * The Class EmulatorTask.
     */
    class EmulatorTask implements Runnable {

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
        /** {@inheritDoc} */
        @Override
        @SuppressWarnings({ "synthetic-access", "boxing" })
        public void run() {
            boolean hasError = false;
            try {
                TerminalController.this.emulator.getSessionRunning().set(true);
                Thread.currentThread().setName(TerminalController.this.tty.getName());
                if (TerminalController.this.tty.init(TerminalController.this.terminalDialog,
                    TerminalController.this.terminalDialog)) {
                    Thread.currentThread().setName(TerminalController.this.tty.getName());
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (TerminalController.this.tty instanceof PtyWindowsCmd
                                && ((PtyWindowsCmd) TerminalController.this.tty).isInteractive()) {
                                TerminalController.this.termPanel
                                    .setKeyHandler(new ConnectedKeyHandler(TerminalController.this.emulator, true));
                            } else {
                                TerminalController.this.termPanel
                                    .setKeyHandler(new ConnectedKeyHandler(TerminalController.this.emulator));
                            }
                            TerminalController.this.termPanel.requestFocusInWindow();
                        }
                    });

                    if (TerminalController.this.getPanel() != null && TerminalController.this.tty instanceof JSchTty) {
                        final JSchTty jschTty = (JSchTty) TerminalController.this.tty;
                        final String name = jschTty.getUser() + "@" + jschTty.getHost()
                            + (jschTty.getPort() != 22 ? ":" + new Integer(jschTty.getPort()).toString() : "");
                        TerminalController.this.getPanel().setName(name);
                    }
                    TerminalController.this.emulator.start();
                } else {
                    hasError = true;
                }

            }
            catch (final Exception e) {
                TerminalController.this.log.error(EXCEPTION, e);

            } finally {

                try {
                    TerminalController.this.tty.close();
                    TerminalController.this.exitStatus = TerminalController.this.tty.getExitStatus();
                }
                catch (final Exception e) {
                    TerminalController.this.log.debug(EXCEPTION, e);
                }
                TerminalController.this.emulator.getSessionRunning().set(false);
                if (hasError && TerminalController.this.isCloseOnError()
                    || !hasError && TerminalController.this.isCloseOnExit()) {
                    TerminalController.this.close();
                } else if (hasError) {
                    TerminalController.this.emulator.getTerminalWriter().carriageReturn();
                    TerminalController.this.emulator.getTerminalWriter().newLine();
                    TerminalController.this.emulator.getTerminalWriter()
                        .writeString(TerminalMessages.getI18n("TERMINAL_TERMINATED_WITH_ERROR"));
                    TerminalController.this.emulator.getTerminalWriter().carriageReturn();
                    TerminalController.this.emulator.getTerminalWriter().newLine();
                } else {
                    TerminalController.this.emulator.getTerminalWriter().carriageReturn();
                    TerminalController.this.emulator.getTerminalWriter().newLine();
                    TerminalController.this.emulator.getTerminalWriter().writeString(
                        TerminalMessages.getI18n("TERMINAL_TERMINATED", TerminalController.this.exitStatus));
                    TerminalController.this.emulator.getTerminalWriter().carriageReturn();
                    TerminalController.this.emulator.getTerminalWriter().newLine();
                }
            }
        }
    }

    /**
     * Gets the buffer text.
     *
     * @param type
     *            the type
     * @return the buffer text
     */
    public String getBufferText(final BufferType type) {
        return type.getValue(this.getPanel());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.agilhard.guk.framework.swing.app.AppController#setFrame(net.agilhard.guk.framework.swing.app.AppFrame
     * )
     */
    /** {@inheritDoc} */
    @Override
    public void setFrame(final TerminalFrame frame) {
        this.removeFrameWindowListener();
        if (this.getManager() != null) {
            this.getManager().removeFromTabs(this);
        }

        this.termPanel.setResizePanelDelegate(new ResizePanelDelegate() {

            @Override
            public void resizedPanel(final Dimension pixelDimension, final RequestOrigin origin) {
                if (origin == RequestOrigin.Remote) {
                    frame.sizeFrameForTerm();

                }
            }
        });
        super.setFrame(frame);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.agilhard.guk.framework.swing.app.AppController#actionPerformed(java.awt.event.ActionEvent
     * )
     */
    /** {@inheritDoc} */
    @Override
    public void actionPerformed(final ActionEvent e) {
        final String action = e.getActionCommand();

        if (!this.isSessionRunning()
            && (action.equals(ACTION_OPEN_SSH_SESSION) || action.equals(ACTION_OPEN_SFTP_SESSION))) {

            if (action.equals(ACTION_OPEN_SSH_SESSION)) {
                this.mode = SHELL;
            } else if (action.equals(ACTION_OPEN_SFTP_SESSION)) {
                this.mode = SFTP;
            }
            this.openSession();

        } else if (action.equals(ACTION_X11_FORWARDING)) {
            final String display = JOptionPane.showInputDialog(this.getPanel(),
                TerminalMessages.getI18n("XDISPLAY_NAME"), this.xhost == null ? "" : this.xhost + ":" + this.xport);
            try {
                if (display != null) {
                    this.xhost = display.substring(0, display.indexOf(':'));
                    this.xport = Integer.parseInt(display.substring(display.indexOf(':') + 1));
                    this.xforwarding = true;
                }
            }
            catch (final Exception ee) {
                this.xforwarding = false;
                this.xhost = null;
            }
        } else if (action.equals(ACTION_COMPRESSION)) {
            final String foo = JOptionPane.showInputDialog(this.getPanel(),
                TerminalMessages.getI18n("COMPRESSION_LEVEL"), new Integer(this.compression).toString());
            try {
                if (foo != null) {
                    this.compression = Integer.parseInt(foo);
                    this.setCompression(this.compression);
                }
            }
            catch (final Exception ee) {
                this.log.debug(EXCEPTION, ee);
            }
        } else if (action.equals(ACTION_LOCAL_PORT) || action.equals(ACTION_REMOTE_PORT)) {
            if (this.tty instanceof JSchTty) {
                return;
            }
            final Session session = ((JSchTty) this.tty).getSession();
            if (session == null) {
                JOptionPane.showMessageDialog(this.getPanel(), TerminalMessages.getI18n("ESTABLISH_CONNECTION"));
                return;
            }

            try {
                String title = "";
                if (action.equals(ACTION_LOCAL_PORT)) {
                    title += TerminalMessages.getI18n("LOCAL_PORT_FORWARDING");
                } else {
                    title += TerminalMessages.getI18n("REMOTE_PORT_FORWARDING");
                }
                title += TerminalMessages.getI18n("PORT_HOST_HOSTPORT");

                String foo = JOptionPane.showInputDialog(this.getPanel(), title, "");
                if (foo == null) {
                    return;
                }
                final int port1 = Integer.parseInt(foo.substring(0, foo.indexOf(':')));
                foo = foo.substring(foo.indexOf(':') + 1);
                final String hostspec = foo.substring(0, foo.indexOf(':'));
                final int port2 = Integer.parseInt(foo.substring(foo.indexOf(':') + 1));

                if (action.equals(ACTION_LOCAL_PORT)) {
                    this.setPortForwardingL(port1, hostspec, port2);
                } else {
                    this.setPortForwardingR(port1, hostspec, port2);
                }
            }
            catch (final Exception ee) {
                this.log.debug(EXCEPTION, ee);
            }
        } else if (action.equals(ACTION_PASTE)) {
            if (this.getTermPanel() != null) {
                this.getTermPanel().pasteClipboard();
            }
        } else if (action.equals(ACTION_COPY)) {
            if (this.getTermPanel() != null) {
                this.getTermPanel().copyClipboard();
            }
        } else {
            super.actionPerformed(e);
        }
    }

    /**
     * Sets the x host.
     *
     * @param csXhost
     *            the new x host
     */
    public void setXHost(final String csXhost) {
        this.xhost = csXhost;
    }

    /**
     * Sets the x port.
     *
     * @param csXport
     *            the new x port
     */
    public void setXPort(final int csXport) {
        this.xport = csXport;
    }

    /**
     * Sets the x forwarding.
     *
     * @param csXforwarding
     *            the new x forwarding
     */
    public void setXForwarding(final boolean csXforwarding) {
        // TODO implement X Forwarding
        this.xforwarding = csXforwarding;
    }

    /**
     * Checks if is xforwarding.
     *
     * @return true, if is xforwarding
     */
    public boolean isXforwarding() {
        return this.xforwarding;
    }

    /**
     * Sets the compression.
     *
     * @param csCompression
     *            the new compression
     */
    public void setCompression(final int csCompression) {

        if (!(this.tty instanceof JSchTty)) {
            return;
        }
        final Session session = ((JSchTty) this.tty).getSession();

        if (csCompression < 0 || 9 < csCompression) {
            return;
        }
        this.compression = csCompression;
        if (session != null) {
            if (csCompression == 0) {
                session.setConfig("compression.s2c", "none");
                session.setConfig("compression.c2s", "none");
                session.setConfig("compression_level", "0");
            } else {
                session.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
                session.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");
                session.setConfig("compression_level", new Integer(csCompression).toString());
            }
            try {
                session.rekey();
            }
            catch (final Exception e) {
                this.log.warn("exception in setCompression", e);
            }
        }
    }

    /**
     * Gets the compression.
     *
     * @return the compression
     */
    public int getCompression() {
        return this.compression;
    }

    /**
     * Sets the port forwarding l.
     *
     * @param port1
     *            the port1
     * @param csHost
     *            the host
     * @param port2
     *            the port2
     */
    public void setPortForwardingL(final int port1, final String csHost, final int port2) {
        if (!(this.tty instanceof JSchTty)) {
            return;
        }
        final Session session = ((JSchTty) this.tty).getSession();
        try {
            session.setPortForwardingL(port1, csHost, port2);
        }
        catch (final JSchException e) {
            this.log.debug(EXCEPTION, e);
        }
    }

    /**
     * Sets the port forwarding r.
     *
     * @param port1
     *            the port1
     * @param csHost
     *            the host
     * @param port2
     *            the port2
     */
    public void setPortForwardingR(final int port1, final String csHost, final int port2) {
        if (!(this.tty instanceof JSchTty)) {
            return;
        }
        final Session session = ((JSchTty) this.tty).getSession();
        try {
            session.setPortForwardingR(port1, csHost, port2);
        }
        catch (final JSchException e) {
            this.log.debug(EXCEPTION, e);
        }
    }

    /**
     * Open session.
     */
    public void openSession() {
        if (this.mode == LOCAL_SHELL || this.mode == LOCAL_EXEC) {
            this.openLocalSession();
            return;
        }
        if (!this.isSessionRunning()) {
            // setTty(new JNAPtyLinux());
            this.setTty(new JSchTty(this.host, this.user, this.password, this.mode));
            this.start();
        }
    }

    /**
     * Open local session.
     */
    public void openLocalSession() {
        if (LocalPtyFactory.isInteractiveSupported()) {
            if (!this.isSessionRunning()) {
                this.log.debug("command=" + this.command);
                if (this.command != null) {
                    this.setTty(LocalPtyFactory.createPty(this.command, this.directory));
                } else {
                    this.setTty(LocalPtyFactory.createPty());
                }
                this.start();
            }
        }
    }

    /**
     * Open local session.
     *
     * @param csCommand
     *            the command
     */
    public void openLocalSession(final String csCommand) {
        if (LocalPtyFactory.isInteractiveSupported()) {
            if (!this.isSessionRunning()) {
                this.setTty(LocalPtyFactory.createPty(csCommand));
                this.start();
            }
        }
    }

    /**
     * Checks if is close_on_error.
     *
     * @return true, if is close_on_error
     */
    @Override
    public boolean isCloseOnError() {
        return this.closeOnError;
    }

    /**
     * Sets the user.
     *
     * @param user
     *            the new user
     */
    public void setUser(final String user) {
        this.user = user;
    }

    /**
     * Sets the host.
     *
     * @param host
     *            the new host
     */
    public void setHost(final String host) {
        this.host = host;
    }

    /**
     * Sets the close on error.
     *
     * @param closeOnError
     *            the new close on error
     */
    public void setCloseOnError(final boolean closeOnError) {
        this.closeOnError = closeOnError;
    }

    /**
     * Gets the exit status.
     *
     * @return the exitStatus
     */
    public int getExitStatus() {
        return this.exitStatus;
    }

}
