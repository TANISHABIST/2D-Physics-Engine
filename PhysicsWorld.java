package core;

import java.util.*;

public class PhysicsWorld {
    private final List<PhysicsBody> bodies = new ArrayList<>();
    private final Vector2D gravity = new Vector2D(0, 500);
    private double idleTimer = 0; // Use a world-level idle timer for sleeping

    public void addBody(PhysicsBody body) { bodies.add(body); }
    public List<PhysicsBody> getBodies() { return bodies; }

    public void update(double dt, int width, int height) {
        // 1. Apply forces
        for (PhysicsBody b : bodies) {
            if (!b.isStatic && !b.isSleeping) {
                b.applyForce(gravity.scale(b.mass));
            }
        }
        
        // 2. Update positions and velocities
        for (PhysicsBody b : bodies) {
            b.update(dt);
        }

        // 3. Handle object-vs-object collisions
        handleCollisions();

        // 4. Handle boundary collisions and the "standing up" logic
        for (PhysicsBody b : bodies) {
            if (b.isStatic || b.isSleeping) continue;

            double radius = b.getRadius();
            // Walls and ceiling
            if (b.position.x + radius > width) { b.position.x = width - radius; b.velocity.x *= -b.restitution; b.wakeUp(); }
            if (b.position.x - radius < 0) { b.position.x = radius; b.velocity.x *= -b.restitution; b.wakeUp(); }
            if (b.position.y - radius < 0) { b.position.y = radius; b.velocity.y *= -b.restitution; b.wakeUp(); }
            
            // Floor collision
            if (b.position.y + radius > height) {
                b.position.y = height - radius;
                b.velocity.y *= -b.restitution;
                b.wakeUp();
                
                // --- THE SELF-RIGHTING AND STABILITY LOGIC IS ONLY HERE ---
                // If the body is on the ground and moving slowly, we intervene.
                double speed = b.velocity.magnitude();
                if (speed < 40.0 && Math.abs(b.angularVelocity) < 2.5) {
                    // It's settling on the ground.
                    
                    // a) Apply torque to make it stand up on its base
                    Vector2D supportCenter = b.shape.getSupportCenter(b.angle); 
                    Vector2D leverArm = supportCenter.scale(-1);
                    double cosA = Math.cos(b.angle); double sinA = Math.sin(b.angle);
                    double rotatedLeverX = leverArm.x * cosA - leverArm.y * sinA;
                    double rotatedLeverY = leverArm.x * sinA + leverArm.y * cosA;
                    Vector2D worldLeverArm = new Vector2D(rotatedLeverX, rotatedLeverY);
                    double gravityTorque = worldLeverArm.cross(new Vector2D(0, 1));
                    b.applyTorque(gravityTorque * b.mass * 250);

                    // b) Apply strong damping to stop it from wobbling
                    b.angularVelocity *= 0.80;
                    b.velocity.x *= 0.80;
                }
            }
        }

        // 5. Handle sleeping
        boolean allSleeping = true;
        for (PhysicsBody b : bodies) {
            if (b.isStatic) continue;
            // A body is considered "active" if it's moving more than a tiny amount
            if (b.velocity.magnitude() > 1.0 || Math.abs(b.angularVelocity) > 0.1) {
                allSleeping = false;
                break;
            }
        }

        if (allSleeping) {
            idleTimer += dt;
            if (idleTimer > 1.0) { // If everything has been still for 1 second
                for (PhysicsBody b : bodies) {
                    b.isSleeping = true;
                    b.velocity = new Vector2D(0, 0); // Force to a perfect stop
                    b.angularVelocity = 0;
                }
            }
        } else {
            idleTimer = 0; // Reset timer if anything is moving
        }
    }

