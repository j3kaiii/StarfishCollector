package starfishcollector;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
/**
 *
 * @author j3kaiii
 */
public class LevelScreen extends BaseScreen {
    private Turtle turtle;
    private boolean win, die;

    @Override
    public void initialize() {
        BaseActor ocean = new BaseActor(0, 0, mainStage);
        ocean.loadTextire("water-border.jpg");
        ocean.setSize(800, 1600);
        BaseActor.setWorldBounds(ocean);
        
        new Starfish(600, 200, mainStage);
        new Starfish(400, 400, mainStage);
        new Starfish(200, 600, mainStage);
        new Starfish(400, 800, mainStage);
        new Starfish(600, 1000, mainStage);
        new Starfish(200, 1200, mainStage);
        new Starfish(200, 1400, mainStage);
        new Starfish(600, 1500, mainStage);
        
        new Rock(400, 620, mainStage);
        new Rock(500, 600, mainStage);
        new Rock(600, 580, mainStage);
        new Rock(200, 800, mainStage);
        new Rock(250, 900, mainStage);
        new Rock(350, 900, mainStage);
        new Rock(450, 900, mainStage);
        
        new Shark(200, 100, mainStage);
        
        turtle = new Turtle(20, 20, mainStage);
        
        die = false;
        win = false;
    }

    @Override
    public void update(float dt) {
        for (BaseActor rockActor : BaseActor.getList(mainStage, "starfishcollector.Rock")) {
            turtle.preventOverlap(rockActor);
        }
        
        for (BaseActor starfishActor : BaseActor.getList(mainStage, "starfishcollector.Starfish")) {
            Starfish starfish = (Starfish)starfishActor;
            
            if (turtle.overlaps(starfish) && !starfish.collected) {
                starfish.collected = true;
                starfish.clearActions();
                starfish.addAction(Actions.fadeOut(1));
                starfish.addAction(Actions.after(Actions.removeActor()));
                
                Whirlpool whirl = new Whirlpool(0, 0, mainStage);
                whirl.centerAtActor(starfish);
                whirl.setOpacity(0.25f);
            }
        }
        
        for (BaseActor sharkActor : BaseActor.getList(mainStage, "starfishcollector.Shark")) {
            Shark shark = (Shark)sharkActor;
            
            if (shark.getX() > 600) {
                shark.MoveLeft();
            }
            if (shark.getX() < 10) {
                shark.MoveRight();
            }
            
            if (turtle.overlaps(sharkActor)) {
                die = true;
                turtle.remove();
                BaseActor youLose = new BaseActor(0, 0, uiStage);
                youLose.loadTextire("game-over.png");
                youLose.centerAtPosition(400, 300);
                youLose.setOpacity(0);
                youLose.addAction(Actions.delay(1));
                youLose.addAction(Actions.after(Actions.fadeIn(1)));
                Timer.schedule(new Task() {
                    
                    @Override
                    public void run() {
                        
                    }
                }, 30000);
                StarfishGame.setActiveScreen(new FinalScreen());
            }
        }
        
        if (BaseActor.count(mainStage, "starfishcollector.Starfish") == 0 && !win) {
            win = true;
            BaseActor youWinMessage = new BaseActor(0, 0, uiStage);
            youWinMessage.loadTextire("you-win.png");
            youWinMessage.centerAtPosition(400, 300);
            youWinMessage.setOpacity(0);
            youWinMessage.addAction(Actions.delay(1));
            youWinMessage.addAction(Actions.after(Actions.fadeIn(1)));
        }
    
    }

}
