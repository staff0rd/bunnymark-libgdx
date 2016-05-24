package in.atqu.bunnymark;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;

public class BunnyMark extends ApplicationAdapter {
	private SpriteBatch batch;

    private Sprite sprite;

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

    private class Bunny {
        float x, y, xv, yv, r, rv;
    }
    private Array<Bunny> bunnies = new Array<Bunny>();

	@Override
	public void create() {
		batch = new SpriteBatch(5400);

        labelFont = new BitmapFont();
        labelFont.setColor(Color.WHITE);

        sprite = new Sprite(new Texture("wabbit_alpha.png"));

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        moreBunnies(1000);

        switch (Gdx.app.getType()) {
            case iOS:
            case Android:
                Gdx.input.setInputProcessor(new GestureDetector(new GestureDetector.GestureAdapter() {
                    @Override
                    public boolean fling(float velocityX, float velocityY, int button) {
                        if (velocityX < 0) {
                            lessBunnies();
                        } else {
                            moreBunnies(100);
                        }
                        return true;
                    }

                    @Override
                    public boolean tap(float screenX, float screenY, int count, int button) {
                        moreBunnies(1000);
                        return true;
                    }
                }));
                break;

            default:
                Gdx.input.setInputProcessor(new InputAdapter() {
                	@Override
                	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                		moreBunnies(1000);
						return false;
                	}
                	
                    @Override
                    public boolean keyUp(int keycode) {
                        switch (keycode) {
                            case Input.Keys.Q:
                                lessBunnies();
                                return true;
                            case Input.Keys.W:
                                moreBunnies(1000);
                                return true;
                            case Input.Keys.S:
                                moreBunnies(10000);
                                return true;
                        }
                        return false;
                    }
                });
                break;
        }
    }

    @Override
    public void resize(int w, int h) {
        minX = 0;
        maxX = w - sprite.getWidth();
        minY = 0;
        maxY = h - sprite.getHeight();

        labelY = h - 5;
    }

    private void moreBunnies(int more) {
        for (int i = 0; i < more; i++) {
            bunnies.add(initBunny(new Bunny()));
        }

        bunnyLabel = String.valueOf(bunnies.size);
    }

    private void lessBunnies() {
        if (bunnies.size <= 100) {
            bunnies.clear();
        } else {
            bunnies.removeRange(bunnies.size-100, bunnies.size-1);
        }

        bunnyLabel = String.valueOf(bunnies.size);
    }

    private Bunny initBunny(Bunny bunny) {
        bunny.x = minX + random.nextInt(MathUtils.floor(maxX - minX));
        bunny.y = minY + random.nextInt(MathUtils.floor(maxY - minY));

        do {
            bunny.xv = 100 - random.nextInt(200);
        } while (bunny.xv < 10 && bunny.xv > -10);

        do {
            bunny.yv = 100 - random.nextInt(200);
        } while (bunny.yv < 10 && bunny.yv > -10);

        bunny.r = random.nextInt(360);

        do {
            bunny.rv = 50 - random.nextInt(100);
        } while (bunny.rv < 10 && bunny.rv > -10);

        return bunny;
    }

    private void renderBunny(Bunny bunny, float dt) {
        bunny.x += dt * bunny.xv;
        if (bunny.x < minX) { bunny.x = minX; bunny.xv = -bunny.xv; }
        if (bunny.x > maxX) { bunny.x = maxX; bunny.xv = -bunny.xv; }

        bunny.y += dt * bunny.yv;
        if (bunny.y < minY) { bunny.y = minY; bunny.yv = -bunny.yv; }
        if (bunny.y > maxY) { bunny.y = maxY; bunny.yv = -bunny.yv; }

        bunny.r = (bunny.r + dt * bunny.rv) % 360;

        sprite.setPosition(bunny.x,bunny.y);
        sprite.setRotation(bunny.r);
        sprite.setScale(0.1f + Math.abs(MathUtils.sinDeg(bunny.r)));
        sprite.draw(batch);
    }

    private float fpsTime  = 0;
    private int   fpsCount = 0;

    @Override
	public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        fpsTime  += dt;
        fpsCount++;
        if (fpsTime > 1.0) {
            fpsLabel = String.valueOf((((float)fpsCount) / fpsTime)).substring(0,5) + " FPS";
            fpsTime  = 0;
            fpsCount = 0;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        for (int i = 0, c = bunnies.size; i < c; i++) {
            renderBunny(bunnies.get(i), dt);
        }

        labelFont.draw(batch, bunnyLabel, labelX, labelY);
        labelFont.draw(batch, fpsLabel  , labelX, labelY - 15);

		batch.end();
	}
}
