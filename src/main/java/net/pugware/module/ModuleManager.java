package net.pugware.module;

import net.pugware.module.modules.combat.*;
import net.pugware.module.modules.hud.ClickGuiSettings;
import net.pugware.module.modules.hud.SelfDestruct;
import net.pugware.module.modules.hud.SkliggaVersionText;
import net.pugware.module.modules.misc.*;
import net.pugware.module.modules.render.UpsideDownPlayers;
import net.pugware.module.modules.render.nameTagPing;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class ModuleManager {
    private final HashMap<Class<? extends Module>, Module> modulesByClass = new HashMap<>();
    private final HashMap<String, Module> modulesByName = new HashMap<>();
    private final HashSet<Module> modules = new HashSet<>();

    public ModuleManager() {
        addModules();
    }

    public ArrayList<Module> getModules() {
        ArrayList<Module> arrayList = new ArrayList<>(modules);
        arrayList.sort(Comparator.comparing(Module::getName));
        return arrayList;
    }

    public Module getModule(Class<? extends Module> clazz) {
        return modulesByClass.get(clazz);
    }

    public Module getModuleByName(String name) {
        return modulesByName.get(name);
    }

    private void addModules() {
        //addModule(ModuleName.class);
        addModule(NoLoadingScreen.class);
        addModule(AutoDoubleHand.class);
        addModule(AutoInventoryTotem.class);
        addModule(AutoLootYeeter.class);
        addModule(AutoRekit.class);
        addModule(CwCrystal.class);
        addModule(MarlowCrystal.class);
        addModule(TriggerBot.class);
        addModule(AutoMedalClip.class);
        addModule(MiddleClickPing.class);
        addModule(nameTagPing.class);
        addModule(UpsideDownPlayers.class);
        addModule(AnchorMacro.class);
        addModule(NameProtect.class);
        addModule(AutoXp.class);
        addModule(AA.class);
        addModule(ALY.class);
        addModule(APR.class);
        addModule(Fakeplayer.class);
        addModule(ClickGuiSettings.class);
        addModule(AIT.class);
        addModule(ADH.class);
        addModule(Puggers.class);
        addModule(TB.class);
        addModule(SkliggaVersionText.class);
        addModule(CwCrystal2.class);
        addModule(AntiLookUpdateFeature.class);
        addModule(AutoDtap.class);
        addModule(SelfDestruct.class);
        addModule(AutoHeadBob.class);
        addModule(FastBreak.class);
        addModule(PingSpoof.class);
        addModule(AutoWTap.class);
        addModule(AutoTotem.class);
        addModule(MarlowAnchor.class);
    }

    private void addModule(Class<? extends Module> clazz) {
        try {
            Module module = clazz.getConstructor().newInstance();
            modulesByClass.put(clazz, module);
            modulesByName.put(module.getName(), module);
            modules.add(module);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public int getNumberOfCategory(Category cat){
        int count=0;
        for(Module module : getModules()){
            if(module.getCategory()==cat){
                count++;
            }
        }
        return count;
    }
}

