import java.util.Comparator;

/**
 * Created by Matthew on 30/10/2015.
 */
public class MassComparator implements Comparator<GameObject> {
    @Override
    public int compare(GameObject o1, GameObject o2) {
        return Math.round(o2.getMass() - o1.getMass());
    }
}
