package GameObjects.Tools;

import GameObjects.Creep;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * Created by Matthew on 06/12/2015.
 */
public class HealthBar {

    private final float THICKNESS = 0.1f;
    private Creep parent;
    private RectangleShape bar;

    public HealthBar(Creep parent) {

        this.parent = parent;
        bar = new RectangleShape(new Vector2f(parent.getSize().x, parent.getSize().y * THICKNESS));
        bar.setFillColor(Color.GREEN);

        update();
    }

    public void update() {
        bar.setPosition(parent.getPosition().x, parent.getPosition().y + parent.getSize().y * (1f - THICKNESS));

        float frontLength = (parent.getCurrentHp() / parent.getMaxHp()) * parent.getSize().x;
        bar.setSize(new Vector2f(frontLength, bar.getSize().y));
    }

    public void draw(RenderWindow window) {
        window.draw(bar);
    }

}
