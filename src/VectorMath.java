import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.IntRect;
import org.jsfml.system.Vector2f;

import java.util.Random;

/**
 * Created by Matthew on 26/10/2015.
 */
public class VectorMath {

    public static final Vector2f UP = new Vector2f(0, -1);
    public static final Vector2f DOWN = new Vector2f(0, 1);
    public static final Vector2f LEFT = new Vector2f(-1, 0);
    public static final Vector2f RIGHT = new Vector2f(1, 0);

    private static final float floatTolerance = 3.0f;
    private static final float vectorTolerance = 1.0f;

    public static strictfp Vector2f normalize(Vector2f v){
        if(magnitude(v) > 0){
            return Vector2f.div(v, magnitude(v));
        } else{
            return Vector2f.ZERO;
            //throw new ArithmeticException("Vector division by zero!");
        }
    }

    public static strictfp float magnitude(Vector2f v){
        float f = (float) Math.pow(v.x, 2) + (float) Math.pow(v.y, 2);
        if(f > 0){
            return (float)Math.sqrt(f);
        } else{
            return 0;
            //throw new ArithmeticException("Vector magnitude divide by zero!");
        }
    }

    public static strictfp float dot(Vector2f a, Vector2f b){
        return a.x * b.x + a.y * b.y;
    }

    public static strictfp float radBetween(Vector2f a, Vector2f b) {
        return (float) Math.acos(dot(a, b) / (magnitude(a) * magnitude(b)));
    }

    public static strictfp float degBetween(Vector2f a, Vector2f b) {
        return  radBetween(a, b) * (180.0f / (float) Math.PI);
    }

    public static strictfp Vector2f unitDir(Vector2f to, Vector2f from){
        return normalize(Vector2f.sub(to, from));
    }

    public static strictfp  Vector2f unitDir(float deg){
        return new Vector2f((float) Math.sin(Math.toRadians(deg)) , (float) Math.cos(Math.toRadians(deg)));
    }

    public static strictfp boolean nearlyEquals(Vector2f a, Vector2f b){
        return (a.x < b.x + vectorTolerance)
                && (a.x > b.x - vectorTolerance)
                && (a.y < b.y + vectorTolerance)
                && (a.y > b.y - vectorTolerance);
    }

    public static strictfp boolean nearlyEquals(float a, float b){
        return (a < b + floatTolerance) && (a > b - floatTolerance);
    }

    public static strictfp Vector2f centroid(FloatRect r){
        return new Vector2f(r.width* 0.5f, r.height * 0.5f);
    }

    public static strictfp Vector2f centroid(IntRect i){
        return new Vector2f(i.width *0.5f, i.height * 0.5f);
    }

    public static strictfp int direction(Vector2f a, Vector2f b){

        //return 0 if vectors are directly opposite each other
        //return 1 if vector b lies clockwise of vector a
        //return -1 if vector b lies anticlockwise of vector a

        float x = a.x*b.y - a.y*b.x;
        if(x == 0) return 0;
        return (x > 0) ? 1 : -1;
    }

    public static strictfp int direction(float angleA, float angleB){
        return direction(unitDir(angleA), unitDir(angleB));
    }

    public static strictfp Vector2f unitPerpendicularTo(Vector2f v){
        return new Vector2f(-normalize(v).y, normalize(v).x);
    }

    //generates a random unit vector
    //Random.nextFloat() generates a float between 0.0 and 1.0, so subtract 0.5 to get -0.5 to +0.5
    //then normalize to get back into the range -1.0 to +1.0
    public static strictfp Vector2f randomUnit(){
        Random rnd = new Random();
        return normalize(new Vector2f(rnd.nextFloat() - 0.5f, rnd.nextFloat() - 0.5f));
    }
}
