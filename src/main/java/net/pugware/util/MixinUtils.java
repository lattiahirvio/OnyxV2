package net.pugware.util;

import net.pugware.event.CancellableEvent;
import net.pugware.event.Event;
import net.pugware.event.EventManager;
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
