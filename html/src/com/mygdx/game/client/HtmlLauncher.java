package com.mygdx.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.preloader.Preloader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.mygdx.game.Bridge;
import com.mygdx.game.battlewindow.ContinuousGameFrame;
import com.mygdx.game.battlewindow.Event;
import com.mygdx.game.battlewindow.Events;

public class HtmlLauncher extends GwtApplication {

    public GwtApplicationConfiguration getConfig () {
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(600, 360);
        config.preferFlash = false;
        //Assets.init(getPreloaderBaseURL());
        return config;
    }

    @Override
    public ApplicationListener getApplicationListener () {
        return new ContinuousGameFrame(new HtmlBridge());
    }


    public static class HtmlBridge implements Bridge {
        @Override
        public TextureAtlas getAtlas(String path) {
            return Assets.getAtlas(path);
        }

        @Override
        public Texture getTexture(String path) {
            return Assets.getTexture(path);
        }

        @Override
        public BitmapFont getFont(String path) {
            return Assets.getFont(path);
        }

        private ContinuousGameFrame game;

        @Override
        public void setGame(ContinuousGameFrame game) {
            this.game = game;
            try {
                setCallBacks();
            } catch (Exception e) {
                Window.alert("can't find battle");
            }
        }

        @Override
        public void log(String text) {
            Logger.println(text);
        }

        @Override
        public void pause() {
            pausebattle();
        }

        @Override
        public void unpause() {
            unpausebattle();
        }

        private native void pausebattle() /*-{
            $wnd.battle.pause();
        }-*/;

        private native void unpausebattle() /*-{
            $wnd.battle.unpause();
        }-*/;

        @Override
        public void alert(String message) {
            //Window.alert("Alert from: " + side);
            if (message.equals("true")) {
                //game.HUDs[0].updatePoke(JavaScriptPokemon.fromJS(getPoke(0, 1)));
                Window.alert(getPoke(1, 0));
            } else if (message.equals("false")){
                //game.HUDs[1].updatePoke(JavaScriptPokemon.fromJS(getPoke(1, 1)));
                Window.alert(getPoke(0, 0));
                //addEvent(new Events.LogEvent(getBattle()));
            } else {
                //Window.alert(message);
            }
        }

        @Override
        public void finished() {
            Logger.println("finished");
            int randomNum = Random.nextInt(37);
            HtmlEvents.DelayedEvent event = new HtmlEvents.DelayedEvent(new Events.BackgroundChange(randomNum), this, 2);
            addEvent(event);
            unpausebattle();
            ///final HtmlBridge that = this;
            Timer t = new Timer() {
                @Override
                public void run() {
                    init();
                    unpausebattle();
                }
            };

            t.schedule(2000);
        }

        private void init() {
            // Window finished loading, let's send information
            JavaScriptPokemon me = JavaScriptPokemon.fromJS(getPoke(1, 0));
            JavaScriptPokemon opp = JavaScriptPokemon.fromJS(getPoke(0, 0));
            Logger.println("My Pokemon " + getPoke(1, 0));
            Logger.println("Opp Pokemon " + getPoke(0, 0));
            game.HUDs[0].updatePoke(me);
            game.HUDs[1].updatePoke(opp);
            if (me.num() > 0) {
                HtmlEvents.DelayedEvent eventme = new HtmlEvents.DelayedEvent(new Events.SpriteChange(me, true), this, 1);
                addEvent(eventme);
            }
            if (opp.num() > 0) {
                HtmlEvents.DelayedEvent eventopp = new HtmlEvents.DelayedEvent(new Events.SpriteChange(opp, false), this, 1);
                addEvent(eventopp);
            }
            //addEvent(new Events.SpriteChange(true, me.num() + ".atlas", false));
            //addEvent(new Events.SpriteChange(false, opp.num() + ".atlas", false));
        }

        @Override
        public void addEvent(Event event) {
            game.service.offer(event);
        }

