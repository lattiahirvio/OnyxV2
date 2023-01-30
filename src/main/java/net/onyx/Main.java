package net.onyx;

import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        try {
            onyx.INSTANCE.init();
        } catch (Exception ignored) {

        }
    }
}