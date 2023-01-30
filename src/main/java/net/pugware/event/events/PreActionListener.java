package net.pugware.event.events;

import net.pugware.event.Event;
import net.pugware.event.Listener;

import java.util.ArrayList;

public interface PreActionListener extends Listener
{
    void onPreAction();

    class PreActionEvent extends Event<PreActionListener>
    {

        @Override
        public void fire(ArrayList<PreActionListener> listeners)
        {
            for (PreActionListener listener : listeners)
            {
                listener.onPreAction();
            }
        }

        @Override
        public Class<PreActionListener> getListenerType()
        {
            return PreActionListener.class;
        }
    }
}
