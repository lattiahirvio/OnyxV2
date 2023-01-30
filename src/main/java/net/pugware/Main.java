package net.pugware;

import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        try {
            Pugware.INSTANCE.init();
        } catch (Exception ignored) {

        }
    }
}