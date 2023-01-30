package net.onyx.event.events;

import net.onyx.event.Event;
import net.onyx.event.Listener;

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
