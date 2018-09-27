/**
 *
 */
package net.agilhard.gui.betty;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 * The Class BufferPanel.
 */
@SuppressWarnings("serial")
public class BufferPanel extends JPanel {

    /**
     * Instantiates a new buffer panel.
     *
     * @param terminal
     *            the terminal
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BufferPanel(final TerminalPanel terminal) {
        super(new BorderLayout());
        final JTextArea area = new JTextArea();
        this.add(area, BorderLayout.NORTH);

        final BufferType[] choices = BufferType.values();

        final JComboBox chooser = new JComboBox(choices);
        this.add(chooser, BorderLayout.NORTH);

        area.setFont(Font.decode("Monospaced-14"));
        this.add(new JScrollPane(area), BorderLayout.CENTER);

        /**
         * The class Updater.
         */
        class Updater implements ActionListener, ItemListener {

            /**
             * Update.
             */
            void update() {
                final BufferType type = (BufferType) chooser.getSelectedItem();
                final String text = terminal.getController().getBufferText(type);
                area.setText(text);
            }

            @Override
            public void actionPerformed(final ActionEvent e) {
                this.update();
            }

            @Override
            public void itemStateChanged(final ItemEvent e) {
                this.update();
            }
        }
        final Updater up = new Updater();
        chooser.addItemListener(up);
        final Timer timer = new Timer(1000, up);
        timer.setRepeats(true);
        timer.start();

    }
}