package net.onyx.event.events;

import net.onyx.event.CancellableEvent;
import net.onyx.event.Listener;

import java.util.ArrayList;

public interface PlayerJumpListener extends Listener
{
	void onPlayerJump(PlayerJumpEvent event);

	class PlayerJumpEvent extends CancellableEvent<PlayerJumpListener>
	{

		@Override
		public void fire(ArrayList<PlayerJumpListener> listeners)
		{
			for (PlayerJumpListener listener : listeners)
			{
				listener.onPlayerJump(this);
				if (isCancelled())
					return;
			}
		}

		@Override
		public Class<PlayerJumpListener> getListenerType()
		{
			return PlayerJumpListener.class;
		}
	}
}
