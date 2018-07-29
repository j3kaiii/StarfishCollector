package starfishcollector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
/**
 *
 * @author j3kaiii
 */
public class MenuScreen extends BaseScreen{
   
    @Override
    public void initialize() {
        BaseActor ocean = new BaseActor(0, 0, mainStage);
        ocean.loadTextire("water.jpg");
        ocean.setSize(800, 600);
        
        BaseActor title = new BaseActor(0, 0, mainStage);
        title.loadTextire("starfish-collector.png");
        title.centerAtPosition(400, 300);
        title.moveBy(0, 100);
        
        BaseActor start = new BaseActor(0, 0, mainStage);
        start.loadTextire("message-start.png");
        start.centerAtPosition(400, 300);
        start.moveBy(0, -100);
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyPressed(Keys.S)) {
            StarfishGame.setActiveScreen(new LevelScreen());
        }
    }   
}
