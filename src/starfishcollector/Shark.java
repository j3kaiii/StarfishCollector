package starfishcollector;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/**
 *
 * @author j3kaiii
 */
public class Shark extends BaseActor{
    private boolean faceRight;
    private float startX, startY;
    private float endX, endY;
    private Action sharkMoveRight, sharkMoveLeft;

    public Shark(float x, float y, Stage s) {
        super(x, y, s);
        startX = 0;
        startY = y;
        endX = 800;
        endY = y;
        
        loadTextire("sharky.png");
        
        faceRight = true;
        
        sharkMoveRight = Actions.moveTo(endX, endY, 3);
        sharkMoveLeft = Actions.moveTo(startX, startY, 3);
        this.addAction(sharkMoveRight);
        
        setBoundaryPolygon(8);        
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float getEndX() {
        return endX;
    }

    public float getEndY() {
        return endY;
    }
    
    
   
    public void MoveRight() {
            clearActions();
            addAction(Actions.scaleTo(1, 1));
            addAction(Actions.after(sharkMoveRight));
    }
    
    public void MoveLeft() {
         clearActions();
            addAction(Actions.scaleTo(-1, 1));
            addAction(Actions.after(sharkMoveLeft));
    }

}
