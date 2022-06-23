package com.moral.quicktiles.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.moral.quicktiles.QuickTiles;
import com.moral.quicktiles.screens.PlayScreen;

public class Player extends Sprite {

    public enum State {FALLING, JUMPING, STANDING, RUNNING, DEAD}
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private Animation<TextureRegion> playerStand;
    private Animation<TextureRegion> playerWalk;
    private Animation<TextureRegion> playerRun;
    private Animation<TextureRegion> playerJump;
    private Animation<TextureRegion> playerDies;

    public float getStateTimer() {
        return stateTimer;
    }

    private float stateTimer;
    private boolean runningRight;
    private boolean isDead;

    public Player(PlayScreen screen) {
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < 5; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("tiny_adventurer_sheet"), i * 22, 0, 16, 16));
        }
        playerStand = new Animation<>(.1f, frames);
        frames.clear();
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("tiny_adventurer_sheet"), i * 22, 17, 16, 16));
        }
        playerWalk = new Animation<>(.1f, frames);
        frames.clear();
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("tiny_adventurer_sheet"), i * 22, 34, 16, 16));
        }
        playerRun = new Animation<>(.1f, frames);
        frames.clear();
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("tiny_adventurer_sheet"), i * 22, 50, 16, 16));
        }
        playerJump = new Animation<>(.1f, frames);
        frames.clear();
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("tiny_adventurer_sheet"), i * 22, 102, 16, 16));
        }
        playerDies = new Animation<>(.1f, frames);

        definePlayer();
        setBounds(0, 0, 16 / QuickTiles.PPM, 16 / QuickTiles.PPM);
        setRegion(getFrame(0));
    }

    public void update(float dt) {
        if (b2body.getPosition().y < 0) {hit();}
        setPosition(b2body.getPosition().x - 8 / QuickTiles.PPM, b2body.getPosition().y - 7 / QuickTiles.PPM);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case DEAD:
                region = playerDies.getKeyFrame(stateTimer);
                break;
            case JUMPING:
                region = playerJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = playerRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = playerStand.getKeyFrame(stateTimer, true);
                break;
        }

        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        if (isDead){
            return State.DEAD;
        } else if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
            return State.JUMPING;
        } else if(b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if(b2body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else {
            return State.STANDING;
        }
    }

    private void definePlayer() {
        BodyDef bdef = new BodyDef();
        //initial position
        bdef.position.set(300 / QuickTiles.PPM, 100 / QuickTiles.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / QuickTiles.PPM);
        fdef.filter.categoryBits = QuickTiles.PLAYER_BIT;
        fdef.filter.maskBits = QuickTiles.GROUND_BIT |
                QuickTiles.COIN_BIT
                | QuickTiles.PLATFORM_BIT
                | QuickTiles.ENEMY_BIT
                | QuickTiles.OBJECT_BIT
                | QuickTiles.ENEMY_HEAD_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / QuickTiles.PPM, 6 / QuickTiles.PPM), new Vector2(2 / QuickTiles.PPM, 6 / QuickTiles.PPM));
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData("head");

        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-2 / QuickTiles.PPM, -7 / QuickTiles.PPM), new Vector2(2 / QuickTiles.PPM, -7 / QuickTiles.PPM));
        fdef.shape = feet;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData("feet");

        EdgeShape left = new EdgeShape();
        left.set(new Vector2(-4 / QuickTiles.PPM, 4 / QuickTiles.PPM), new Vector2(-4 / QuickTiles.PPM, -5 / QuickTiles.PPM));
        fdef.shape = left;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData("left");

        EdgeShape right = new EdgeShape();
        right.set(new Vector2(4 / QuickTiles.PPM, 4 / QuickTiles.PPM), new Vector2(4 / QuickTiles.PPM, -5 / QuickTiles.PPM));
        fdef.shape = right;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData("right");
    }

    public void hit() {
        //QuickTiles.manager.get("audio/music/aleph.mp4", Music.class).stop();
        isDead = true;
        Filter filter = new Filter();
        filter.maskBits = QuickTiles.NOTHING_BIT;
        for (Fixture fixture : b2body.getFixtureList()) {fixture.setFilterData(filter);}
        b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
    }
}
