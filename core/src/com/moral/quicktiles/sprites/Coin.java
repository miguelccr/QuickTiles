package com.moral.quicktiles.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.moral.quicktiles.QuickTiles;
import com.moral.quicktiles.scenes.Hud;
import com.moral.quicktiles.screens.PlayScreen;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Coin extends InteractiveTileObject {

    private static TiledMapTileSet tileSet;

    public Coin(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        tileSet = map.getTileSets().getTileSet("Fruits_colored_outline");
        fixture.setUserData(this);
        setCategoryFilter(QuickTiles.COIN_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Coin", "Head Collision");
        setCategoryFilter(QuickTiles.DESTROYED_BIT);
        getCell(1).setTile(null);
        Hud.addScore(100);
        QuickTiles.manager.get("audio/sounds/drop.wav", Sound.class).play();
    }

    @Override
    public void onFeetHit() {
        Gdx.app.log("Coin", "Feet Collision");
        setCategoryFilter(QuickTiles.DESTROYED_BIT);
        getCell(1).setTile(null);
        Hud.addScore(200);
        QuickTiles.manager.get("audio/sounds/drop.wav", Sound.class).play();
    }

    @Override
    public void onLeftHit() {
        Gdx.app.log("Coin", "Left Collision");
        setCategoryFilter(QuickTiles.DESTROYED_BIT);
        getCell(1).setTile(null);
        Hud.addScore(200);
        QuickTiles.manager.get("audio/sounds/drop.wav", Sound.class).play();
    }

    @Override
    public void onRightHit() {
        Gdx.app.log("Coin", "Right Collision");
        setCategoryFilter(QuickTiles.DESTROYED_BIT);
        getCell(1).setTile(null);
        Hud.addScore(200);
        QuickTiles.manager.get("audio/sounds/drop.wav", Sound.class).play();
    }
}
