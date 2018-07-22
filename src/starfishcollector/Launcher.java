package starfishcollector;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

/**
 *
 * @author j3kaiii
 */
public class Launcher {
    public static void main(String[] args) {
        Game myGame = new StarfishGame();
        LwjglApplication launcher = new LwjglApplication(myGame, "Starfish Collector", 800, 600);
    }

}
