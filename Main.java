package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainApp {

    // Define our color palette for easy reuse
    private static final Color BG_COLOR = new Color(50, 50, 50);
    private static final Color PANEL_BG_COLOR = new Color(30, 30, 30);
    private static final Color FOREGROUND_COLOR = new Color(220, 220, 220); // This is our "white" text color
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color ACCENT_HOVER_COLOR = new Color(20, 140, 235);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Advanced 2D Physics Engine");
            PhysicsPanel panel = new PhysicsPanel();

            StyledButton startBtn = new StyledButton("Start");
            StyledButton stopBtn = new StyledButton("Stop");
            StyledButton resetBtn = new StyledButton("Reset");

            startBtn.addActionListener(e -> panel.start());
            stopBtn.addActionListener(e -> panel.stop());
            resetBtn.addActionListener(e -> panel.reset());

            JPanel controls = new JPanel();
            controls.setBackground(BG_COLOR);
            controls.setBorder(new EmptyBorder(5, 10, 5, 10));
            controls.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));

            JLabel shapeLabel = new JLabel("Add Shape:");
            shapeLabel.setForeground(FOREGROUND_COLOR);
            shapeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            // --- CORRECTED SECTION ---
            JComboBox<String> shapeSelector = panel.getShapeSelector();
            shapeSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            // This is the crucial line that sets the text color to white
            shapeSelector.setForeground(FOREGROUND_COLOR);

            // Apply the custom UI to control the look of the main box and arrow
            shapeSelector.setUI(new DarkComboBoxUI());
            
            // Apply the custom renderer to control the look of the dropdown list
            shapeSelector.setRenderer(new DarkComboBoxRenderer());
            // --- END OF CORRECTION ---

            controls.add(startBtn);
            controls.add(stopBtn);
            controls.add(resetBtn);
            controls.add(shapeLabel);
            controls.add(shapeSelector);
            
            frame.add(panel, BorderLayout.CENTER);
            frame.add(controls, BorderLayout.SOUTH);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * A custom UI delegate for JComboBox that provides complete control over its appearance.
     * (Unchanged from previous version)
     */
    private static class DarkComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            final JButton button = new JButton("▼");
            button.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
            button.setForeground(FOREGROUND_COLOR);
            button.setBackground(ACCENT_COLOR);
            button.setBorder(new EmptyBorder(2, 4, 2, 4));
            button.setFocusable(false);
            return button;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(BG_COLOR);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
    
    /**
     * A custom ListCellRenderer to correctly style the JComboBox dropdown list
     * for a dark theme. (Unchanged from previous version).
     */
    private static class DarkComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setForeground(FOREGROUND_COLOR);
            if (isSelected) {
                setBackground(ACCENT_COLOR);
            } else {
                setBackground(BG_COLOR);
            }
            return this;
        }
    }

    /**
     * A custom JButton class for a modern, flat look with hover effects.
     * (Unchanged from previous version).
     */
    private static class StyledButton extends JButton {
        private boolean isHovered = false;
        public StyledButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(FOREGROUND_COLOR);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setPreferredSize(new Dimension(100, 35));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isHovered ? ACCENT_HOVER_COLOR : ACCENT_COLOR);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
