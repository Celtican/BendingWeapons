package com.celtican.utils;

import com.celtican.BendingWeapons;
import com.destroystokyo.paper.block.TargetBlockInfo;
import com.projectkorra.projectkorra.BendingPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

public class General {

    public static Block getWaterSource (BendingPlayer player, int distance) {
        Block block = player.getPlayer().getTargetBlock(distance, TargetBlockInfo.FluidMode.ALWAYS);
        if (block == null) return null;
        if (block.getType() == Material.WATER || block.getType() == Material.ICE) return block;
        else return null;
    }
    public static Block getWaterSource(BendingPlayer player) {
        return getWaterSource(player, 5);
    }

    public static boolean expendWaterBottle(BendingPlayer player) {
        PlayerInventory inv = player.getPlayer().getInventory();
        for (ItemStack item : inv) {
            if (item != null && item.getType() == Material.POTION && item.hasItemMeta()) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                if (meta.getBasePotionData().getType().equals(PotionType.WATER)) {
                    item.setType(Material.GLASS_BOTTLE);
                    return true;
                }
            }
        }
        return false;
    }

    public static Vector getRandomVector(float variance) {
        Vector v = new Vector();

        v.setX((BendingWeapons.random.nextFloat()-0.5f)*variance);
        v.setY((BendingWeapons.random.nextFloat()-0.5f)*variance);
        v.setZ((BendingWeapons.random.nextFloat()-0.5f)*variance);

        return v;
    }
}
