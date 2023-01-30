package net.onyx.keybind;

import net.onyx.onyx;
import net.onyx.event.events.KeyPressListener;
import net.onyx.gui.GuiScreen;
import net.onyx.module.modules.hud.ClickGuiSettings;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static net.onyx.onyx.mc;

public class KeybindManager implements KeyPressListener {

    private final ArrayList<Keybind> keybinds = new ArrayList<>();

    public KeybindManager() {
        onyx.INSTANCE.getEventManager().add(KeyPressListener.class, this);
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
            if (mc.currentScreen != null)
                return;
            mc.setScreen(new GuiScreen());
        });
        addKeybind(guiBind);
        ClickGuiSettings.class.cast(onyx.INSTANCE.getModuleManager().getModule(ClickGuiSettings.class)).activateKey.set(guiBind);
    }
}
