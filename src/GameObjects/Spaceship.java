package GameObjects;

import Core.TextureManager;
import Core.VectorMath;
import GameObjects.Colliders.CircleCollider;
import GameObjects.Tools.Bullet;
import GameObjects.Tools.Weapon;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

/**
 * Created by smithma on 18/11/15.
 */
public class Spaceship extends GameObject {

    private Thruster thruster;
    private Weapon weapon;

    public Spaceship(Vector2f position) {

        this.mass = 10000;
        this.temperature = 200;
        this.heatCapacity = 1;
        this.density = 10;

        texture = TextureManager.getTexture("spaceship.png");
        sprite.setTexture(texture);

        float radius = (float) Math.sqrt(this.mass / (Math.PI * density));
        sprite.setScale(2 * radius / sprite.getGlobalBounds().width, 2 * radius / sprite.getGlobalBounds().height);

        setPosition(position);

        collider = new CircleCollider(this, 1);

        thruster = new Thruster();
        thruster.setRotation(Math.toRadians(sprite.getRotation()));

        weapon = new Weapon(this);
    }

    @Override
    public void updateVelocity(float dt) {
        if (active) {
            appliedForce = Vector2f.add(appliedForce, thruster.getThrustVector());
        }
        super.updateVelocity(dt);

        weapon.decrementCooldown(dt);
    }

    @Override
    public void updatePosition(float dt){
        super.updateVelocity(dt);

        weapon.decrementCooldown(dt);
    }

    public Bullet fireWeapon(Vector2f targetPos){
        return weapon.fire(targetPos);
    }

    public boolean isWeaponReady(){
        return weapon.isReady();
    }

    @Override
    public boolean hasWeapons(){
        return weapon != null;
    }
}
