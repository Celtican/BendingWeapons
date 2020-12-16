package com.celtican.stamina;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StaminaPlayer extends StaminaEntity {

    public final Player player;

    protected StaminaPlayer(UUID uuid) {
        super(uuid);
        player = Bukkit.getPlayer(uuid);
    }

    @Override public boolean isPlayer() {
        return true;
    }

    @Override protected void localRun() {
        super.localRun();
        StringBuilder message = new StringBuilder("§2[ ");

        int numPipes = 5;
        int numCharacters = (int) (maxStamina*numPipes + maxStamina-1);
        int numLit = (int) (numCharacters * stamina / maxStamina);

        for (int i = 0; i < numCharacters; i++) {
            message.append(i < numLit ? "§a" : "§8").append((i+1) % (numPipes+1) == 0 ? ':' : '|');
        }

        message.append(" §2]");

        player.sendActionBar(message.toString());
    }

}
