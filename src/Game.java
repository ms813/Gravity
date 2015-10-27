import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.window.VideoMode;

import javax.swing.*;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by Matthew on 26/10/2015.
 */
public class Game {

    private RenderWindow window = new RenderWindow();
    private Stack<GameState> gameStates = new Stack<GameState>();

    public Game(){
        window.create(new VideoMode(1024, 800), "Space Game");
        window.setVerticalSyncEnabled(true);

        gameStates.push(new MainState(this));
    }

    public void start(){

        Clock clock = new Clock();

             while (window.isOpen()){
                 Time elapsed = clock.restart();
                 Float dt = elapsed.asSeconds();

                 peekState().handleInput();

                 window.clear(Color.BLACK);
                 peekState().update(dt);
                 peekState().draw(dt);
                 window.display();
             }
    }

    public GameState peekState() {
        try {
            if (gameStates.isEmpty()) {
                throw new EmptyStackException();
            }
        } catch (EmptyStackException e) {
            e.printStackTrace();
        }

        return gameStates.peek();
    }

    public RenderWindow getWindow(){
        return window;
    }
}
