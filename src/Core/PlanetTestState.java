package Core;

import GameObjects.Asteroid;
import GameObjects.GameObject;
import GameObjects.Planet;
import org.jsfml.graphics.RenderWindow;
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

    private GravityHandler gravityHandler = new GravityHandler();
    private CollisionHandler collisionHandler = new CollisionHandler();

    public PlanetTestState(Game game) {
        this.game = game;
        Planet planet = new Planet(10000, new Vector2f(300,300));
        planet.addSatellite(new Asteroid(5000, new Vector2f(350,350)));

        planets.add(planet);
    }

    @Override
    public void draw(float dt) {
        RenderWindow window = game.getWindow();

        for(GameObject planet : planets){
            planet.draw(window);
        }
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
