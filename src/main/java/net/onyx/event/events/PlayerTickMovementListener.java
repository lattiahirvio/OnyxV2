package net.onyx.event.events;

import net.onyx.event.CancellableEvent;
import net.onyx.event.Listener;

import java.util.ArrayList;

public interface PlayerTickMovementListener extends Listener
{
	void onPlayerTickMovement(PlayerTickMovementEvent event);

	class PlayerTickMovementEvent extends CancellableEvent<PlayerTickMovementListener>
	{
		@Override
		public void fire(ArrayList<PlayerTickMovementListener> listeners)
		{
			for (PlayerTickMovementListener listener : listeners)
			{
				listener.onPlayerTickMovement(this);
				if (isCancelled())
					return;
			}
		}

		@Override
		public Class<PlayerTickMovementListener> getListenerType()
		{
			return PlayerTickMovementListener.class;
		}
	}
}
