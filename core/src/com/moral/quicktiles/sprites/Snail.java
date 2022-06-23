package com.moral.quicktiles.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.moral.quicktiles.QuickTiles;
import com.moral.quicktiles.screens.PlayScreen;

public class Snail extends Enemy {

    private float stateTimer;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy = false;
    private boolean destroyed = false;

    public Snail(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<>();
        for (int i = 0; i < 8; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("SnailEnemy"), i * 36, 0, 36, 16));
        }
        walkAnimation = new Animation(.2f, frames);
        stateTimer = 0;
        setBounds(getX(), getY(), 36 / QuickTiles.PPM, 16 / QuickTiles.PPM);
    }

    public void update(float dt) {
        stateTimer += dt;
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            setRegion(walkAnimation.getKeyFrame(4, true));
            stateTimer = 0;
        } else if (!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - (getHeight() / 2));
            TextureRegion region = walkAnimation.getKeyFrame(stateTimer, true);
            setRegion(getFrame(dt));
        }
    }

    public TextureRegion getFrame(float dt) {
        TextureRegion region = walkAnimation.getKeyFrame(stateTimer, true);
        if ((b2body.getLinearVelocity().x > 0) && !region.isFlipX()) {
            region.flip(true, false);
        } else if((b2body.getLinearVelocity().x < 0) && region.isFlipX()) {
            region.flip(true, false);
        }
        return region;
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / QuickTiles.PPM);
        fdef.filter.categoryBits = QuickTiles.ENEMY_BIT;
        fdef.filter.maskBits = QuickTiles.GROUND_BIT |
                QuickTiles.PLAYER_BIT |
                QuickTiles.PLATFORM_BIT |
                QuickTiles.ENEMY_BIT |
                QuickTiles.OBJECT_BIT |
                QuickTiles.COIN_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1 / QuickTiles.PPM);
        vertice[1] = new Vector2(5, 8).scl(1 / QuickTiles.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / QuickTiles.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / QuickTiles.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = .5f;
        fdef.filter.categoryBits = QuickTiles.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch) {
        if (!destroyed || stateTimer < 2) {
            super.draw(batch);
        }
    }

    @Override
    public void hitOnHead() {
        setToDestroy = true;
    }
}
