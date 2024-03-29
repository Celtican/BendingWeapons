package com.celtican.abilities;

import com.celtican.BendingWeapons;
import com.celtican.stamina.StaminaEntity;
import com.celtican.utils.ItemHandler;
import com.celtican.utils.TempFallingBlock;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AxeBlast extends EarthAbility implements AddonAbility {

    private TempBlock sourceBlock;
    private TempFallingBlock block;
    private StaminaEntity e;
    private boolean isBlasting = false;

    public AxeBlast(Player player, Block block) {
        super(player);

        if (!bPlayer.canBend(this)) return;

        if (!block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0)).isEmpty()) return;

        Runnable r = () -> {
            AxeBlast.this.block = new TempFallingBlock(block.getLocation().toCenterLocation(), new Vector(0, 0.4, 0), block.getBlockData(), 900);
            AxeBlast.this.sourceBlock = new TempBlock(block, Material.AIR.createBlockData());
        };
        Bukkit.getScheduler().scheduleSyncDelayedTask(BendingWeapons.main, r);
        e = StaminaEntity.getStaminaEntity(player);
        e.affect();

        Location loc = player.getLocation();
        World world = player.getWorld();
        world.playSound(loc, Sound.BLOCK_ANCIENT_DEBRIS_BREAK, 1, 0.5f);
        start();
    }

    public static void blast(BendingPlayer player) {
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
    }
    public static void create(BendingPlayer player, Block block) {
        if (!isEarthbendable(player.getPlayer(), "AxeBlast", block)) return;
        new AxeBlast(player.getPlayer(), block);
    }

    @Override public void progress() {
        if (block == null) return; // waiting 1 tick to initialize
        if (block.fallingBlock.isDead()) {
            remove();
            return;
        }
        if (!isBlasting) return;
        Entity entity = GeneralMethods.getClosestLivingEntity(getLocation(), 1);
        if (entity != null) {
            if (entity.getUniqueId() == player.getUniqueId()) return;
            GeneralMethods.setVelocity(entity, block.fallingBlock.getVelocity().multiply(0.3f));
            DamageHandler.damageEntity(entity, ItemHandler.getDamage(player)/2, this);
            block.remove();
        }
    }

    private boolean blast(Vector vector) {
        if (isBlasting) return false;
        if (!e.has(1)) return false;
        isBlasting = true;
        Vector v = new Vector();
        v.setX((BendingWeapons.random.nextFloat()-0.5f)*0.1f);
        v.setY((BendingWeapons.random.nextFloat()-0.5f)*0.1f);
        v.setZ((BendingWeapons.random.nextFloat()-0.5f)*0.1f);
        block.fallingBlock.setVelocity(vector.normalize().multiply(2).add(v));
        block.delay = 2000;
        e.affect(1);
        return true;
    }

    @Override public void remove() {
        super.remove();
        if (block != null) block.remove();
        if (sourceBlock != null) sourceBlock.revertBlock();
    }
    @Override public boolean isSneakAbility() {
        return true;
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
        return BendingWeapons.version;
    }
    @Override public String getDescription() {
        return "§2§nAxe-Based Move\n" +
               "§a§oOutdated. Will be replaced soon.\n" +
               "§2Right-Click/Shift:§a Raise target earth block.\n" +
               "§2Left-Click:§a Launch raised earth blocks.";
    }
    @Override public void load() {

    }
    @Override public void stop() {

    }
}
