package net.onyx;

import net.minecraft.client.MinecraftClient;
import net.onyx.command.CommandParser;
import net.onyx.core.CrystalDataTracker;
import net.onyx.core.PlayerActionScheduler;
import net.onyx.core.Rotator;
import net.onyx.event.EventManager;
import net.onyx.gui.ClickGui;
import net.onyx.keybind.KeybindManager;
import net.onyx.module.ModuleManager;

import java.awt.*;

public enum Onyx {

    INSTANCE;

    public static MinecraftClient mc;

    private EventManager eventManager;
    private ModuleManager moduleManager;
    private CommandParser commandParser;
    private KeybindManager keybindManager;
    private ClickGui gui;
    private boolean guiInitialized = false;
    private CrystalDataTracker crystalDataTracker;
    private PlayerActionScheduler playerActionScheduler;
    private Rotator rotator;
    private Robot robot;


    public void init() {
        mc = MinecraftClient.getInstance();
        eventManager = new EventManager();
        moduleManager = new ModuleManager();
        commandParser = new CommandParser();
        keybindManager = new KeybindManager();
        gui = new ClickGui();
        crystalDataTracker = new CrystalDataTracker();
        playerActionScheduler = new PlayerActionScheduler();
        rotator = new Rotator();
        System.setProperty("java.awt.headless", "false");
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandParser getCommandParser() {
        return commandParser;
    }

    public KeybindManager getKeybindManager() {
        return keybindManager;
    }

    public ClickGui getClickGui() {
        if (!guiInitialized) {
            gui.init();
            guiInitialized = true;
        }
        return gui;
    }

    public CrystalDataTracker getCrystalDataTracker() {
        return crystalDataTracker;
    }

    public PlayerActionScheduler getPlayerActionScheduler() {
        return playerActionScheduler;
    }

    public Rotator getRotator() {
        return rotator;
    }

    public Robot getRobot() {
        return robot;
    }

    public void onDestruct() {
    }
}
