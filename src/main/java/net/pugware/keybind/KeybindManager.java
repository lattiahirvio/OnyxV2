package net.pugware.keybind;

import net.pugware.Pugware;
import net.pugware.event.events.KeyPressListener;
import net.pugware.gui.GuiScreen;
import net.pugware.module.modules.hud.ClickGuiSettings;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static net.pugware.Pugware.MC;

public class KeybindManager implements KeyPressListener {

    private final ArrayList<Keybind> keybinds = new ArrayList<>();

    public KeybindManager() {
        Pugware.INSTANCE.getEventManager().add(KeyPressListener.class, this);
        addDefaultKeybinds();
    }

    public ArrayList<Keybind> getAllKeybinds() {
        return (ArrayList<Keybind>) keybinds.clone();
    }

    public void removeAll() {
        keybinds.clear();
        addDefaultKeybinds();
    }

    public void addKeybind(Keybind keybind) {
        keybinds.add(keybind);
    }

    public void removeKeybind(Keybind keybind) {
        keybinds.remove(keybind);
    }

    public void removeKeybind(String name) {
        keybinds.removeIf(e -> e.getName().equals(name));
    }

    @Override
    public void onKeyPress(KeyPressListener.KeyPressEvent event) {
        for (Keybind keybind : keybinds) {
            if (event.getKeyCode() == keybind.getKey()) {
                if (event.getAction() == GLFW.GLFW_PRESS)
                    keybind.press();
                if (event.getAction() == GLFW.GLFW_RELEASE)
                    keybind.release();
            }
        }
        //event.cancel();
    }

    private void addDefaultKeybinds() {
        Keybind guiBind = new Keybind("insert-gui", GLFW.GLFW_KEY_RIGHT_ALT, true, false, () ->
        {
            if (MC.currentScreen != null)
                return;
            MC.setScreen(new GuiScreen());
        });
        addKeybind(guiBind);
        ClickGuiSettings.class.cast(Pugware.INSTANCE.getModuleManager().getModule(ClickGuiSettings.class)).activateKey.set(guiBind);
    }
}
