package Core;

import java.awt.event.ActionListener;

/**
 * Created by Matthew on 26/10/2015.
 */
public interface GameState {
    public abstract void draw(float dt);
    public abstract void update(float dt);
    public abstract void handleInput();
}
