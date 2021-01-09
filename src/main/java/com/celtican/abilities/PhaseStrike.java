package com.celtican.abilities;

import com.celtican.BendingWeapons;
import com.celtican.stamina.StaminaEntity;
import com.celtican.utils.General;
import com.celtican.utils.ItemHandler;
import com.destroystokyo.paper.ParticleBuilder;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class PhaseStrike extends WaterAbility implements AddonAbility {

    private final static int TIME_TO_CHARGE = 20;

    private final ParticleBuilder pb;

    private boolean hasClickedThisTick = false; // so you can't left/right click twice in the same tick
    private final StaminaEntity staminaEntity;
    private final float distanceFromSource;
    private TempBlock tempBlock;
    private boolean isIce;
    private boolean controllable = true;
    private Phase phase;
    private int charging = TIME_TO_CHARGE;

    private PhaseStrike(Player player, float distanceFromSource, boolean isIce) {
        super(player);
        pb = new ParticleBuilder(Particle.FALLING_WATER).offset(0.4, 0.1, 0.4).count(1);
        staminaEntity = StaminaEntity.getStaminaEntity(player);

        this.distanceFromSource = distanceFromSource;
        this.isIce = isIce;
        makeTempBlock();
        player.getWorld().playSound(tempBlock.getLocation(), Sound.ITEM_BUCKET_FILL, 0.75f, 1);
        start();
    }
    private PhaseStrike(Player player, Block source) {
        this(player, (float) source.getLocation().toCenterLocation().distance(player.getLocation()), source.getType() == Material.ICE);
    }

    public static void strike(BendingPlayer player) {
        PhaseStrike move = null;
        for (PhaseStrike m : getAbilities(player.getPlayer(), PhaseStrike.class)) {
            if (m.controllable && !m.hasClickedThisTick && !m.isCharging() && m.staminaEntity.has(1)) {
                move = m;
                break;
            }
        }
        if (move == null) return;
        move.hasClickedThisTick = true;
        if (move.phase == null) move.phase = move.isIce ? move.new PhaseIce() : move.new PhaseWater();
        move.phase.strike();
    }
    public static void phase(BendingPlayer player) {
        PhaseStrike move = null;
        for (PhaseStrike m : getAbilities(player.getPlayer(), PhaseStrike.class)) {
            if (m.controllable && m.phase == null && !m.hasClickedThisTick && !m.isCharging()) {
                move = m;
                break;
            }
        }
        if (move == null || move.tempBlock == null) return;
        move.hasClickedThisTick = true;
        move.isIce = !move.isIce;
        move.player.getWorld().playSound(move.tempBlock.getLocation(), move.isIce ? Sound.BLOCK_GLASS_BREAK : Sound.ITEM_BUCKET_FILL, move.isIce ? 1 : 0.75f, 1);
        move.tempBlock.setType(move.isIce ? Material.ICE : Material.WATER);
    }
    public static void create(BendingPlayer player) {
        if (ItemHandler.getType(player.getPlayer().getInventory().getItemInMainHand()) != ItemHandler.ItemType.SWORD) return; // requires sword in hand
        if (!StaminaEntity.getStaminaEntity(player).has(1)) return;
        Block block = General.getWaterSource(player); // fetch source block in 5 blocks
        boolean fromBottle = false;
        if (block == null) {
            if (General.expendWaterBottle(player)) fromBottle = true;
            else return;
        }
        if (!player.canBend(getAbility("PhaseStrike"))) return;
        if (fromBottle) new PhaseStrike(player.getPlayer(), 3, false);
        else new PhaseStrike(player.getPlayer(), block);
    }

    public void makeTempBlock() {
        Block block = player.getLocation().getWorld().getBlockAt(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(distanceFromSource)));
        if (!block.getLocation().equals(this.getLocation())) {
            // create new tempblock
            if (!(block.getType() == Material.WATER || block.getType() == Material.ICE || !block.getType().isSolid())) {
                remove();
                return;
            }
            if (tempBlock != null) tempBlock.revertBlock();
            if (isIce) tempBlock = new TempBlock(block, Material.ICE);
            else if (isCharging()) tempBlock = new TempBlock(block, GeneralMethods.getWaterData(charging*8/TIME_TO_CHARGE));
            else tempBlock = new TempBlock(block, isIce ? Material.ICE : Material.WATER);
        } else if (isCharging()) {
            tempBlock.setType(GeneralMethods.getWaterData((charging*8)/(TIME_TO_CHARGE)));
        }
        if (!isIce) {
            pb.location(block.getLocation().toCenterLocation()).spawn();
        }
        if (!isIce && Bukkit.getCurrentTick() % 15 == 0) player.getWorld().playSound(block.getLocation(), Sound.BLOCK_WATER_AMBIENT, 1, 0.8f);
    }

    public boolean isCharging() {
        return charging > 0;
    }

    @Override public void progress() {
        if (hasClickedThisTick) hasClickedThisTick = false;
        if (isCharging()) charging--;
        if (!bPlayer.canBendIgnoreCooldowns(this) || !player.isSneaking()) {
            remove();
            return;
        }
        if (controllable) {
            makeTempBlock();
            staminaEntity.affect();
        }
        if (phase != null) phase.progress();
    }
    @Override public void remove() {
        super.remove();
        if (tempBlock != null) tempBlock.revertBlock();
        if (phase != null) phase.remove();
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
    @Override public String getName() {
        return "PhaseStrike";
    }
    @Override public Location getLocation() {
        return tempBlock != null ? tempBlock.getLocation() : null;
    }
    @Override public void load() {

    }
    @Override public void stop() {

    }
    @Override public String getAuthor() {
        return BendingWeapons.AUTHOR;
    }
    @Override public String getVersion() {
        return BendingWeapons.version;
    }
    @Override public String getDescription() {
        return "§3§nSword-Based Move.\n" +
               "§3Hold Shift:§b Grab water source.\n" +
               "§3Right-Click:§b Phase the source between water and ice.\n" +
               "§3Left-Click with Water:§b Blast a single water stream that inflicts slowness.\n" +
               "§3Left-Click with Ice:§b Shoot multiple ice bullets that inflict weakness.";
    }

    private interface Phase {
        void strike();
        void progress();
        void remove();
    }

    private class PhaseWater implements Phase {

        private Location loc;
        private float distanceCovered = 0;

        private int range = 15;
        private float speed = 0.6f;

        private TempBlock head;
        private TempBlock trail1;
        private TempBlock trail2;

        PhaseWater() {
            loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(distanceFromSource));
            controllable = false;
            tempBlock.revertBlock();
            staminaEntity.affect(1);
            bPlayer.addCooldown(PhaseStrike.this, (long) (1000 / ItemHandler.getAttackSpeed(player.getPlayer())));
        }

        @Override public void strike() {}
        @Override public void progress() {

            LivingEntity target = GeneralMethods.getClosestLivingEntity(loc, 1);
            if (target != null && target != player) {
                GeneralMethods.setVelocity(target, player.getEyeLocation().getDirection().multiply(0.3f));
                DamageHandler.damageEntity(target, ItemHandler.getDamage(player)/2, PhaseStrike.this);
                StaminaEntity.getStaminaEntity(target).affect(1);
                PhaseStrike.this.remove();
                pb.particle(Particle.WATER_SPLASH).count(15).location(target.getEyeLocation()).offset(0.5, 0.5, 0.5).spawn();
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
                player.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1, 1);
                return;
            }

            distanceCovered += speed;
            if (distanceCovered >= range) {
                PhaseStrike.this.remove();
                return;
            }

            loc.add(player.getEyeLocation().getDirection().multiply(speed));

            Block block = player.getWorld().getBlockAt(loc);
            if (head == null || !head.getBlock().equals(loc.getBlock())) {
                if (trail2 != null) trail2.revertBlock();
                trail2 = trail1; if (trail2 != null) trail2.setType(GeneralMethods.getWaterData(3));
                trail1 = head;   if (trail1 != null) trail1.setType(GeneralMethods.getWaterData(5));
                head = new TempBlock(block, Material.WATER);
            }
            pb.location(loc).spawn();

            if (Bukkit.getCurrentTick() % 3 == 0) player.getWorld().playSound(block.getLocation(), Sound.BLOCK_WATER_AMBIENT, 1, 2);
        }
        @Override public void remove() {
            if (head   != null) head  .revertBlock();
            if (trail1 != null) trail1.revertBlock();
            if (trail2 != null) trail2.revertBlock();
        }
    }

    private class PhaseIce implements Phase {

        private final static int DURATION_OF_ICE_BULLET = 100; // 5 second

        private final int duration;
        private int d = 0;
        private final ArrayList<ItemTime> items = new ArrayList<>(10);
        private boolean takenStamina = false;

        PhaseIce() {
            duration = (int)(20 * ItemHandler.getAttackSpeed(player)); // inverse of attack speed. faster attack speed = longer duration
        }

        @Override public void strike() {
            player.getWorld().playSound(player.getEyeLocation(), Sound.BLOCK_GLASS_BREAK, 1, 0.8f);
            for (int i = 0; i < 2; i++){
                Item item = player.getWorld().dropItem(tempBlock.getLocation().toCenterLocation().add(player.getEyeLocation().getDirection()), new ItemStack(Material.ICE));
                item.setPickupDelay(Integer.MAX_VALUE); // can't be picked up
                item.setVelocity(player.getEyeLocation().getDirection().add(General.getRandomVector(0.25f * (i+1))).add(new Vector(0, 0.1f, 0))); // 0.25*(i+1) allows one bullet to have a wider shotgun effect than the other

                // insert item into blank slot in items, otherwise append
                boolean append = true;
                for (int j = 0; j < items.size(); j++) {
                    if (items.get(j) == null) {
                        items.set(j, new ItemTime(item, d, j));
                        append = false;
                        break;
                    }
                }
                if (append) items.add(new ItemTime(item, d, items.size()));
            }
        }
        @Override public void progress() {
            if (++d == duration) {
                controllable = false;
                tempBlock.revertBlock();
                if (!takenStamina) {
                    staminaEntity.affect(1);
                    takenStamina = true;
                    bPlayer.addCooldown(PhaseStrike.this, (long) (1000 / ItemHandler.getAttackSpeed(player.getPlayer())));
                }
            } else if (d > duration) {
                if (items.isEmpty()) PhaseStrike.this.remove();
            }
            items.removeIf(itemTime -> {
                if (d-itemTime.time >= DURATION_OF_ICE_BULLET || itemTime.item.isOnGround()) {
                    itemTime.item.remove();
                    return true;
                } else {
                    LivingEntity target = GeneralMethods.getClosestLivingEntity(itemTime.item.getLocation(), 0.5);
                    if (target != null && target != player) {
                        GeneralMethods.setVelocity(target, itemTime.item.getVelocity().multiply(0.25f));
                        DamageHandler.damageEntity(target, ItemHandler.getDamage(player)/4, PhaseStrike.this);
                        itemTime.item.remove();
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0));
                        player.getWorld().playSound(target.getEyeLocation(), Sound.BLOCK_GLASS_BREAK, 1, 0.8f);
                        return true;
                    }
                }
                return false;
            });
        }
        @Override public void remove() {
            for (ItemTime item : items) item.item.remove();
            items.clear(); // just to be safe, I don't want mem leaks
            if (!takenStamina) {
                staminaEntity.affect(1);
                takenStamina = true;
                bPlayer.addCooldown(PhaseStrike.this, (long) (1000 / ItemHandler.getAttackSpeed(player.getPlayer())));
            }
        }

        private class ItemTime {
            final Item item;
            final int time;
            final int i;

            ItemTime(Item item, int time, int i) {
                this.item = item;
                this.time = time;
                this.i = i;
            }
        }
    }
}
