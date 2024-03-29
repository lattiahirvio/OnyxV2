package net.onyx.util;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import net.onyx.Onyx;
import net.onyx.module.modules.misc.NameProtect;
import org.apache.logging.log4j.LogManager;

import static net.onyx.Onyx.mc;

public enum ChatUtils {
    ;
    private static final String prefix = "§f[§9onyx§f] ";

    public static void log(String message) {
        LogManager.getLogger().info("[Onyx] {}", message.replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    public static void info(String message) {
        String string = prefix + "Info: " + message;
        sendPlainMessage(string);
    }

    public static void error(String message) {
        String string = prefix + "§4Error: §f" + message;
        sendPlainMessage(string);
    }

    public static void sendPlainMessage(String message) {
        InGameHud hud = mc.inGameHud;
        if (hud != null)
            hud.getChatHud().addMessage(Text.literal(message));
    }

    public static void plainMessageWithPrefix(String message) {
        String string = prefix + message;
        sendPlainMessage(string);
    }

    public static String replaceName(String string) {
        if (string != null && Onyx.INSTANCE.getModuleManager().getModule(NameProtect.class).isEnabled()) {
            return string.replace(mc.getSession().getUsername(), "Player");
        }
        return string;
    }
}
