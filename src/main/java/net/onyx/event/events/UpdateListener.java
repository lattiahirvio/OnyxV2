package net.onyx.event.events;

import net.onyx.event.Event;
import net.onyx.event.Listener;

import java.util.ArrayList;

public interface UpdateListener extends Listener {
    void onUpdate();

    class UpdateEvent extends Event<UpdateListener> {

        @Override
        public void fire(ArrayList<UpdateListener> listeners) {
            for (UpdateListener listener : listeners) {
                listener.onUpdate();
            }
        }

        @Override
        public Class<UpdateListener> getListenerType() {
            return UpdateListener.class;
        }
    }
}
