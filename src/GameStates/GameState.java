package GameStates;

import GameObjects.GameObject;

import java.awt.event.ActionListener;

/**
 * Created by Matthew on 26/10/2015.
 */
public interface GameState {
    void draw(float dt);
    void update(float dt);
    void handleInput();
    void addGameObject(GameObject object);
}
