package net.pugware.gui.window;

import net.pugware.gui.ClickGui;
import net.pugware.gui.component.Component;
import net.pugware.gui.component.ModuleButtonComponent;
import net.pugware.module.Module;
import net.pugware.module.setting.Setting;

public class ModuleSettingWindow extends Window {

    private final Module module;
    private final ModuleButtonComponent moduleButton;

    public ModuleSettingWindow(ClickGui parent, double x, double y, Module module, ModuleButtonComponent moduleButton) {
        super(parent, x, y, 150, 200);
        super.closable = true;
        super.minimizable = false;
        super.setTitle(module.getName());
        this.module = module;
        this.moduleButton = moduleButton;
        y = 40;
        for (Setting<?> setting : module.getSettings()) {
            Component component = setting.makeComponent(this);
            if (component != null) {
                component.setX(20);
                component.setY(y);
                addComponent(component);
                y += component.getLength() + 20.0;
            }
        }
    }

    @Override
    public void onClose() {
        moduleButton.settingWindowClosed();
    }

}
