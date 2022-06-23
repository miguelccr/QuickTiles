package com.moral.quicktiles.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.moral.quicktiles.QuickTiles;
import com.moral.quicktiles.scenes.Hud;
import com.moral.quicktiles.sprites.Background;
import com.moral.quicktiles.sprites.Enemy;
import com.moral.quicktiles.sprites.Player;
import com.moral.quicktiles.sprites.Snail;
import com.moral.quicktiles.tools.B2WorldCreator;
import com.moral.quicktiles.tools.WorldContactListener;

public class PlayScreen implements Screen {

    private final QuickTiles game;
    private TextureAtlas atlas;
    private Background[] backgrounds;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    //sprites
    private Player player;

    private Music music;

    public PlayScreen(QuickTiles game) {
        atlas = new TextureAtlas("GFX/unnamed.atlas");
        backgrounds = new Background[]{
                new Background(new Texture("GFX/bg_0.png"), 10, 30),
                new Background(new Texture("GFX/bg_1.png"), 20, 10),
                new Background(new Texture("GFX/bg_2.png"), 40, 0)};

        this.game = game;

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(QuickTiles.V_WIDTH / QuickTiles.PPM, QuickTiles.V_HEIGHT / QuickTiles.PPM, gameCam);
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("GFX/level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / QuickTiles.PPM);
        gameCam.position.set(gamePort.getWorldWidth() / 2f, gamePort.getWorldHeight() / 2f, 0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = new Player(this);

        world.setContactListener(new WorldContactListener());

        music = QuickTiles.manager.get("audio/music/aleph.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(.3f);
        music.play();
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    private void handleInput() {
        if (player.currentState != Player.State.DEAD) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP) && player.b2body.getLinearVelocity().y == 0) {
                player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) {
                player.b2body.applyLinearImpulse(new Vector2(.1f, 0), player.b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) {
                player.b2body.applyLinearImpulse(new Vector2(-.1f, 0), player.b2body.getWorldCenter(), true);
            }
        }
    }

    public void update(float dt) {
        handleInput();
        for (Background bg : backgrounds) {
            bg.update(dt, player.b2body.getLinearVelocity().x, gameCam.position.x);
        }

        world.step(1/60f, 6, 2);

        player.update(dt);
        for (Enemy enemy : creator.getSnails()) {
            enemy.update(dt);
            if (enemy.getX() < player.getX() + 224 / QuickTiles.PPM) {
                enemy.b2body.setActive(true);
            }
        }
        hud.update(dt);

        if (player.currentState != Player.State.DEAD) {
            gameCam.position.x = player.b2body.getPosition().x;
        }

        gameCam.update();
        renderer.setView(gameCam);
    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0,0, 0, 1);

        //render bg
        game.batch.begin();
        for (Background bg : backgrounds) {
            bg.draw(game.batch);
        }
        game.batch.end();

        //render game map
        renderer.render();

        //render Box2DDebugLines
        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getSnails()) {
            enemy.draw(game.batch);
        }
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    public boolean gameOver(){
        if(player.currentState == Player.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World  getWorld() {
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        for (Background bg : backgrounds) {
            bg.dispose();
        }
    }
}
