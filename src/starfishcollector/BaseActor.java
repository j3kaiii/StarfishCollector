package starfishcollector;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import java.util.ArrayList;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 *
 * @author j3kaiii
 */
public class BaseActor extends Actor{
    
    private static Rectangle worldBounds;
    
    private Animation animation;
    private float elapsedTime;
    private boolean animationPaused;
    private Vector2 velocityVec;
    private Vector2 accelerationVec;
    private float acceleration;
    private float maxSpeed;
    private float deceleration;
    private Polygon boundaryPolygon;
    
    public BaseActor(float x, float y, Stage s) {
        super();
        setPosition(x, y);
        s.addActor(this);
        
        animation = null;
        elapsedTime = 0;
        animationPaused = false;
        velocityVec = new Vector2(0, 0);
        accelerationVec = new Vector2(0, 0);
        acceleration = 0;
        maxSpeed = 1000;
        deceleration = 0;
    }
    
    public void setBoundaryRectangle() {        //получаем рамку для коллизий
        float w = getWidth();
        float h = getHeight();
        float[] vertices = {0,0, w,0, w,h, 0,h};
        boundaryPolygon = new Polygon(vertices);
    }
    
    public void setMaxSpeed(float ms) {         //устанавливаем макс скорость
        maxSpeed = ms;
    }
    
    public void setDeceleration(float dec) {    //скорость торможения
        deceleration = dec;
    }
    
    public boolean isMoving() {                 //проверяем, движется или нет
        return (getSpeed() > 0);
    }
    
    public void setAcceleration(float acc) {    //устанавливаем ускорение
        acceleration = acc;
    }
    
    public void accelerateAtAngle(float angle) {    //ускоряем в сторону
        accelerationVec.add(new Vector2(acceleration, 0).setAngle(angle));
    }
    
    public void accelerateForward() {           //ускоряем вперед
        accelerateAtAngle(getRotation());
    }
    
    public void setSpeed(float speed) {         //устанавливаем скорость
        if (velocityVec.len() == 0) {
            velocityVec.set(speed, 0);
        } else {
            velocityVec.setLength(speed);
        }
    }
    
    public float getSpeed() {                   //получаем скорость объекта
        return velocityVec.len();
    }
    
    public void setMotionAngle(float angle) {   //поворачиваем объект в сторону
        velocityVec.setAngle(angle);
    }
    
    public float getMotionAngle() {             //получаем сторону, куда смотрит объект
        return velocityVec.angle();
    }
    
    public void setAnimation(Animation anim) {  //устанавливаем анимацию
        animation = anim;
        TextureRegion tr = animation.getKeyFrame(0);
        float w = tr.getRegionWidth();
        float h = tr.getRegionHeight();
        setSize(w, h);
        setOrigin(w/2, h/2);
        
        if (boundaryPolygon == null) {
            setBoundaryRectangle();
        }
    }
    
    //создаем анимацию из списка файлов
    public Animation loadAnimationFromFiles(String[] fileNames, float frameDuration, boolean loop) {
        int fileCount = fileNames.length;
        Array<TextureRegion> textureArray = new Array<TextureRegion>();
        
        for (int n = 0; n < fileCount; n++) {
            String fileName = fileNames[n];
            Texture texture = new Texture(Gdx.files.internal(fileName));
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            textureArray.add(new TextureRegion(texture));
        }
        
        Animation anim = new Animation(frameDuration, textureArray);
        if (loop) {
            anim.setPlayMode(PlayMode.LOOP);
        } else {
            anim.setPlayMode(PlayMode.NORMAL);
        }
        
        if (animation == null) {
            setAnimation(anim);
        }
        
        return anim;
    }
    
    //создаем анимацию из спрайтлоста
    public Animation loadAnimationFromSheet(String fileName, int rows, int cols, float frameDuration, boolean loop) {
        Texture texture = new Texture(Gdx.files.internal(fileName));
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        int frameWidth = texture.getWidth() / cols;
        int frameHeight = texture.getHeight() / rows;
        
        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);
        
