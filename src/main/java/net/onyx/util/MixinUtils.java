package net.onyx.util;

import net.onyx.event.CancellableEvent;
import net.onyx.event.Event;
import net.onyx.event.EventManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public enum MixinUtils {
    ;

    public static void fireEvent(Event<?> event) {
        EventManager.fire(event);
    }

    public static void fireCancellable(CancellableEvent<?> event, CallbackInfo ci) {
        EventManager.fire(event);
        if (event.isCancelled() && ci.isCancellable())
            ci.cancel();
    }
}
