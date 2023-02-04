/*LilNotMaster Aproves!*/
package net.onyx.module.modules.combat;

import net.onyx.module.Category;
import net.onyx.module.Module;

public class CrystalPingBypass extends Module {
    public static boolean removeCrystal;

    public CrystalPingBypass() {
        super("CrystalPingBypass", "Makes Crystals not ping based", false, Category.COMBAT);
    }

    public void onEnable() {
        super.onEnable();
        removeCrystal = true;
    }

    public void onDisable() {
        super.onDisable();
        removeCrystal = false;
    }


}
