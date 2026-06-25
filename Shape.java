package core;
import java.awt.Graphics2D;

public interface Shape {
    void draw(Graphics2D g, Vector2D position, double angle);
    double getBoundingRadius();
    Vector2D getSupportCenter(double angle);
}
