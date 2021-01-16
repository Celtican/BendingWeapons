package com.celtican.mythicmobs;

import com.celtican.stamina.StaminaEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.entity.LivingEntity;

public class StaminaMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private final float amount; // amount of stamina to remove
    private final boolean force; // false, stamina will not be removed if the player has not used a move recently

    public StaminaMechanic(MythicLineConfig config) {
        super(config.getLine(), config);
        setAsyncSafe(false);

        amount = config.getFloat("amount", 0);
        force = config.getBoolean("force", false);
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (!abstractEntity.isLiving()) return false;

        LivingEntity target = (LivingEntity) BukkitAdapter.adapt(abstractEntity);

        StaminaEntity se = StaminaEntity.getStaminaEntity(target, force);
        if (se != null)
            if (amount == 0) se.affect();
            else se.affect(1);

        return true;
    }
}
