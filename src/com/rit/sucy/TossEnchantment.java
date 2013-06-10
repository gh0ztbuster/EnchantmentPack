package com.rit.sucy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Hashtable;

public class TossEnchantment extends CustomEnchantment{

    int max;
    long cooldownBase;
    long cooldownBonus;
    double speedBase;
    double speedBonus;

    // Cooldown timer for the ability
    Hashtable<String, Long> timers = new Hashtable<String, Long>();

    public TossEnchantment(JavaPlugin plugin) {
        super("Toss", plugin.getConfig().getStringList("Toss.items").toArray(new String[0]));
        max = plugin.getConfig().getInt("Toss.max");
        cooldownBonus = (long)(1000 * plugin.getConfig().getDouble("Toss.cooldownBonus"));
        cooldownBase = (long)(1000 * plugin.getConfig().getDouble("Toss.cooldownBase")) + cooldownBonus;
        speedBonus = plugin.getConfig().getDouble("Toss.speedBonus");
        speedBase = plugin.getConfig().getDouble("Toss.speedBase") - speedBonus;
    }

    @Override
    public void applyEntityEffect(Player player, int enchantLevel, PlayerInteractEntityEvent event) {
        if(event.getRightClicked() instanceof Player){
            Player enemy = (Player)event.getRightClicked();
            if(enemy != null){
                if (!timers.containsKey(player.getName())) timers.put(player.getName(), 0l);
                if (System.currentTimeMillis() - timers.get(player.getName()) < cooldownBase - cooldownBonus * enchantLevel) {
                    player.setPassenger(enemy);
                    enemy.sendMessage(ChatColor.GREEN+player.getDisplayName()+ChatColor.GRAY+" just picked you up with "+ChatColor.LIGHT_PURPLE + enchantName + " "+enchantLevel+ChatColor.GRAY+".");
                    timers.put(player.getName(), System.currentTimeMillis());
                }else{
                    player.sendMessage("You have " + ((int)((cooldownBase - cooldownBonus * enchantLevel) - (System.currentTimeMillis() - timers.get(player.getName()))) / 1000 + 1) + " seconds left.");
                }
            }
        }
    }
    @Override
    public void applyMiscEffect(Player player, int enchantLevel, PlayerInteractEvent event){
        Player p = event.getPlayer();
        if(p.getPassenger() !=null && p.getPassenger() instanceof Player){
            Player enemy = (Player)p.getPassenger();
            if(event.getAction() == Action.LEFT_CLICK_AIR){
                Vector vector = p.getEyeLocation().getDirection();
                p.eject();
                vector.multiply((speedBase + speedBonus * enchantLevel) / vector.length());
                enemy.setVelocity(vector);
                enemy.sendMessage(ChatColor.GREEN+p.getDisplayName()+ChatColor.GRAY+" just threw you with " + ChatColor.LIGHT_PURPLE+enchantName + " " + enchantLevel+ChatColor.GRAY + ".");
            }
        }
    }
    @Override
    public int getEnchantmentLevel(int expLevel) {
        // This allows for an enchantment level of up to 5. (49 / 10 + 1 = 5 when rounded down)
        return expLevel / 10 + 1;
    }
}