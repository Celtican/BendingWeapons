package com.celtican.stamina;

import com.projectkorra.projectkorra.BendingPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Hashtable;
import java.util.UUID;

public class StaminaEntity {

    ////////////////
    //   Static   //
    ////////////////

    private static final int TICKS_TO_DESTROY = 100; // 5 seconds after filling up, StaminaEntity will remove itself to free up space. Note that it

    private static final Hashtable<UUID, StaminaEntity> entities = new Hashtable<>();
    // I think Hashtable is better at multi-threading than HashMap. Otherwise they are effectively the same

    public static StaminaEntity getStaminaEntity(BendingPlayer bPlayer) {
        return getStaminaEntity(bPlayer.getUUID());
    }
    public static StaminaEntity getStaminaEntity(LivingEntity entity) {
        return getStaminaEntity(entity.getUniqueId());
    } // only living things can have stamina
    private static StaminaEntity getStaminaEntity(UUID uuid) {
        // if there's already a StaminaEntity for that uuid, return it, otherwise make one
        StaminaEntity e = entities.get(uuid);
        if (e != null) return e;
        return Bukkit.getPlayer(uuid) != null ? new StaminaPlayer(uuid) : new StaminaEntity(uuid);
    } // private so outside class can't add nonliving things

    public static void run() {
        for (StaminaEntity entity : entities.values()) entity.localRun();
    }

    ////////////////////
    //   Non-Static   //
    ////////////////////

    public final UUID uuid;
    public final LivingEntity entity;

    protected float maxStamina = 3;
    protected float stamina = maxStamina;
    protected float regenRate = maxStamina/60; // three seconds to fill
    protected float regenDelay = 30; // 1.5 seconds to start filling
    protected int lastUpdated = Bukkit.getCurrentTick();

    protected StaminaEntity(UUID uuid) {
        this.uuid = uuid;
        entity = (LivingEntity) Bukkit.getEntity(uuid);

        entities.put(uuid, this);
    }

    protected void localRun() {

        int curTick = Bukkit.getCurrentTick();

        if (getStamina() == getMaxStamina()) {
            if (lastUpdated + TICKS_TO_DESTROY <= curTick) entities.remove(uuid);
            return;
        }

        if (lastUpdated + getRegenDelay() >= curTick) return;
        stamina += getRegenRate();
        if (getStamina() >= getMaxStamina()) {
            stamina = getMaxStamina();
            affect();
        }
    }

    public void affect() {
        lastUpdated = Bukkit.getCurrentTick();
    }
    public void affect(float damage) {
        affect();
        stamina -= damage;
        if (getStamina() < 0) stamina = 0;
        else if (getStamina() > getMaxStamina()) stamina = getMaxStamina();
    }

    public float getStamina() {
        return stamina;
    }
    public float getMaxStamina() {
        return maxStamina;
    }
    public float getRegenRate() {
        float regenRate = this.regenRate;

        PotionEffect potion = entity.getPotionEffect(PotionEffectType.SPEED);
        if (potion != null) regenRate *= 1.2f * (potion.getAmplifier() + 1);
        potion = entity.getPotionEffect(PotionEffectType.SLOW);
        if (potion != null) regenRate *= Math.pow(0.75f, potion.getAmplifier()+1);

        return regenRate;
    }
    public float getRegenDelay() {
        float regenDelay = this.regenDelay;

        PotionEffect potion = entity.getPotionEffect(PotionEffectType.SPEED);
//        if (potion != null) regenDelay *= 0.75f * (potion.getAmplifier() + 1);
        if (potion != null) regenDelay *= Math.pow(0.75f, potion.getAmplifier()+1);
        potion = entity.getPotionEffect(PotionEffectType.SLOW);
        if (potion != null) regenDelay *= 1.2f * potion.getAmplifier()+1;

        return regenDelay;
    }
    public float getStaminaPercent() {
        return getStamina()/getMaxStamina();
    }
    public boolean has(float stamina) {
        return getStamina() >= stamina;
    }

    public boolean isPlayer() {
        return false;
    }
}
