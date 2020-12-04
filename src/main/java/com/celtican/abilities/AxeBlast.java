package com.celtican.abilities;

import com.celtican.BendingWeapons;
import com.celtican.utils.ItemHandler;
import com.celtican.utils.TempFallingBlock;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class AxeBlast extends EarthAbility implements AddonAbility {

    private TempFallingBlock block;
    private boolean isBlasting = false;

    public AxeBlast(Player player) {
        this(player, null);
    }
    public AxeBlast(Player player, Block block) {
        super(player);

        if (!bPlayer.canBend(this)) return;

        if (!block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0)).isEmpty()) return;

//        new TempBlock(block, Material.AIR.createBlockData(), 1000);
        this.block = new TempFallingBlock(block.getLocation().toCenterLocation(), new Vector(0, 0.4, 0), block.getBlockData(), 1000);
        new TempBlock(block, Material.AIR.createBlockData(), 5000);


        Location loc = player.getLocation();
        World world = player.getWorld();
        world.playSound(loc, Sound.BLOCK_ANCIENT_DEBRIS_BREAK, 1, 0.5f);
        start();
    }

    public static void create(BendingPlayer player, PlayerInteractEvent event) {
        if (ItemHandler.getType(player.getPlayer()) != ItemHandler.ItemType.AXE) return;

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                if (!isEarthbendable(player.getPlayer(), "AxeBlast", event.getClickedBlock())) return;
                new AxeBlast(player.getPlayer(), event.getClickedBlock());
                break;
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                boolean wasAbilityFound = false;

                for (AxeBlast axeBlast : getAbilities(player.getPlayer(), AxeBlast.class)) {
                    if (axeBlast.isBlasting) continue;
                    if (axeBlast.getLocation().distance(player.getPlayer().getEyeLocation()) <= 5 &&
                            axeBlast.blast(player.getPlayer().getEyeLocation().getDirection())) {
                        wasAbilityFound = true;
                    }
                }


                if (wasAbilityFound) {
                    player.addCooldown("AxeBlast", (long) (1000 / ItemHandler.getAttackSpeed(player.getPlayer())));
                    Location loc = player.getPlayer().getLocation();
                    World world = player.getPlayer().getWorld();
                    world.playSound(loc, Sound.ENTITY_GHAST_SHOOT, 1, 2.0f);
                    world.playSound(loc, Sound.BLOCK_ANCIENT_DEBRIS_BREAK, 1, 2.0f);
                }
                break;
        }
    }

    @Override public void progress() {
        if (block.fallingBlock.isDead()) remove();
    }

    private boolean blast(Vector vector) {
        if (isBlasting) return false;
        isBlasting = true;
        block.fallingBlock.setVelocity(vector.normalize().multiply(2));
        block.delay = 2000;
        return true;
    }

    @Override public boolean isSneakAbility() {
        return false;
    }
    @Override public boolean isHarmlessAbility() {
        return false;
    }
    @Override public long getCooldown() {
        return 1000;
    }
    @Override public String getName() {
        return "AxeBlast";
    }
    @Override public Location getLocation() {
        return block != null ? block.fallingBlock.getLocation() : player.getLocation();
    }
    @Override public String getAuthor() {
        return BendingWeapons.AUTHOR;
    }
    @Override public String getVersion() {
        return BendingWeapons.VERSION;
    }
    @Override public String getDescription() {
        return "[WIP] Right click with axe to raise earth, left click raised earth to blast. Damage scales with axe.";
    }
    @Override public void load() {

    }
    @Override public void stop() {

    }
}
