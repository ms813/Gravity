package Core;

import GameObjects.Asteroid;
import GameObjects.GameObject;
import GameObjects.Planet;
import GameObjects.Spaceship;
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

    private View shipView;

    private Spaceship ship;

    private boolean VERLET_STATE = false; //used to update either the position or the velocity

    public PlanetTestState(Game game) {
        this.game = game;

        Vector2f windowCenter = Vector2f.div(new Vector2f(game.getWindow().getSize()), 2);

        Planet planet = new Planet(100000, Vector2f.ZERO);
        planet.setPosition(Vector2f.sub(windowCenter, Vector2f.div(planet.getSize(), 2)));

        planets.add(planet);
        ship = new Spaceship(new Vector2f(windowCenter.x - 10, 20));
        planets.add(ship);

        gravityHandler = new GravityHandler(50);
        collisionHandler = new CollisionHandler(10);
        collisionHandler.showGrid();

        shipView = new View();
        float dist = VectorMath.magnitude(Vector2f.sub(ship.getCenter(), planet.getCenter()));
        shipView.setSize(dist * 3, dist * 3);
    }

    @Override
    public void draw(float dt) {
        RenderWindow window = game.getWindow();
        shipView.setCenter(ship.getCenter());
        window.setView(shipView);
        for (GameObject planet : planets) {
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

        if (VERLET_STATE) {
            for (GameObject p : planets) {
                p.updatePosition(dt);
            }
        } else {
            for (GameObject p : planets) {
                p.applyForce(gravityHandler.getForce(p));
                p.updateVelocity(dt);
            }
        }
        VERLET_STATE = !VERLET_STATE;
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
