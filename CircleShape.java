package core;

import java.awt.*;

public class CircleShape implements Shape {
    private final double radius;

    public CircleShape(double radius) {
        this.radius = radius;
    }

    @Override
    public void draw(Graphics2D g, Vector2D position, double angle) {
        int r = (int) (radius * 2);
        g.fillOval((int)(position.x - radius), (int)(position.y - radius), r, r);
    }

    @Override
    public double getBoundingRadius() {
        return radius;
    }
    
    @Override
    public Vector2D getSupportCenter(double angle) {
        return new Vector2D(0, radius);
    }
}
