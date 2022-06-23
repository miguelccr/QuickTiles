package com.moral.quicktiles.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.moral.quicktiles.QuickTiles;
import com.moral.quicktiles.screens.PlayScreen;

public class Platform extends InteractiveTileObject {

    public Platform(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(QuickTiles.PLATFORM_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Platform", "Head Collision");
        setCategoryFilter(QuickTiles.DESTROYED_BIT);
        getCell(0).setTile(null);
    }

    @Override
    public void onFeetHit() {
        Gdx.app.log("Platform", "Feet Collision");
    }

    @Override
    public void onLeftHit() {
        Gdx.app.log("Platform", "Left Collision");

    }

    @Override
    public void onRightHit() {
        Gdx.app.log("Platform", "Right Collision");
    }
}
