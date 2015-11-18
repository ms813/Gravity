package Core;

import GameObjects.Asteroid;
import GameObjects.GameObject;
import GameObjects.Planet;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.MouseButtonEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smithma on 17/11/15.
 */
public class PlanetTestState implements GameState {

    private Game game;

    private List<GameObject> planets = new ArrayList<>();

    private GravityHandler gravityHandler;
    private CollisionHandler collisionHandler;

    public PlanetTestState(Game game) {
        this.game = game;
        Planet planet = new Planet(100000, new Vector2f(50,50));

        Asteroid asteroid = new Asteroid(10000, new Vector2f(350, 350));

        planet.addSatellite(asteroid);
        planets.add(planet);

        gravityHandler = new GravityHandler(50);
        collisionHandler = new CollisionHandler(10);
        collisionHandler.showGrid();
    }

    @Override
    public void draw(float dt) {
        RenderWindow window = game.getWindow();

        for(GameObject planet : planets){
            planet.draw(window);
        }

        gravityHandler.draw(window);
        collisionHandler.draw(window);
    }

    @Override
    public void update(float dt) {

        collisionHandler.reset();
        collisionHandler.insertAll(planets);
        collisionHandler.resolveCollisions();

        gravityHandler.reset();
        gravityHandler.insertAll(planets);
        gravityHandler.recalculatePhysicalProperties();
        gravityHandler.applyGravityForces();

        for(GameObject planet : planets){
            planet.update(dt);
        }
    }

    @Override
    public void handleInput() {
        for (Event e : game.getWindow().pollEvents()) {
            if (e.type == Event.Type.CLOSED) {
                game.getWindow().close();
            } else if (e.type == Event.Type.MOUSE_BUTTON_PRESSED) {
                MouseButtonEvent mouseEvt = e.asMouseButtonEvent();
                if (mouseEvt.button == Mouse.Button.LEFT) {
                    Vector2f worldPos = game.mapPixelToCoords(mouseEvt.position);
                }
            }
        }
    }
}
