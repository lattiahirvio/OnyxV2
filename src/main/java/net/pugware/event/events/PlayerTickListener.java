package net.pugware.event.events;

import net.pugware.event.Event;
import net.pugware.event.Listener;

import java.util.ArrayList;

public interface PlayerTickListener extends Listener {
    void onPlayerTick();

    class PlayerTickEvent extends Event<PlayerTickListener> {

        @Override
        public void fire(ArrayList<PlayerTickListener> listeners) {
            listeners.forEach(PlayerTickListener::onPlayerTick);
        }

        @Override
        public Class<PlayerTickListener> getListenerType() {
            return PlayerTickListener.class;
        }
    }
}
