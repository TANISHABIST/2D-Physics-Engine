package core;

import java.awt.*;

public class TriangleShape implements Shape {
    private final int size;
    private final Vector2D[] vertices; 

    public TriangleShape(int size) {
        this.size = size;
        this.vertices = new Vector2D[] {
            new Vector2D(0, -size / 2.0),          
            new Vector2D(-size / 2.0, size / 2.0), 
            new Vector2D(size / 2.0, size / 2.0)  
        };
    }

    @Override
    public void draw(Graphics2D g, Vector2D position, double angle) {
        int p1x = (int) vertices[0].x;
        int p1y = (int) vertices[0].y;
        int p2x = (int) vertices[1].x;
        int p2y = (int) vertices[1].y;
        int p3x = (int) vertices[2].x;
        int p3y = (int) vertices[2].y;
        
        int[] xPoints = { p1x, p2x, p3x };
        int[] yPoints = { p1y, p2y, p3y };

        Polygon poly = new Polygon(xPoints, yPoints, 3);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(position.x, position.y);
        g2d.rotate(angle);
        g2d.fillPolygon(poly);
        g2d.dispose();
    }

    @Override
    public double getBoundingRadius() {
        return size / 2.0;
    }

    @Override
    public Vector2D getSupportCenter(double angle) {
        Vector2D lowestVertex = null;
        double max_y = Double.NEGATIVE_INFINITY;

        for (Vector2D v : vertices) {
            double rotated_y = v.x * Math.sin(angle) + v.y * Math.cos(angle);
            
            if (rotated_y > max_y) {
                max_y = rotated_y;
                lowestVertex = v;
            }
        }
        return lowestVertex;
    }
}
