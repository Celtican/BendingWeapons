package com.celtican.utils;

import com.celtican.BendingWeapons;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class ItemHandler {

    public static ItemType getType(Material material) {
        switch (material) {
            case WOODEN_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case GOLDEN_SWORD:
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
                return ItemType.SWORD;
            case WOODEN_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case GOLDEN_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:
                return ItemType.AXE;
            case BOW:
                return ItemType.BOW;
            case WOODEN_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case GOLDEN_PICKAXE:
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:
            case WOODEN_SHOVEL:
            case STONE_SHOVEL:
            case IRON_SHOVEL:
            case GOLDEN_SHOVEL:
            case DIAMOND_SHOVEL:
            case NETHERITE_SHOVEL:
            case WOODEN_HOE:
            case STONE_HOE:
            case IRON_HOE:
            case GOLDEN_HOE:
            case DIAMOND_HOE:
            case NETHERITE_HOE:
                return ItemType.TOOL;
            case AIR:
                return ItemType.NONE;
            default:
                return ItemType.OTHER;
        }
    }
    private static float getBaseDamage(ItemStack item) {
        switch (item.getType()) {
            // swords
            case WOODEN_SWORD:
            case GOLDEN_SWORD: return 4;
            case STONE_SWORD: return 5;
            case IRON_SWORD: return 6;
            case DIAMOND_SWORD: return 7;
            case NETHERITE_SWORD: return 8;

            // shovels
            case WOODEN_SHOVEL:
            case GOLDEN_SHOVEL: return 2.5f;
            case STONE_SHOVEL: return 3.5f;
            case IRON_SHOVEL: return 4.5f;
            case DIAMOND_SHOVEL: return 5.5f;
            case NETHERITE_SHOVEL: return 6.5f;

            // pickaxes
            case WOODEN_PICKAXE:
            case GOLDEN_PICKAXE: return 2;
            case STONE_PICKAXE: return 3;
            case IRON_PICKAXE: return 4;
            case DIAMOND_PICKAXE: return 5;
            case NETHERITE_PICKAXE: return 6;

            // axes
            case WOODEN_AXE:
            case GOLDEN_AXE: return 7;
            case STONE_AXE:
            case IRON_AXE:
            case DIAMOND_AXE: return 9;
            case NETHERITE_AXE: return 10;

            // misc
            case TRIDENT: return 9;
            default: return 1;
        }
    }
    private static float getBaseSpeed(ItemStack item) {
        switch (item.getType()) {
            case WOODEN_SWORD: case GOLDEN_SWORD: case STONE_SWORD: case IRON_SWORD: case DIAMOND_SWORD: case NETHERITE_SWORD: return 1.6f;
            case TRIDENT: return 1.1f;
            case WOODEN_AXE:
            case STONE_AXE: return 0.8f;
            case IRON_AXE: return 0.9f;
            case GOLDEN_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:
            case WOODEN_SHOVEL: case GOLDEN_SHOVEL: case STONE_SHOVEL: case IRON_SHOVEL: case DIAMOND_SHOVEL: case NETHERITE_SHOVEL: return 1;
            case WOODEN_PICKAXE: case GOLDEN_PICKAXE: case STONE_PICKAXE: case IRON_PICKAXE: case DIAMOND_PICKAXE: case NETHERITE_PICKAXE: return 1.2f;
            case WOODEN_HOE: case GOLDEN_HOE: return 1;
            case STONE_HOE: return 2;
            case IRON_HOE: return 3;
            case DIAMOND_HOE: case NETHERITE_HOE: default: return 4;
        }
    }

    public static ItemType getType(ItemStack item) {
        return getType(item.getType());
    }

    public static float getDamage(Player player) {
        return getDamage(player, player.getInventory().getItemInMainHand());
    }
    public static float getDamage(Player player, ItemStack item) {
        float damage = getBaseDamage(item);

        if (BendingWeapons.useAttributes) {
//            Collection<AttributeModifier> modifiers = item.getItemMeta().getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE);
//            if (modifiers != null) for (AttributeModifier modifier : modifiers) {
//
//            }
        }

        return damage;
    }

    public static float getAttackSpeed(Player player) {
        return getAttackSpeed(player, player.getInventory().getItemInMainHand());
    }
    public static float getAttackSpeed(Player player, ItemStack item) {
        float speed = getBaseSpeed(item);

        if (BendingWeapons.useAttributes) {
            // etc.
        }

        return speed;
    }
    public static int getAttackSpeedInTicks(Player player, ItemStack item) {
        return Math.round(20 / getAttackSpeed(player.getPlayer(), item));
    }
    public static int getAttackSpeedInverseInTicks(Player player, ItemStack item) {
        return Math.round(getAttackSpeed(player, item) * 20);
    }

    public enum ItemType {
        SWORD, AXE, BOW, TOOL, NONE, OTHER
    }

}
