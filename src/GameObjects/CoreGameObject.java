package GameObjects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 28/10/15.
 */
public interface CoreGameObject {
    Vector2f getPosition();
    void setPosition(Vector2f position);
    Vector2f getCenter();
    FloatRect getBounds();
    Vector2f getSize();
    void setFillColor(Color c);

    void update(float dt);
    void draw(RenderWindow window);

}
