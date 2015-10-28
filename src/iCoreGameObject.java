import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 28/10/15.
 */
public interface iCoreGameObject {
    Vector2f getPosition();
    public IntRect getBounds();

    void update(float dt);
    void draw(RenderWindow window);
}
