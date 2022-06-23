package com.moral.quicktiles.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class Background implements Disposable {

    private Texture texture;
    private int speed;
    private Vector2 pos0, pos1, pos2;
    private float prevPos0, prevPos1, prevPos2;

    public Background(Texture texture, int speed, int height) {
        this.texture = texture;
        this.speed = speed;
        pos0 = new Vector2(0, height);
        prevPos0 = pos0.x;
        pos1 = new Vector2(texture.getWidth(), height);
        prevPos1 = pos1.x;
        pos2 = new Vector2(texture.getWidth() * 2, height);
        prevPos2 = pos2.x;
    }

    public void update(float dt, float mov, float camPos) {
        pos0.add(-speed * dt * mov, 0);
        pos1.add(-speed * dt * mov, 0);
        pos2.add(-speed * dt * mov, 0);
}

    public void draw(SpriteBatch batch) {
        batch.draw(texture, pos0.x, pos0.y);
        batch.draw(texture, pos1.x, pos1.y);
        batch.draw(texture, pos2.x, pos2.y);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
