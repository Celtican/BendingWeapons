package com.celtican;

import com.celtican.abilities.AxeBlast;
import com.projectkorra.projectkorra.BendingPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class AbilityListener implements Listener {

    @EventHandler public void onPlayerLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (bPlayer == null) return;

        switch (bPlayer.getBoundAbilityName()) {
            case "AxeBlast":
                AxeBlast.create(bPlayer, event);
                break;
        }
    }

}
