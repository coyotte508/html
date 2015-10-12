package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.battlewindow.ContinuousGameFrame;
import com.mygdx.game.battlewindow.Event;

public interface Bridge {
    public void pause();
    public void unpause();
    public void finished();
    public void setGame(ContinuousGameFrame game);
    public void alert(String message);
    public TextureAtlas getAtlas(String path);
    public Texture getTexture(String path);
    public BitmapFont getFont(String path);
    public void addEvent(Event event);
    public void log(String text);
}