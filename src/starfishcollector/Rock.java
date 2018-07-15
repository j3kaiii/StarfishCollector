package starfishcollector;

import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 *
 * @author j3kaiii
 */
public class Rock extends BaseActor{

    public Rock(float x, float y, Stage s) {
        super(x, y, s);
        loadTextire("rock.png");
        setBoundaryPolygon(8);
    }

}
