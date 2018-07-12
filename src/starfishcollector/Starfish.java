package starfishcollector;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 *
 * @author j3kaiii
 */
public class Starfish extends BaseActor{

    public Starfish(float x, float y, Stage s) {
        super(x, y, s);
        
        loadTextire("starfish.png");
        
        Action spin = Actions.rotateBy(30, 1);
        this.addAction(Actions.forever(spin));
    }

}
