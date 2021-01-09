package com.celtican.abilities;

import com.celtican.BendingWeapons;
import com.celtican.stamina.StaminaEntity;
import com.celtican.utils.ItemHandler;
import com.destroystokyo.paper.ParticleBuilder;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CinderSlash extends FireAbility implements AddonAbility {

    private final static float DURATION = 5;
    private final static float SPEED = 0.5f; // duration x speed = radius in blocks of move
    private final static float RANGE = 3.5f;
    private final static float RADIUS = 1.5f;
    private final static float EXPLODE_RADIUS = 1.5f;
    private final static float EXPLODE_VELOCITY = 1;

    private final ParticleBuilder pb;
    private final Location loc;
    private int time = 0;

    public CinderSlash(Player player) {
        super(player);

        loc = GeneralMethods.getTargetedLocation(player, RANGE, false, true);
        loc.setY(player.getLocation().getY());
        loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 1, 1);
        loc.getWorld().spawnParticle(Particle.LAVA, loc, 15, 0.5f, 0.1f, 0.5f);
        loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 10, 1f, 0.1f, 1f, 0);
        pb = new ParticleBuilder(Particle.FLAME).count(1).extra(0.02);

        StaminaEntity.getStaminaEntity(player).affect(1);
        bPlayer.addCooldown(this, ItemHandler.getAttackSpeedInTicks(player, player.getInventory().getItemInMainHand())*50L);

        List<Entity> entities = GeneralMethods.getEntitiesAroundPoint(loc, RADIUS);
        for (Entity entity : entities) {
            if (entity == player.getPlayer()) continue;
            if (entity instanceof LivingEntity && !entity.isDead()) {
                loc.getWorld().playSound(((LivingEntity) entity).getEyeLocation(), Sound.ENTITY_PLAYER_HURT_ON_FIRE, SoundCategory.PLAYERS, 1, 1.2f);
            }
            entity.setVelocity(entity.getLocation().subtract(loc.clone()).toVector().normalize().multiply(0.5f));
            DamageHandler.damageEntity(entity, player.getPlayer(), ItemHandler.getDamage(player.getPlayer()), this);
            int ticksToBeOnFire = ItemHandler.getAttackSpeedInverseInTicks(player.getPlayer(), player.getInventory().getItemInMainHand());
            entity.setFireTicks(entity.getFireTicks() + ticksToBeOnFire);
        }

        start();
    }

    public static void leftClick(BendingPlayer player) {
        if (!player.getPlayer().isSneaking()) return;
        if (ItemHandler.getType(player.getPlayer().getInventory().getItemInMainHand()) != ItemHandler.ItemType.SWORD) return;
        if (!player.canBend(CoreAbility.getAbility("CinderSlash"))) return;
        Material type = player.getPlayer().getEyeLocation().getBlock().getType();
        if (type == Material.WATER || type.isSolid()) return;
        if (!StaminaEntity.getStaminaEntity(player).has(1)) return;

        new CinderSlash(player.getPlayer());
    }
    public static void rightClick(BendingPlayer player) {
        if (!player.getPlayer().isSneaking()) return;
        if (ItemHandler.getType(player.getPlayer().getInventory().getItemInMainHand()) != ItemHandler.ItemType.SWORD) return;
        if (!player.canBend(CoreAbility.getAbility("CinderSlash"))) return;
        Material type = player.getPlayer().getEyeLocation().getBlock().getType();
        if (type == Material.WATER || type.isSolid()) return;
        if (!StaminaEntity.getStaminaEntity(player).has(1)) return;

        Location loc = GeneralMethods.getTargetedLocation(player.getPlayer(), RANGE, false, true);
        List<Entity> entities = GeneralMethods.getEntitiesAroundPoint(loc, EXPLODE_RADIUS);
        for (Entity entity : entities) {
            if (entity == player.getPlayer()) continue;
            if (entity instanceof LivingEntity && !entity.isDead()) {
                loc.getWorld().playSound(((LivingEntity) entity).getEyeLocation(), Sound.ENTITY_PLAYER_HURT_ON_FIRE, SoundCategory.PLAYERS, 1, 1.2f);
            }
            entity.setVelocity(entity.getLocation().subtract(loc.clone()).toVector().normalize().multiply(EXPLODE_VELOCITY/2));
            DamageHandler.damageEntity(entity, player.getPlayer(), ItemHandler.getDamage(player.getPlayer())/2, getAbility("CinderSlash"));
            int ticksToBeOnFire = ItemHandler.getAttackSpeedInverseInTicks(player.getPlayer(), player.getPlayer().getInventory().getItemInMainHand()) * 2;
            entity.setFireTicks(entity.getFireTicks() + ticksToBeOnFire);
        }

        loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 1, 1);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2, 1.5f);
        loc.add(0, 0.25f, 0);
        loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 30, 1.25f, 0, 1.25f, 0.05f);
        loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 20, 0.25f, 0.25f, 0.25f, 0.01f);
        loc.getWorld().spawnParticle(Particle.LAVA, loc, 15, 0.5f, 0.1f, 0.5f);

        player.getPlayer().setVelocity(player.getPlayer().getEyeLocation().getDirection().multiply(-1 * EXPLODE_VELOCITY));
        player.addCooldown("CinderSlash", 600000); // ten minutes. I would put Integer.MAX_VALUE, but I'm setting a limit just in case something happens
        StaminaEntity.getStaminaEntity(player).affect(1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(BendingWeapons.main, () -> explodingPlayers.add(player),
                ItemHandler.getAttackSpeedInTicks(player.getPlayer(), player.getPlayer().getInventory().getItemInMainHand()));
    }

    @Override public void progress() {
        float r = SPEED * time;
        float circ = (float) (r * Math.PI * 2);

        for (float i = 0; i <= circ; i += 0.8f) {
            float theta = (float) ((i/circ) * Math.PI * 2);
            float x = (float) (loc.getX() + r*Math.cos(theta));
            float z = (float) (loc.getZ() + r*Math.sin(theta));
            pb.location(loc.getWorld(), x, loc.getY() + 0.1, z).spawn();
        }

        loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1, 1.5f);

        if (++time == DURATION) remove();
    }

    @Override public boolean isSneakAbility() {
        return true;
    }
    @Override public boolean isHarmlessAbility() {
        return false;
    }
    @Override public long getCooldown() {
        return 0;
    }
    @Override public Location getLocation() {
        return loc;
    }

    @Override public void load() {

    }
    @Override public void stop() {}
    @Override public String getName() {
        return "CinderSlash";
    }
    @Override public String getAuthor() {
        return BendingWeapons.AUTHOR;
    }
    @Override public String getVersion() {
        return BendingWeapons.version;
    }
    @Override public String getDescription() {
        return "§4§nSword-Based Move.\n" +
               "§4Shift-Left-Click:§c Scatter cinders in front of you and set creatures alight for a short time.\n" +
               "§4Shift-Right-Click:§c Launch yourself back and set creatures in front of you alight for a moderate time.\n" +
               "§cDuration of fire stacks.";
    }

    private static final ArrayList<BendingPlayer> explodingPlayers = new ArrayList<>();
    public static void staticProgress() {
        for (int i = 0; i < explodingPlayers.size(); i++)
            if (!explodingPlayers.get(i).getPlayer().isOnline() || explodingPlayers.get(i).getPlayer().isOnGround()) // .isOnGround is the easiest solution, I may improve on this later
                explodingPlayers.remove(i--).removeCooldown("CinderSlash");
    }
}
