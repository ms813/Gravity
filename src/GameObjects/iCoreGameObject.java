package GameObjects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 28/10/15.
 */
public interface iCoreGameObject {
    Vector2f getPosition();
    FloatRect getBounds();
    Vector2f getSize();
    void setFillColor(Color c);

    void update(float dt);
    void draw(RenderWindow window);
}
