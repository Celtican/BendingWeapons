package com.celtican;

import com.celtican.abilities.CinderSlash;
import com.celtican.stamina.StaminaEntity;
import com.celtican.utils.TempFallingBlock;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class BendingWeapons extends JavaPlugin implements Runnable {

    public static String AUTHOR = "Celtican";
    public static String VERSION = "0.1";

    public static boolean useAttributes = true;

    public static BendingWeapons main;
    public static Random random;

    @Override public void onEnable() {
        main = this;
        random = new Random();
        getServer().getPluginManager().registerEvents(new AbilityListener(), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, this, 0, 1);
        CoreAbility.registerPluginAbilities(this, "com.celtican.abilities");
    }

    @Override public void onDisable() {

    }

    @Override public void run() {
        TempFallingBlock.run();
        StaminaEntity.run();
        CinderSlash.staticProgress();
    }

}
