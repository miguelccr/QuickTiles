package com.moral.quicktiles.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.moral.quicktiles.QuickTiles;
import com.moral.quicktiles.screens.PlayScreen;
import com.moral.quicktiles.sprites.Coin;
import com.moral.quicktiles.sprites.Platform;
import com.moral.quicktiles.sprites.Snail;

public class B2WorldCreator {
    private Array<Snail> snails;

    public B2WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        //create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for (MapObject object : map.getLayers().get("ground").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / QuickTiles.PPM, (rect.getY() + rect.getHeight() / 2) / QuickTiles.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / QuickTiles.PPM, (rect.getHeight() / 2) / QuickTiles.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = QuickTiles.GROUND_BIT;
            body.createFixture(fdef);
        }

        //create walls bodies/fixtures
        for (MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / QuickTiles.PPM, (rect.getY() + rect.getHeight() / 2) / QuickTiles.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / QuickTiles.PPM, (rect.getHeight() / 2) / QuickTiles.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = QuickTiles.OBJECT_BIT;
            body.createFixture(fdef);
        }

        //create platforms bodies/fixtures
        for (MapObject object : map.getLayers().get("platforms").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Platform(screen, rect);
        }

        //create fruits bodies/fixtures
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Coin(screen, rect);
        }

        //create snails
        snails = new Array<Snail>();
            for (MapObject object : map.getLayers().get("snail").getObjects().getByType(RectangleMapObject.class)) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                snails.add(new Snail(screen, rect.getX() / QuickTiles.PPM, rect.getY() / QuickTiles.PPM));
            }
    }

    public Array<Snail> getSnails() {
        return snails;
    }
}
