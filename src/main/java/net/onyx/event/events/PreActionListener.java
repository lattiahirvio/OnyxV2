package net.onyx.event.events;

import net.onyx.event.Event;
import net.onyx.event.Listener;

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
