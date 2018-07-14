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

/**
 *
 * @author j3kaiii
 */
public class BaseActor extends Actor{
    
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
    
    public void setBoundaryRectangle() {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = {0,0, w,0, w,h, 0,h};
        boundaryPolygon = new Polygon(vertices);
    }
    
    public void setMaxSpeed(float ms) {
        maxSpeed = ms;
    }
    
    public void setDeceleration(float dec) {
        deceleration = dec;
    }
    
    public boolean isMoving() {
        return (getSpeed() > 0);
    }
    
    public void setAcceleration(float acc) {
        acceleration = acc;
    }
    
    public void accelerateAtAngle(float angle) {
        accelerationVec.add(new Vector2(acceleration, 0).setAngle(angle));
    }
    
    public void accelerateForward() {
        accelerateAtAngle(getRotation());
    }
    
    public void setSpeed(float speed) {
        if (velocityVec.len() == 0) {
            velocityVec.set(speed, 0);
        } else {
            velocityVec.setLength(speed);
        }
    }
    
    public float getSpeed() {
        return velocityVec.len();
    }
    
    public void setMotionAngle(float angle) {
        velocityVec.setAngle(angle);
    }
    
    public float getMotionAngle() {
        return velocityVec.angle();
    }
    
    public void setAnimation(Animation anim) {
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
    
    public Animation loadTextire(String fileName) {
        String[] fileNames = new String[1];
        fileNames[0] = fileName;
        return loadAnimationFromFiles(fileNames, 1, true);
    }
    
    public boolean isAnimationFinished() {
        return animation.isAnimationFinished(elapsedTime);
    }
    
    public void setAnimationPaused(boolean pause) {
        animationPaused = pause;
    }
    
    public void applyPhysics(float dt) {
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
    
    public void setBoundaryPolygon(int numSides) {
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
    
    public Polygon getBoundaryPolygon() {
        boundaryPolygon.setPosition(getX(), getY());
        boundaryPolygon.setOrigin(getOriginX(), getOriginY());
        boundaryPolygon.setRotation(getRotation());
        boundaryPolygon.setScale(getScaleX(), getScaleY());
        return boundaryPolygon;
    }
    
    public boolean overlaps(BaseActor other) {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();
        
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
            return false;
        }
        
        return Intersector.overlapConvexPolygons(poly1, poly2);
    }
        
    public void act(float dt) {
        super.act(dt);
        if (!animationPaused) {
            elapsedTime += dt;
        }
    }
    
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);
        
        if (animation != null && isVisible()) {
            batch.draw(animation.getKeyFrame(elapsedTime),
                    getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }
}
