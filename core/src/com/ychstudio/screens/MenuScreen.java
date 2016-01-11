package com.ychstudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ychstudio.SpaceRocket;
import com.ychstudio.gamesys.GM;

public class MenuScreen implements Screen {
    
    private SpaceRocket game;
    private SpriteBatch batch;
    
    private FitViewport viewport;
    private Stage stage;
    
    private Color selectedColor = new Color(0.8f, 0.8f, 0.5f, 1.0f);
    private Color unselectedColor = new Color(0.6f, 0.6f, 0.6f, 1.0f);
    private int selected = 0;
    private Array<Label> options;

    private Array<Image> rocks;

    private AssetManager assetManager;

    private boolean paused;
    
    public MenuScreen(SpaceRocket game) {
        this.game = game;
        assetManager = GM.getAssetManager();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/MONOFONT.TTF"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 30;
        BitmapFont monoFont30 = generator.generateFont(parameter);

        parameter.size = 64;
        parameter.color = new Color(0.8f, 0.8f, 0.8f, 1.0f);
        parameter.borderWidth = 2.8f;
        parameter.borderColor = new Color(0.6f, 0.6f, 0.6f, 1.0f);
        BitmapFont monoFont64 = generator.generateFont(parameter);
        generator.dispose();

        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport, batch);

        // rocks
        rocks = new Array<>();
        for (int i = 0; i < 60; i++) {
            Image rock = new Image(assetManager.get("images/Rock.png", Texture.class));
            rock.setScale(MathUtils.random(0.4f, 1.2f));
            rock.setOrigin(rock.getWidth() / 2, rock.getHeight() / 2);
            rock.setPosition(MathUtils.random(0, Gdx.graphics.getWidth()), MathUtils.random(0, Gdx.graphics.getHeight()));
            rock.addAction(Actions.forever(Actions.parallel(Actions.rotateBy(MathUtils.random(66f), 1f), Actions.moveBy(MathUtils.random(60f) - 30f, MathUtils.random(60f) - 30f, MathUtils.random(1f, 3f)))));
            stage.addActor(rock);
            rocks.add(rock);
        }

        Label titleLabel = new Label("SpaceRocket", new Label.LabelStyle(monoFont64, Color.WHITE));
        titleLabel.setPosition((Gdx.graphics.getWidth() - titleLabel.getWidth()) / 2, Gdx.graphics.getHeight() - 180);
        
        Label startGameLabel = new Label("Start", new Label.LabelStyle(monoFont30, Color.WHITE));
        startGameLabel.setPosition((Gdx.graphics.getWidth() - startGameLabel.getWidth()) / 2, (Gdx.graphics.getHeight() - startGameLabel.getHeight()) / 2);
        
        Label exitGameLabel = new Label("Exit", new Label.LabelStyle(monoFont30, Color.WHITE));
        exitGameLabel.setPosition((Gdx.graphics.getWidth() - exitGameLabel.getWidth()) / 2, (Gdx.graphics.getHeight() - exitGameLabel.getHeight()) / 2 - 28);
        
        options = new Array<>();
        options.add(startGameLabel);
        options.add(exitGameLabel);

        stage.addActor(titleLabel);
        stage.addActor(startGameLabel);
        stage.addActor(exitGameLabel);
        
        startGameLabel.addAction(Actions.color(selectedColor));
        exitGameLabel.addAction(Actions.color(unselectedColor));



        paused = false;
        
    }
    
    public void update(float delta) {
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            int preselected = selected;
            selected = Math.max(0, selected - 1);
            options.get(preselected).addAction(Actions.color(unselectedColor, 0.2f));
            options.get(selected).addAction(Actions.color(selectedColor, 0.2f));
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            int preselected = selected;
            selected = Math.min(options.size-1, selected + 1);
            options.get(preselected).addAction(Actions.color(unselectedColor, 0.2f));
            options.get(selected).addAction(Actions.color(selectedColor, 0.2f));
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selected == 0) {
                game.startGame();
            }
            else if (selected == 1) {
                game.exitGame();
            }
        }

        for (Image rock : rocks) {
            if (rock.getX() < -rock.getWidth()) {
                rock.setX(Gdx.graphics.getWidth() + rock.getWidth() / 2);
            }
            else if (rock.getX() > Gdx.graphics.getWidth() + rock.getWidth()) {
                rock.setX(-rock.getWidth() / 2);
            }

            if (rock.getY() < -rock.getHeight()) {
                rock.setY(Gdx.graphics.getHeight() + rock.getHeight() / 2);
            }
            else if (rock.getY() > Gdx.graphics.getHeight() + rock.getHeight()) {
                rock.setY(-rock.getHeight() / 2);
            }
        }
        
        stage.act(delta);
    }

    @Override
    public void render(float delta) {
        if (!paused) {
            update(delta);
        }
        
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.3f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        
    }

}
