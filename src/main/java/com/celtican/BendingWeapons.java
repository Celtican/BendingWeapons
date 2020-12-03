package com.celtican;

import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BendingWeapons extends JavaPlugin {

    public static String AUTHOR = "Celtican";
    public static String VERSION = "0.1";

    @Override public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new AbilityListener(), this);
        CoreAbility.registerPluginAbilities(this, "com.celtican.abilities");
    }

    @Override public void onDisable() {

    }

}
