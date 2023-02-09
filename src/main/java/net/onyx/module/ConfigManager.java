package net.onyx.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.onyx.core.CrystalDataTracker;
import net.onyx.core.PlayerActionScheduler;
import net.onyx.core.Rotator;
import net.onyx.event.EventManager;
import net.onyx.gui.ClickGui;
import net.onyx.keybind.KeybindManager;

public enum ConfigManager {
   INSTANCE;

   public static MinecraftClient MC;
   private EventManager eventManager;
   private ModuleManager moduleManager;
   private KeybindManager keybindManager;
   private ClickGui gui;
   private Boolean guiInitialized = false;
   private CrystalDataTracker crystalDataTracker;
   private PlayerActionScheduler playerActionScheduler;
   private Rotator rotator;
   private Session session;
   private String hwid;

   public void init() {
      MC = MinecraftClient.getInstance();
      this.eventManager = new EventManager();
      this.moduleManager = new ModuleManager();
      this.keybindManager = new KeybindManager();
      this.gui = new ClickGui();
      this.crystalDataTracker = new CrystalDataTracker();
      this.playerActionScheduler = new PlayerActionScheduler();
      this.rotator = new Rotator();
      this.session = MinecraftClient.getInstance().getSession();
   }

   public void panic() {
      this.guiInitialized = null;
      MC = null;
      this.eventManager = null;
      this.moduleManager = null;
      this.keybindManager = null;
      this.gui = null;
      this.crystalDataTracker = null;
      this.playerActionScheduler = null;
      this.rotator = null;
      this.session = null;
      this.hwid = null;
   }

   public EventManager getEventManager() {
      return this.eventManager;
   }

   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   public KeybindManager getKeybindManager() {
      return this.keybindManager;
   }

   public ClickGui getClickGui() {
      if (!this.guiInitialized) {
         this.gui.init();
         this.guiInitialized = true;
      }

      return this.gui;
   }

   public void updateClickGui() {
      this.gui = new ClickGui();
      if (!this.guiInitialized) {
         this.gui.init();
         this.guiInitialized = true;
      }

   }

   public CrystalDataTracker getCrystalDataTracker() {
      return this.crystalDataTracker;
   }

   public PlayerActionScheduler getPlayerActionScheduler() {
      return this.playerActionScheduler;
   }

   public Rotator getRotator() {
      return this.rotator;
   }

   public Session getSession() {
      return this.session;
   }

   public String getHwid() {
      return this.hwid;
   }

   // $FF: synthetic method
   private static ConfigManager[] $values() {
      return new ConfigManager[]{INSTANCE};
   }
}
