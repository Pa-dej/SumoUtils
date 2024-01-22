package me.padej.sumoutils;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SumoUtilsEvents implements Listener {

    public SumoUtilsEvents(SumoUtils sumoUtils) {
    }

    @EventHandler
    private void onPlayerDead(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        player.setGravity(true);

        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.NETHERITE_HELMET) {
            player.getInventory().setHelmet(null);
        }

        if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.NETHERITE_CHESTPLATE) {
            player.getInventory().setChestplate(null);
        }

        if (player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getType() == Material.NETHERITE_LEGGINGS) {
            player.getInventory().setLeggings(null);
        }

        if (player.getInventory().getBoots() != null && player.getInventory().getBoots().getType() == Material.NETHERITE_BOOTS) {
            player.getInventory().setBoots(null);
        }

        // Забираем меч из инвентаря игрока, если он есть
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.NETHERITE_SWORD) {
                player.getInventory().removeItem(item);
                break;
            }
        }

        // Проверяем левую руку
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem != null && offHandItem.getType() == Material.NETHERITE_SWORD) {
            player.getInventory().setItemInOffHand(null);
        }

        // Проверяем слоты крафта
        InventoryView craftingInventory = player.getOpenInventory();
        for (int i = 1; i <= 4; i++) {
            ItemStack craftingItem = craftingInventory.getItem(i);
            if (craftingItem != null && craftingItem.getType() == Material.NETHERITE_SWORD) {
                craftingInventory.setItem(i, null);
            }
        }

        // Забираем меч из инвентаря игрока, если он есть
        for (int i = 0; i < 27; i++) {  // Перебираем слоты с 0 по 26 включительно
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.NETHERITE_SWORD) {
                player.getInventory().removeItem(item);
                break;
            }
        }

        // Проверяем удерживаемый предмет
        ItemStack cursorItem = player.getItemOnCursor();
        if (cursorItem.getType() == Material.NETHERITE_SWORD) {
            player.setItemOnCursor(null);
        }

        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation().add(0, 1, 0), 1);
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.setGravity(true);

        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.NETHERITE_HELMET) {
            player.getInventory().setHelmet(null);
        }

        if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.NETHERITE_CHESTPLATE) {
            player.getInventory().setChestplate(null);
        }

        if (player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getType() == Material.NETHERITE_LEGGINGS) {
            player.getInventory().setLeggings(null);
        }

        if (player.getInventory().getBoots() != null && player.getInventory().getBoots().getType() == Material.NETHERITE_BOOTS) {
            player.getInventory().setBoots(null);
        }

        // Забираем меч из инвентаря игрока, если он есть
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.NETHERITE_SWORD) {
                player.getInventory().removeItem(item);
                break;
            }
        }

        // Проверяем левую руку
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem != null && offHandItem.getType() == Material.NETHERITE_SWORD) {
            player.getInventory().setItemInOffHand(null);
        }

        // Проверяем слоты крафта
        InventoryView craftingInventory = player.getOpenInventory();
        for (int i = 1; i <= 4; i++) {
            ItemStack craftingItem = craftingInventory.getItem(i);
            if (craftingItem != null && craftingItem.getType() == Material.NETHERITE_SWORD) {
                craftingInventory.setItem(i, null);
            }
        }

        // Забираем меч из инвентаря игрока, если он есть
        for (int i = 0; i < 27; i++) {  // Перебираем слоты с 0 по 26 включительно
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.NETHERITE_SWORD) {
                player.getInventory().removeItem(item);
                break;
            }
        }

        // Проверяем удерживаемый предмет
        ItemStack cursorItem = player.getItemOnCursor();
        if (cursorItem.getType() == Material.NETHERITE_SWORD) {
            player.setItemOnCursor(null);
        }

        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation().add(0, 1, 0), 1);
    }
}
