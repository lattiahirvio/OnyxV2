package net.onyx.module;

import net.minecraft.client.MinecraftClient;
import net.onyx.Onyx;
import net.onyx.event.EventManager;
import net.onyx.module.setting.KeybindSetting;
import net.onyx.module.setting.Setting;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Module implements Serializable {

    public final static MinecraftClient mc = MinecraftClient.getInstance();
    protected static EventManager eventManager = Onyx.INSTANCE.getEventManager();

    private final String name;
    private final String description;
    private boolean enabled;
    private final ArrayList<Setting<?>> settings = new ArrayList<>();
    private final Category category;

    public Module(String name, String description, boolean enabled, Category category) {
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.category = category;
        if (enabled)
            onEnable();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled)
                onEnable();
            else
                onDisable();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void onEnable() {
        for (Setting<?> setting : settings) {
            if (setting instanceof KeybindSetting keybindSetting) {
                Onyx.INSTANCE.getKeybindManager().addKeybind(keybindSetting.get());
            }
        }
    }

    public void onDisable() {
        for (Setting<?> setting : settings) {
            if (setting instanceof KeybindSetting keybindSetting) {
                Onyx.INSTANCE.getKeybindManager().removeKeybind(keybindSetting.get());
            }
        }
    }

    public void addSetting(Setting<?> setting) {
        settings.add(setting);
    }

    public ArrayList<Setting<?>> getSettings() {
        return (ArrayList<Setting<?>>) settings.clone();
    }

}
