package GameObjects;

import GameObjects.Colliders.Collider;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 28/10/15.
 */
public interface GameObject {

    /*
        Core
    */
    void update(float dt);
    void draw(RenderWindow window);

    /*
        Basic shape manipulation
    */
    Vector2f getPosition();
    void setPosition(Vector2f position);
    Vector2f getCenter();
    FloatRect getBounds();
    Vector2f getSize();
    void move(Vector2f offset);
    void setFillColor(Color c);

    /*
       Physics
    */
    Vector2f getNextPos(float dt);
    boolean isSolid();
    void applyForce(Vector2f force);
    Vector2f getVelocity();
    void setVelocity(Vector2f velocity);
    float getMass();
    float getArea();
    float getDensity();
    float getTemperatureChange(float energy);
    float getTemperature();
    void  setTemperature(float temperature);
    void calculateCollision(GameObject object);
    void applyCollision();

    /*
        Collider
    */
    Collider getCollider();


}
