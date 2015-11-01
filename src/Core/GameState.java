package Core;

/**
 * Created by Matthew on 26/10/2015.
 */
public abstract class GameState {

    public GameState(Game game) {this.game = game; }

    public abstract void draw(float dt);
    public abstract void update(float dt);
    public abstract void handleInput();

    protected Game game;

}
