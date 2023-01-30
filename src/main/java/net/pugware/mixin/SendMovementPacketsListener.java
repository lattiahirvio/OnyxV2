package net.pugware.mixin;

import net.pugware.event.CancellableEvent;
import net.pugware.event.Listener;

import java.util.ArrayList;

public interface SendMovementPacketsListener extends Listener
{
	void onSendMovementPackets(SendMovementPacketsEvent event);

	class SendMovementPacketsEvent extends CancellableEvent<SendMovementPacketsListener> implements net.pugware.mixin.SendMovementPacketsEvent {

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
