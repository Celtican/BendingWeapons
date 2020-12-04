package com.celtican.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.concurrent.ConcurrentHashMap;

public class TempFallingBlock {

    /*

    Inspired by JedCore's TempFallingBlock class

     */

    public static ConcurrentHashMap<FallingBlock, TempFallingBlock> blocks = new ConcurrentHashMap<>();

    public static void run() {
        long time = System.currentTimeMillis();
        for (TempFallingBlock block : blocks.values()) if (time >= block.creationTime + block.delay) block.remove();
    }
    public static void remove(FallingBlock fallingBlock) {
        if (blocks.containsKey(fallingBlock)) blocks.get(fallingBlock).remove();
    }


    public final FallingBlock fallingBlock;
    public final long creationTime;
    public long delay;

    public TempFallingBlock(Location location, Vector velocity, Material material, long delay) {
        this(location, velocity, material.createBlockData(), delay);
    }
    public TempFallingBlock(Location location, Vector velocity, BlockData blockData, long delay) {
        this.fallingBlock = location.getWorld().spawnFallingBlock(location, blockData);
        this.creationTime = System.currentTimeMillis();
        this.delay = delay;
        fallingBlock.setVelocity(velocity);
        fallingBlock.setDropItem(false);
        blocks.put(fallingBlock, this);
    }

    public void remove() {
        fallingBlock.remove();
        blocks.remove(fallingBlock);
    }

}