        /*
        public void newEvent(int args) {
            String s = "";
            if (args == 1) {
                s = "Send Out";
            }
            if (args == 2) {
                s = "Send Back";
            }
            //Event event = new Events.LogEvent(s);
            //addEvent(event);
        }
        */

        public void dealWithSendOut(int player) {
            Logger.println("sendout");
            JavaScriptPokemon poke = JavaScriptPokemon.fromJS(getPoke(player, 0));
            boolean side = player == 1;
            Event event = new Events.HUDChange(poke, side);
            addEvent(event);
            HtmlEvents.DelayedEvent event1 = new HtmlEvents.DelayedEvent(new Events.SpriteChange(poke, side), this, 1);
            addEvent(event1);
        }

        public void dealWithSendBack(int player) {
            Logger.println("sendback");
            pausebattle();
            //Event event = new Events.SendBack((byte) player);
            //addEvent(event);
            Timer t = new Timer() {
                @Override
                public void run() {
                    unpausebattle();
                }
            };
            t.schedule(500);
        }

        public void dealWithStatusChange(int player, int status) {
            Logger.println("statuschange");
            Event event = new Events.StatusChange(player == 1, status);
            addEvent(event);
        }

        public void dealWithHpChange(int player, int change) {
            Logger.println("hpchange");
            pausebattle();
            int duration = change;
            if (duration < 0) duration = -duration;
            if (duration > 100) duration = 100;
            Event event = new HtmlEvents.AnimatedHPEvent((byte) change, player == 1, duration * 30, this);
            addEvent(event);
        }

        public void dealWithKo(int player) {
            Logger.println("KO");
            pausebattle();
            Event event = new Events.KO(player == 1);
            addEvent(event);
            Timer t = new Timer() {
                @Override
                public void run() {
                    unpausebattle();
                }
            };
            t.schedule(800);
        }

        private native void setCallBacks() /*-{
        var that = this;
        $wnd.battle.on("sendout", function(player) {
            that.@com.mygdx.game.client.HtmlLauncher.HtmlBridge::dealWithSendOut(I)(player);
        });
        $wnd.battle.on("sendback", function(player) {
            that.@com.mygdx.game.client.HtmlLauncher.HtmlBridge::dealWithSendBack(I)(player);
        });
        $wnd.battle.on("ko", function(player) {
            that.@com.mygdx.game.client.HtmlLauncher.HtmlBridge::dealWithKo(I)(player);
        });
        $wnd.battle.on("statuschange", function(player, status) {
            that.@com.mygdx.game.client.HtmlLauncher.HtmlBridge::dealWithStatusChange(II)(player, status);
        });
        $wnd.battle.on("hpchange", function(player, change) {
            that.@com.mygdx.game.client.HtmlLauncher.HtmlBridge::dealWithHpChange(II)(player, change);
        });
        }-*/;

        private static native int battleId() /*-{
        return $wnd.battleId;
        }-*/;

        private static native String getPoke(int side, int slot) /*-{
        var poke = $wnd.battle.teams[side][slot];
        return JSON.stringify(poke);
        }-*/;

        private static native String getBattle() /*-{
            var battle = $wnd.battle;
            return JSON.stringify(battle);
        }-*/;

        private static native String getTeam(int side) /*-{
            var team = $wnd.battle.teams[side];
            return JSON.stringify(team);
        }-*/;


    }


    @Override
    public Preloader.PreloaderCallback getPreloaderCallback() {
        return new Preloader.PreloaderCallback() {
            @Override
            public void update(Preloader.PreloaderState state) {
                // like update(stateTime) but update(stat.getProgess())
            }

            @Override
            public void error(String file) {
                System.out.println("error: " + file);
            }
        };
    }



    /*
    public LoadingListener getLoadingListener() {
        return new LoadingListener() {
            @Override
            public void beforeSetup() {
                // Do something!
            }

            @Override
            public void afterSetup() {
                // Do something!
            }
        };
    }
    */


    @Override
    public String getPreloaderBaseURL() {
        return GWT.getHostPageBaseURL() + "public/battle/";
    }
}