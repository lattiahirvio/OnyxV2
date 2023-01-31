package net.onyx.module.modules.misc;

import net.onyx.event.events.ItemUseListener;
import net.onyx.module.Category;
import net.onyx.module.Module;

public class NameProtect extends Module {
    private static String replacementUsername = "LilNotMaster";
    private static boolean isEnabledStatic = false;


    public NameProtect() {
        super("NameProtect", "Hides your name in chat.", false, Category.MISC);
    }

    @Override
    public void onEnable() {
        isEnabledStatic = true;
    }

    @Override
    public void onDisable() {
        isEnabledStatic = false;
    }


    public static int setReplacementUsername(String newReplacementUsername) {
        replacementUsername = newReplacementUsername;
        return 0;
    }

    public static String replaceName(String string) {
        if (string != null && isEnabledStatic) {
            return string.replace(mc.getSession().getUsername(), replacementUsername);
        }
        return string;
    }
}

