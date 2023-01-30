package net.pugware.event.events;

import net.pugware.event.CancellableEvent;
import net.pugware.event.Listener;
import net.minecraft.network.Packet;

import java.util.ArrayList;

public interface PacketOutputListener extends Listener
{
    void onSendPacket(PacketOutputEvent event);

    class PacketOutputEvent extends CancellableEvent<PacketOutputListener>
    {
        private Packet<?> packet;

        public PacketOutputEvent(Packet<?> packet)
        {
            this.packet = packet;
        }

        public Packet<?> getPacket()
        {
            return packet;
        }

        @Override
        public void fire(ArrayList<PacketOutputListener> listeners)
        {
            for (PacketOutputListener listener : listeners)
            {
                listener.onSendPacket(this);
                if (isCancelled())
                    return;
            }
        }

        @Override
        public Class<PacketOutputListener> getListenerType()
        {
            return PacketOutputListener.class;
        }

    }

}
