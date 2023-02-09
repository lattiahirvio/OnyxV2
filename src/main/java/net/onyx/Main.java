package net.onyx;

import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        try {
            Onyx.INSTANCE.init();
        } catch (Exception ignored) {

        }
    }
}