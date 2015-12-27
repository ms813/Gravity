package GameStates;

import Core.Game;
import Factories.EntityFactory;
import GameObjects.Entity;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 26/10/2015.
 */
public class GameState {

    private Game game;
    private View view = new View();

    private List<Entity> sceneEntities = new ArrayList<>();

    private boolean VERLET_STATE;

    private EntityFactory entityFactory = new EntityFactory();

    public GameState(Game game) {
        this.game = game;

        Entity asteroid = entityFactory.createEntity("Asteroid");
        sceneEntities.add(asteroid);
    }

    public void draw(float dt) {

        RenderWindow window = game.getWindow();

        //view.setCenter(Vector2f.div(new Vector2f(window.getSize()), 2));

        for (Entity e : sceneEntities) {
            e.draw(window);
        }

        //game.setView(view);
    }

    public void update(float dt) {
        for (Entity e : sceneEntities) {
            e.update(dt, VERLET_STATE);
        }

        VERLET_STATE = !VERLET_STATE;
    }

    public void handleInput() {
        for (Event e : game.getWindow().pollEvents()) {
            if (e.type == Event.Type.CLOSED) {
                game.getWindow().close();
            }
        }
    }

    public void addEntity(Entity entity) {
        if (!sceneEntities.contains(entity)) {
            sceneEntities.add(entity);
        } else {
            System.err.println("[GameState.addEntity()] Object " + entity + " already exists in " + this + " scene list");
        }
    }
}
