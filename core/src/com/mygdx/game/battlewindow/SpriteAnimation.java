package com.mygdx.game.battlewindow;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class SpriteAnimation extends Animation {

    public SpriteAnimation(float frameDuration, Array<? extends TextureRegion> keyFrames) {
        super(frameDuration, keyFrames);
    }

    public SpriteAnimation(float frameDuration, Array<? extends TextureRegion> keyFrames, PlayMode playMode) {
        super(frameDuration, keyFrames, playMode);
    }

    public SpriteAnimation(float frameDuration, TextureRegion... keyFrames) {
        super(frameDuration, keyFrames);
    }

    private float scale = 1f;

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void dispose() {
        getKeyFrame(0f).getTexture().dispose();
    }
    // Run bottomLeft or topRight before using this

    private float offX;
    private float offY;
    public boolean paused = false;
    public boolean visible = true;

    TextureRegion region;

    public void draw(float time, Batch batch) {
        if (visible) {
            if (!paused) {
                region = getKeyFrame(time);
            }
            offX = ((TextureAtlas.AtlasRegion) region).offsetX * scale;
            offY = ((TextureAtlas.AtlasRegion) region).offsetY * scale;
            batch.draw(region, x + offX, y + offY, region.getRegionWidth() * scale, region.getRegionHeight() * scale);
        }
    }

    // 400/240
    private float x = 0f;
    private float y = 0f;

    public void fitInRectangle(Rectangle rect, boolean side) {
        region = getKeyFrame(0f);
        float width = region.getRegionWidth();
        float height = region.getRegionHeight();
        if (width > height) {
            float scale = rect.width / width;
            if (side) {
                if (scale > 3.0f) scale = 3.0f;
            } else {
                if (scale > 2.2f) scale = 2.2f;
            }
            setScale(scale);
        } else {
            float scale = rect.height / height;
            if (side) {
                if (scale > 3.0f) scale = 3.0f;
            } else {
                if (scale > 2.2f) scale = 2.2f;
            }
            setScale(scale);
        }
        float maxOffX = 0;
        float maxOffY = 0;
        for (TextureRegion text : getKeyFrames()) {
            offX = ((TextureAtlas.AtlasRegion) text).offsetX * scale;
            offY = ((TextureAtlas.AtlasRegion) text).offsetY * scale;
            if (offX > maxOffX) maxOffX = offX;
            if (offY > maxOffY) maxOffY = offY;
        }

        width = width * scale;
        height = height * scale;

        float difference = rect.width - width;
        x = rect.x + (difference / 2f) - maxOffX / 2f;

        difference = rect.height - height;
        y = rect.y + (difference / 2f) - maxOffY / 2f;
    }
}