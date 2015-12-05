package GameObjects.Tools;

import Core.TextureManager;
import Core.VectorMath;
import GameObjects.Colliders.CircleCollider;
import GameObjects.GameObject;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 26/11/15.
 */
public class Bullet extends GameObject {

    private Turret source;
    private float length = 15;

    public Bullet(Turret source, Vector2f targetPos) {
        this.source = source;

        mass = 1;
        temperature = 500;
        heatCapacity = 1;
        density = 8;

        texture = TextureManager.getTexture("redLaserRay.png");
        texture.setSmooth(true);
        sprite.setTexture(texture);
        sprite.setScale(length / sprite.getGlobalBounds().width, length / sprite.getGlobalBounds().width);

        Vector2f direction = VectorMath.normalize(Vector2f.sub(targetPos, source.getParent().getCenter()));
        velocity = Vector2f.mul(direction, source.getPower());
        //calculate initial position based on velocity
        sprite.setOrigin(getSize().x / 2, getSize().y / 2);
        float shipRadius = (source.getParent().getSize().x + source.getParent().getSize().y) / 4;
        float bulletRadius = (getSize().x + getSize().y) / 4;

        sprite.setPosition(source.getParent().getCenter());
        sprite.move(Vector2f.mul(direction, (shipRadius + bulletRadius * 2)*1.1f)); //move off to the edge of the ship

        sprite.setRotation((float) Math.toDegrees(VectorMath.angle(velocity)));

        collider = new CircleCollider(this, 1);
    }

}
