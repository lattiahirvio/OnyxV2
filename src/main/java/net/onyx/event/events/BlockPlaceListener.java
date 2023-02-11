package net.onyx.event.events;

import net.minecraft.block.BlockState;
import net.onyx.event.Event;
import net.onyx.event.Listener;

import java.util.ArrayList;

// Wack event system
public interface BlockPlaceListener extends Listener {
    void onBlockPlace(BlockState state);


    class BlockPlaceEvent extends Event<BlockPlaceListener> {

        private final BlockState state;

        public BlockPlaceEvent(BlockState state) {
            this.state = state;
        }

        @Override
        public void fire(ArrayList<BlockPlaceListener> listeners) {
            listeners.forEach(e -> e.onBlockPlace(state));
        }

        @Override
        public Class<BlockPlaceListener> getListenerType() {
            return BlockPlaceListener.class;
        }
    }
}
