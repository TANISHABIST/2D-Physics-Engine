package core;
import java.awt.*;

public class RectangleShape implements Shape {
    private final double width, height;
    private final Vector2D[] vertices;
    private final Vector2D[] faceCenters;
    private final Vector2D[] faceNormals;

    public RectangleShape(double width, double height) {
        this.width = width;
        this.height = height;
        
        double w2 = width / 2;
        double h2 = height / 2;
        this.vertices = new Vector2D[] { new Vector2D(-w2, -h2), new Vector2D(w2, -h2), new Vector2D(w2, h2), new Vector2D(-w2, h2) };
        this.faceCenters = new Vector2D[] { new Vector2D(0, h2), new Vector2D(-w2, 0), new Vector2D(0, -h2), new Vector2D(w2, 0) };
        this.faceNormals = new Vector2D[] { new Vector2D(0, 1), new Vector2D(-1, 0), new Vector2D(0, -1), new Vector2D(1, 0) };
    }

    @Override public void draw(Graphics2D g, Vector2D pos, double angle) { 
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(pos.x, pos.y);
        g2d.rotate(angle);
        g2d.fillRect((int)(-width / 2), (int)(-height / 2), (int)width, (int)height);
        g2d.dispose();
    }
    @Override public double getBoundingRadius() { return Math.sqrt(width * width + height * height) / 2; }

    @Override
    public Vector2D getSupportCenter(double angle) {
        double maxDot = Double.NEGATIVE_INFINITY;
        Vector2D supportCenter = null;
        Vector2D worldDown = new Vector2D(0, 1);

        for (int i = 0; i < faceNormals.length; i++) {
            Vector2D normal = faceNormals[i];
            double rotatedX = normal.x * Math.cos(-angle) - normal.y * Math.sin(-angle);
            double rotatedY = normal.x * Math.sin(-angle) + normal.y * Math.cos(-angle);
            Vector2D worldNormal = new Vector2D(rotatedX, rotatedY);
            double dot = worldNormal.dot(worldDown);
            if (dot > maxDot) {
                maxDot = dot;
                supportCenter = faceCenters[i];
            }
        }
        return supportCenter;
    }
}