    // Unchanged from the last correct version
    private void handleCollisions() {
        // ... (This method can stay exactly as it was in the last version with the typo fixed)
        for (int i = 0; i < bodies.size(); i++) { for (int j = i + 1; j < bodies.size(); j++) { PhysicsBody a = bodies.get(i); PhysicsBody b = bodies.get(j); if ((a.isStatic || a.isSleeping) && (b.isStatic || b.isSleeping)) continue; Vector2D delta = b.position.subtract(a.position); double dist = delta.magnitude(); double minDist = a.getRadius() + b.getRadius(); if (dist < minDist) { a.wakeUp(); b.wakeUp(); Vector2D normal = delta.normalize(); double penetration = minDist - dist; final double correctionPercentage = 0.8; double invMassA = a.isStatic ? 0 : 1 / a.mass; double invMassB = b.isStatic ? 0 : 1 / b.mass; double totalInvMass = invMassA + invMassB; if (totalInvMass > 0) { Vector2D correction = normal.scale(penetration * correctionPercentage / totalInvMass); if (!a.isStatic) a.position = a.position.subtract(correction.scale(invMassA)); if (!b.isStatic) b.position = b.position.add(correction.scale(invMassB)); } Vector2D r_a = normal.scale(a.getRadius()); Vector2D r_b = normal.scale(-b.getRadius()); Vector2D v_a_contact = a.velocity.add(new Vector2D(-a.angularVelocity * r_a.y, a.angularVelocity * r_a.x)); Vector2D v_b_contact = b.velocity.add(new Vector2D(-b.angularVelocity * r_b.y, b.angularVelocity * r_b.x)); Vector2D relativeVelocity = v_b_contact.subtract(v_a_contact); double relativeVelocityNormal = relativeVelocity.dot(normal); if (relativeVelocityNormal > 0) continue; double restitution = Math.min(a.restitution, b.restitution); double r_a_cross_n = r_a.cross(normal); double r_b_cross_n = r_b.cross(normal); double invInertiaA = a.isStatic ? 0 : 1 / a.momentOfInertia; double invInertiaB = b.isStatic ? 0 : 1 / b.momentOfInertia; double impulseDenominator = totalInvMass + (r_a_cross_n * r_a_cross_n) * invInertiaA + (r_b_cross_n * r_b_cross_n) * invInertiaB; double impulseScalar = -(1 + restitution) * relativeVelocityNormal / impulseDenominator; Vector2D impulseVec = normal.scale(impulseScalar); if (!a.isStatic) { a.velocity = a.velocity.subtract(impulseVec.scale(invMassA)); a.angularVelocity -= r_a.cross(impulseVec) * invInertiaA; } if (!b.isStatic) { b.velocity = b.velocity.add(impulseVec.scale(invMassB)); b.angularVelocity += r_b.cross(impulseVec) * invInertiaB; } v_a_contact = a.velocity.add(new Vector2D(-a.angularVelocity * r_a.y, a.angularVelocity * r_a.x)); v_b_contact = b.velocity.add(new Vector2D(-b.angularVelocity * r_b.y, b.angularVelocity * r_b.x)); relativeVelocity = v_b_contact.subtract(v_a_contact); Vector2D tangent = relativeVelocity.subtract(normal.scale(relativeVelocity.dot(normal))); if (tangent.magnitude() < 1e-6) continue; tangent = tangent.normalize(); double r_a_cross_t = r_a.cross(tangent); double r_b_cross_t = r_b.cross(tangent); double impulseDenominatorT = totalInvMass + (r_a_cross_t * r_a_cross_t) * invInertiaA + (r_b_cross_t * r_b_cross_t) * invInertiaB; double tangentImpulseScalar = -relativeVelocity.dot(tangent) / impulseDenominatorT; double friction = Math.sqrt(a.friction * b.friction); Vector2D frictionImpulse; if (Math.abs(tangentImpulseScalar) > impulseScalar * friction) { frictionImpulse = tangent.scale(-impulseScalar * friction); } else { frictionImpulse = tangent.scale(tangentImpulseScalar); } if (!a.isStatic) { a.velocity = a.velocity.add(frictionImpulse.scale(invMassA)); a.angularVelocity += r_a.cross(frictionImpulse) * invInertiaA; } if (!b.isStatic) { b.velocity = b.velocity.subtract(frictionImpulse.scale(invMassB)); b.angularVelocity -= r_b.cross(frictionImpulse) * invInertiaB; } } } }
    }
    public void reset() { bodies.clear(); }
}
