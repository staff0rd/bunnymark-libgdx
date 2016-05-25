package in.atqu.bunnymark;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/*
  Originally from https://github.com/johngirvin/bunnymark-libgdx/blob/master/core/src/com/nivrig/BunnyMark.java
 */

public class BunnyMark extends ApplicationAdapter {
	private SpriteBatch batch;
    private Sprite sprite;
    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private static RandomXS128 random = new RandomXS128();
    private static float minX = 0;
    private static float maxX = 0;
    private static float minY = 0;
    private static float maxY = 0;

    private BitmapFont labelFont;
    private float labelX = 5;
    private float labelY;
    private String bunnyLabel;
    private String fpsLabel = "";
    private String sizeLabel = "";

    public class Bunny {
    	float x, y, speedX, speedY;
    	public Bunny() {
    		y = maxY/2;
    		x = 10;
    		speedY = random.nextInt(500) + 250;
        	speedX = random.nextInt(500) + 250 - 500;
    	}
    }
    private Array<Bunny> bunnies = new Array<Bunny>();

    @Override
	public void create() {

        batch = new SpriteBatch(5400);
        labelFont = new BitmapFont();
        labelFont.setColor(Color.WHITE);
        sprite = new Sprite(new Texture("wabbit_alpha.png"));
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        bunnies.add(new Bunny());
        bunnies.add(new Bunny());
        bunnyLabel = "2";
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (Gdx.app.getType() != Application.ApplicationType.Desktop && Gdx.app.getType() != Application.ApplicationType.WebGL)
            viewport.setUnitsPerPixel(1/Gdx.graphics.getDensity());
        maxX = camera.viewportWidth - sprite.getWidth();
        maxY = camera.viewportHeight - sprite.getHeight();
        labelY = camera.viewportHeight - 5;
        // String.format not available in GWT
        sizeLabel = width + "x" + height + "(" + Math.round(camera.viewportWidth) + "x" + Math.round(camera.viewportHeight) + ") +" + Gdx.graphics.getDensity();
    }

    private void renderBunny(Bunny bunny, float dt) {
        bunny.x += dt * bunny.speedX;
        if (bunny.x < minX) { bunny.x = minX; bunny.speedX = -bunny.speedX; }
        if (bunny.x > maxX) { bunny.x = maxX; bunny.speedX = -bunny.speedX; }

        bunny.y += dt * bunny.speedY;
        if (bunny.y < minY) { bunny.y = minY; bunny.speedY = -bunny.speedY; }
        if (bunny.y > maxY) { bunny.y = maxY; bunny.speedY = -bunny.speedY; }

        sprite.setPosition(bunny.x,bunny.y);
        sprite.draw(batch);
    }

    private float fpsTime  = 0;
    private int   fpsCount = 0;

    @Override
	public void render() {
    	
    	if (Gdx.input.isTouched()) {
    		for (int i = 0; i < 10; i++) {
    			bunnies.add(new Bunny());
    		}
    		bunnyLabel = String.valueOf(bunnies.size);
    	}
    	
        float dt = Gdx.graphics.getDeltaTime();

        fpsTime  += dt;
        fpsCount++;
        if (fpsTime > 1.0) {
            fpsLabel = String.valueOf((((float)fpsCount) / fpsTime)).substring(0,5) + " FPS ";
            fpsTime  = 0;
            fpsCount = 0;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int i = 0, c = bunnies.size; i < c; i++) {
            renderBunny(bunnies.get(i), dt);
        }

        labelFont.draw(batch, bunnyLabel, labelX, labelY);
        labelFont.draw(batch, fpsLabel  , labelX, labelY - 15);
        labelFont.draw(batch, sizeLabel, labelX, labelY - 30);

		batch.end();
	}
}