        Array<TextureRegion> textureArray = new Array<TextureRegion>();
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                textureArray.add(temp[r][c]);
            }
        }
        
        Animation anim = new Animation(frameDuration, textureArray);
        
        if (loop) {
            anim.setPlayMode(PlayMode.LOOP);
        } else {
            anim.setPlayMode(PlayMode.NORMAL);
        }
        
        if (animation == null) {
            setAnimation(anim);
        }
        
        return anim;
    }
    
    public Animation loadTextire(String fileName) { //анимация из одного кадра
        String[] fileNames = new String[1];
        fileNames[0] = fileName;
        return loadAnimationFromFiles(fileNames, 1, true);
    }
    
    public boolean isAnimationFinished() {          //проверяем закончилась ли анимация
        return animation.isAnimationFinished(elapsedTime);
    }
    
    public void setAnimationPaused(boolean pause) { //анимация на паузу
        animationPaused = pause;
    }
    
    public void applyPhysics(float dt) {            //применяем все физические изменения к объекту
        velocityVec.add(accelerationVec.x * dt, accelerationVec.y * dt);
        float speed = getSpeed();
        if (accelerationVec.len() == 0) {
            speed -= deceleration * dt;
        }
        speed = MathUtils.clamp(speed, 0, maxSpeed);
        setSpeed(speed);
        moveBy(velocityVec.x * dt, velocityVec.y * dt);
        accelerationVec.set(0, 0);
    }
    
    public void setBoundaryPolygon(int numSides) {  //полигон коллизий
        float w = getWidth();
        float h = getHeight();
        
        float[] vertices = new float[2 * numSides];
        for (int i = 0; i < numSides; i++) {
            float angle = i * 6.28f / numSides;
            vertices[2*i] = w/2 * MathUtils.cos(angle) + w/2;
            vertices[2*i+1] = h/2 * MathUtils.sin(angle) + h/2;
        }
        boundaryPolygon = new Polygon(vertices);
    }
    
    public Polygon getBoundaryPolygon() {           //получаем полигон коллизий
        boundaryPolygon.setPosition(getX(), getY());
        boundaryPolygon.setOrigin(getOriginX(), getOriginY());
        boundaryPolygon.setRotation(getRotation());
        boundaryPolygon.setScale(getScaleX(), getScaleY());
        return boundaryPolygon;
    }
    
    public boolean overlaps(BaseActor other) {      //проверяем пересечение с другим полигоном
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();
        
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
            return false;
        }
        
        return Intersector.overlapConvexPolygons(poly1, poly2);
    }
    
    public Vector2 preventOverlap(BaseActor other) {    //прекращение движения при пересечении с другим полигоном
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();
        
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
            return null;
        }
        
        MinimumTranslationVector mtv = new MinimumTranslationVector();
        boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);
        
        if (!polygonOverlap) return null;
        
        this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);
        return mtv.normal;
    }
    
    public void centerAtPosition(float x, float y) {    //размещает объект в по координатам
        setPosition(x - getWidth()/2, y - getHeight()/2);
    }
    
    public void centerAtActor(BaseActor other) {        //размещает объект по другому объекту
        centerAtPosition(other.getX() + other.getWidth()/2, other.getY() + other.getHeight()/2);
    }
    
    public void setOpacity(float opacity) {             //устанавливаем прозрачность
        this.getColor().a = opacity;
    }
        
    public void act(float dt) {                         //применение изменений в объекте
        super.act(dt);
        if (!animationPaused) {
            elapsedTime += dt;
        }
    }
    
    public void draw(Batch batch, float parentAlpha) {  //отрисовка объекта после всех телодвижений
        super.draw(batch, parentAlpha);
        
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);
        
        if (animation != null && isVisible()) {
            batch.draw(animation.getKeyFrame(elapsedTime),
                    getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }
    
    
    public static ArrayList<BaseActor> getList(Stage stage, String className) { //сбор инстансов в один список
        ArrayList<BaseActor> list = new ArrayList<BaseActor>();                 //по имени класса
        
        Class theClass = null;
        try {
            theClass = Class.forName(className);
        } catch (Exception error) {
            error.printStackTrace();
        }
        
        for (Actor a : stage.getActors()) {
            if (theClass.isInstance(a)) {
                list.add((BaseActor)a);
            }
        }
        
        return list;
    }
    
    public void boundToWorld() {
        if(getX() < 0) { setX(0); }
        if(getX() + getWidth() > worldBounds.width) { setX(worldBounds.width - getWidth()); }
        if(getY() < 0) { setY(0); }
        if(getY() + getHeight() > worldBounds.height) { setY(worldBounds.height - getHeight()); }
    }
    
    public void alighCamera() {
        Camera cam = this.getStage().getCamera();
        Viewport v = this.getStage().getViewport();
        
        //центрируем камеру на героя
        cam.position.set(this.getX() + this.getOriginX(),
                        this.getY() + this.getOriginY(), 
                        0);
        
        //не даем камере выйти за границы карты
        cam.position.x = MathUtils.clamp(cam.position.x,
                                        cam.viewportWidth/2,
                                        worldBounds.width - cam.viewportWidth/2);
        cam.position.y = MathUtils.clamp(cam.position.y,
                                        cam.viewportHeight/2,
                                        worldBounds.height - cam.viewportHeight/2);
        cam.update();
    }
    
    public static int count(Stage stage, String className) {            //считаем инстансы в списке
        return getList(stage, className).size();
    }
    
    public static void setWorldBounds(float width, float height) {
        worldBounds = new Rectangle(0, 0, width, height);
    }
    
    public static void setWorldBounds(BaseActor ba) {
        setWorldBounds(ba.getWidth(), ba.getHeight());
    }
}

