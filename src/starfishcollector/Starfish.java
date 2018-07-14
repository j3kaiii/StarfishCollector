package starfishcollector;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 *
 * @author j3kaiii
 */
public class Starfish extends BaseActor{
    
    public boolean collected;

    public Starfish(float x, float y, Stage s) {
        super(x, y, s);
        
        loadTextire("starfish.png");
        
        Action spin = Actions.rotateBy(30, 1);
        this.addAction(Actions.forever(spin));
        
        setBoundaryPolygon(8);
        
        collected = false;
    }
    
    public boolean isCollected() {
        return collected;
    }
    
    public void collect() {
        collected = true;
        clearActions();
        addAction(Actions.fadeOut(1));
        addAction(Actions.after(Actions.removeActor()));
    }

}
