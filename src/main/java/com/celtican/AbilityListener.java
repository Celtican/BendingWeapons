package com.celtican;

import com.celtican.abilities.AxeBlast;
import com.celtican.utils.ItemHandler;
import com.celtican.utils.TempFallingBlock;
import com.projectkorra.projectkorra.BendingPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class AbilityListener implements Listener {

    @EventHandler public void onPlayerLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (bPlayer == null) return;

        switch (bPlayer.getBoundAbilityName()) {
            case "AxeBlast":
                if (ItemHandler.getType(player) != ItemHandler.ItemType.AXE) return;
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        AxeBlast.create(bPlayer, event.getClickedBlock());
                        break;
                    case LEFT_CLICK_AIR:
                    case LEFT_CLICK_BLOCK:
                        AxeBlast.blast(bPlayer);
                        break;
                }
                break;
        }
    }

    @EventHandler public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (bPlayer == null) return;

        switch (bPlayer.getBoundAbilityName()) {
            case "AxeBlast":
                if (ItemHandler.getType(player) != ItemHandler.ItemType.AXE) return;
                AxeBlast.create(bPlayer, player.getTargetBlock(5));
                break;
        }
    }

    @EventHandler public void onBlockFall(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            FallingBlock block = (FallingBlock)event.getEntity();
            if (TempFallingBlock.blocks.containsKey(block)) {
                TempFallingBlock.blocks.get(block).remove();
                event.setCancelled(true);
            }
        }
    }

}
