package com.sucy.trap.enchant;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Updates traps every other tick
 */
public class TrapTask extends BukkitRunnable {

    /**
     * Updates all traps
     */
    public void run() {
        Trap[] traps = Trap.traps.values().toArray(new Trap[Trap.traps.values().size()]);
        for (Trap trap : traps) {
            for (LivingEntity entity : trap.center.getWorld().getLivingEntities()) {
                if (trap.contains(entity.getLocation())) trap.addEntity(entity);
                else trap.removeEntity(entity);
            }
            trap.update();
        }
    }
}
