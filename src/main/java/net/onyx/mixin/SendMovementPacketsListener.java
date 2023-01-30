package net.onyx.mixin;

import net.onyx.event.CancellableEvent;
import net.onyx.event.Listener;

import java.util.ArrayList;

public interface SendMovementPacketsListener extends Listener
{
	void onSendMovementPackets(SendMovementPacketsEvent event);

	class SendMovementPacketsEvent extends CancellableEvent<SendMovementPacketsListener> implements net.onyx.mixin.SendMovementPacketsEvent {

		@Override
		public void fire(ArrayList<SendMovementPacketsListener> listeners)
		{
			for (SendMovementPacketsListener listener : listeners)
			{
				listener.onSendMovementPackets(this);
				if (isCancelled())
					return;
			}
		}


		@Override
		public Class<SendMovementPacketsListener> getListenerType()
		{
			return SendMovementPacketsListener.class;
		}
	}
}
