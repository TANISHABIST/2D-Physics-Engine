package core;

public class PhysicsBody {
    public Vector2D position, velocity, force;
    public double mass;
    public boolean isStatic;
    public double restitution, friction;
    public Shape shape;

    public double angle;
    public double angularVelocity;
    public double torque;
    public double momentOfInertia;
    
    public double linearDamping;
    public double angularDamping;
    public boolean isSleeping;
    
    public PhysicsBody(Vector2D position, Shape shape, double mass, boolean isStatic) {
        this.position = position;
        this.shape = shape;
        this.mass = mass;
        this.isStatic = isStatic;
        this.velocity = new Vector2D(0, 0);
        this.force = new Vector2D(0, 0);
        this.restitution = 0.8;
        this.friction = 0.2;

        this.angle = 0;
        this.angularVelocity = 0;
        this.torque = 0;
   
        this.linearDamping = 0.995;
        this.angularDamping = 0.98;
        
        this.isSleeping = false;
        
        if (isStatic) {
            this.momentOfInertia = Double.POSITIVE_INFINITY;
        } else {
            if (shape instanceof CircleShape) {
                this.momentOfInertia = 0.5 * mass * Math.pow(shape.getBoundingRadius(), 2);
            } else if (shape instanceof RectangleShape) {
                double r = shape.getBoundingRadius(); 
                double w = Math.sqrt(2) * r;
                double h = Math.sqrt(2) * r;
                this.momentOfInertia = (1.0/12.0) * mass * (w*w + h*h);
            } else {
                this.momentOfInertia = mass * Math.pow(shape.getBoundingRadius(), 2);
            }
        }
    }
    
    public void wakeUp() {
        if (isSleeping) {
            isSleeping = false;
        }
    }

    public double getRadius() {
        return shape.getBoundingRadius();
    }

    public void applyForce(Vector2D f) {
        force = force.add(f);
    }

    public void applyTorque(double t) {
        torque += t;
    }

    public void update(double dt) {
        if (isStatic || isSleeping) return;

        Vector2D acceleration = force.scale(1 / mass);
        velocity = velocity.add(acceleration.scale(dt));
        
        double angularAcceleration = torque / momentOfInertia;
        angularVelocity += angularAcceleration * dt;
        
        velocity = velocity.scale(linearDamping);
        angularVelocity *= angularDamping;
        
        position = position.add(velocity.scale(dt));
        angle += angularVelocity * dt;

        force = new Vector2D(0, 0);
        torque = 0;
    }
}
