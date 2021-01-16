package com.celtican.mythicmobs;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMobsHandler implements Listener {

    @EventHandler public void onMechanic(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("stamina")) {
            event.register(new StaminaMechanic(event.getConfig()));
        }
    }

}
