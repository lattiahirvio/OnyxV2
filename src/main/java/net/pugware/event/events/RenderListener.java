package net.pugware.event.events;

import net.minecraft.client.util.math.MatrixStack;
import net.pugware.event.Event;
import net.pugware.event.Listener;

import java.util.ArrayList;

public interface RenderListener extends Listener {
    void onRender(RenderEvent event);

    class RenderEvent extends Event<RenderListener> {

        private final MatrixStack matrixStack;
        private final float partialTicks;

        public RenderEvent(MatrixStack matrixStack, float partialTicks) {
            this.matrixStack = matrixStack;
            this.partialTicks = partialTicks;
        }

        public MatrixStack getMatrixStack() {
            return matrixStack;
        }

        public float getPartialTicks() {
            return partialTicks;
        }

        @Override
        public void fire(ArrayList<RenderListener> listeners) {
            for (RenderListener listener : listeners) {
                listener.onRender(this);
            }
        }

        @Override
        public Class<RenderListener> getListenerType() {
            return RenderListener.class;
        }
    }
}
