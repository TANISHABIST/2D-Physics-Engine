//package gui;
//
//import core.*;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//
//public class PhysicsPanel extends JPanel {
//    private final PhysicsWorld world = new PhysicsWorld();
//    private final Timer timer;
//    // --- ADDED ---
//    private final JComboBox<String> shapeSelector;
//
//    public PhysicsPanel() {
//        setPreferredSize(new Dimension(800, 600));
//        setBackground(Color.WHITE);
//
//        // --- ADDED ---
//        // Initialize the dropdown box with shape options
//        shapeSelector = new JComboBox<>(new String[]{"Circle", "Rectangle", "Triangle"});
//
//        timer = new Timer(16, e -> {
//            int width = getWidth();
//            int height = getHeight();
//            world.update(0.016, width, height);
//            repaint();
//        });
//
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                Vector2D pos = new Vector2D(e.getX(), e.getY());
//                core.Shape shape;
//                
//                // --- MODIFIED ---
//                // Get the selected shape from the JComboBox
//                String selectedShape = (String) shapeSelector.getSelectedItem();
//
//                // Create the shape based on the user's selection
//                switch (selectedShape) {
//                    case "Circle":
//                        shape = new CircleShape(20);
//                        break;
//                    case "Rectangle":
//                        shape = new RectangleShape(40, 30);
//                        break;
//                    case "Triangle":
//                        shape = new TriangleShape(40);
//                        break;
//                    default:
//                        // Fallback to circle if something goes wrong
//                        shape = new CircleShape(20);
//                        break;
//                }
//
//                PhysicsBody body = new PhysicsBody(pos, shape, 10, false);
//                body.applyTorque((Math.random() - 0.5) * 500); // apply random torque
//                world.addBody(body);
//                // Wake up the world if it was sleeping
//                world.getBodies().forEach(PhysicsBody::wakeUp);
//            }
//        });
//    }
//
//    public void start() {
//        timer.start();
//    }
//
//    public void stop() {
//        timer.stop();
//    }
//
//    public void reset() {
//        stop();
//        world.reset();
//        repaint();
//    }
//
//    // --- ADDED ---
//    // A getter to allow MainApp to access the dropdown and add it to the controls
//    public JComboBox<String> getShapeSelector() {
//        return shapeSelector;
//    }
//
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2d = (Graphics2D) g.create();
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        for (PhysicsBody b : world.getBodies()) {
//            if (b.isSleeping) {
//                g2d.setColor(Color.GRAY);
//            } else {
//                g2d.setColor(Color.BLUE);
//            }
//            b.shape.draw(g2d, b.position, b.angle);
//        }
//        g2d.dispose();
//    }
//}


package gui;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PhysicsPanel extends JPanel {
    private final PhysicsWorld world = new PhysicsWorld();
    private final Timer timer;
    private final JComboBox<String> shapeSelector;
    
    // Define colors for the physics objects
    private static final Color OBJECT_COLOR = new Color(0, 150, 255);
    private static final Color SLEEPING_OBJECT_COLOR = new Color(120, 120, 120);
    private static final Color PANEL_BG_COLOR = new Color(30, 30, 30);


    public PhysicsPanel() {
        setPreferredSize(new Dimension(800, 600));
        // --- MODIFIED ---
        // Set the dark background for the physics simulation area
        setBackground(PANEL_BG_COLOR);

        shapeSelector = new JComboBox<>(new String[]{"Circle", "Rectangle", "Triangle"});

        timer = new Timer(16, e -> {
            int width = getWidth();
            int height = getHeight();
            world.update(0.016, width, height);
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Vector2D pos = new Vector2D(e.getX(), e.getY());
                core.Shape shape;
                
                String selectedShape = (String) shapeSelector.getSelectedItem();

                switch (selectedShape) {
                    case "Circle":
                        shape = new CircleShape(20);
                        break;
                    case "Rectangle":
                        shape = new RectangleShape(40, 30);
                        break;
                    case "Triangle":
                        shape = new TriangleShape(40);
                        break;
                    default:
                        shape = new CircleShape(20);
                        break;
                }

                PhysicsBody body = new PhysicsBody(pos, shape, 10, false);
                body.applyTorque((Math.random() - 0.5) * 500);
                world.addBody(body);
                // Wake up the world if it was sleeping
                world.getBodies().forEach(PhysicsBody::wakeUp);
            }
        });
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void reset() {
        stop();
        world.reset();
        repaint();
    }
    
    public JComboBox<String> getShapeSelector() {
        return shapeSelector;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (PhysicsBody b : world.getBodies()) {
            // --- MODIFIED ---
            // Use our new color scheme for the objects
            if (b.isSleeping) {
                g2d.setColor(SLEEPING_OBJECT_COLOR);
            } else {
                g2d.setColor(OBJECT_COLOR);
            }
            b.shape.draw(g2d, b.position, b.angle);
        }
        g2d.dispose();
    }
}
