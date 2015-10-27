import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

/**
 * Created by Matthew on 26/10/2015.
 */
public abstract class GameObject {

    protected Sprite sprite;
    protected Shape shape = new CircleShape();

    protected Vector2f velocity = Vector2f.ZERO;
    protected Vector2f appliedForce = Vector2f.ZERO;

    public void draw(RenderWindow window, float dt){
        if (sprite != null) {
            window.draw(sprite);
        } else{
            window.draw(shape);
        }
    }

    public abstract void update(float dt);

    public Vector2f getPosition(){
        if (sprite != null) {
            return sprite.getPosition();
        } else{
            return shape.getPosition();
        }
    }

    public void move(Vector2f offset) {
        if (sprite != null) {
            sprite.move(offset);
        } else{
            shape.move(offset);
        }
    }

    public IntRect getBounds(){
        if (sprite != null) {
            return new IntRect(sprite.getGlobalBounds());
        } else{
            return new IntRect(shape.getGlobalBounds());
        }
    }

   public void applyForce(Vector2f force){
       appliedForce = force;
   }
}
