package com.celtican.stamina;

import com.projectkorra.projectkorra.BendingPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StaminaPlayer extends StaminaEntity {

    public final Player player;
    public final BendingPlayer bPlayer;

    protected StaminaPlayer(UUID uuid) {
        super(uuid);
        player = Bukkit.getPlayer(uuid);
        bPlayer = BendingPlayer.getBendingPlayer(player);
    }

    @Override public boolean isPlayer() {
        return true;
    }

    @Override protected void localRun() {
        super.localRun();
        StringBuilder message = new StringBuilder("§2[ ");

        int numPipes = 5;

        for (int curStamina = 0; curStamina < maxStamina; curStamina++) {
            if (stamina >= curStamina + 1) {
                message.append("§a||||| ");
                continue;
            } else if (stamina <= curStamina) {
                message.append("§8||||| ");
                continue;
            }

            int numPipesColored = (int) (numPipes * (stamina%1));
            message.append("§a");
            int pipe;
            for (pipe = 0; pipe < numPipesColored; pipe++) message.append("|");
            message.append("§8");
            for (; pipe < numPipes; pipe++) message.append("|");
            message.append(" ");
        }

        message.append("§2]");

        player.sendActionBar(message.toString());
    }

}
