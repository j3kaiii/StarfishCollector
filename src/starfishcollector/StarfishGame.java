package starfishcollector;

/**
 *
 * @author j3kaiii
 */
public class StarfishGame extends BaseGame{

    @Override
    public void create() {
        setActiveScreen(new MenuScreen());
    }

}
