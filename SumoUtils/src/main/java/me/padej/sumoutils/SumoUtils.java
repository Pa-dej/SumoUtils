package me.padej.sumoutils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class SumoUtils extends JavaPlugin implements Listener {
    private final Map<Player, BambooTimer> playerTimers = new HashMap<>();
    private boolean isEnabled = true;
    private FileConfiguration statsConfig;
    private File statsFile;
    private StatisticsManager statsManager;
    private SumoUtilsCommand commandExecutor;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        // Регистрация слушателя
        getServer().getPluginManager().registerEvents(new SumoUtilsEvents(this), this);

        // Инициализация файла статистики
        statsFile = new File(getDataFolder(), "Statistics.yml");
        statsConfig = YamlConfiguration.loadConfiguration(statsFile);

        // Регистрация команды
        commandExecutor = new SumoUtilsCommand(SumoUtils.this, statsConfig);
        getCommand("sumoutils").setExecutor(commandExecutor);

        statsManager = new StatisticsManager(getDataFolder());

        if (!statsFile.exists()) {
            try {
                statsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        // Отменяем все задачи при выключении плагина
        Bukkit.getScheduler().cancelTasks(this);
    }

    // Метод для проверки, является ли предмет баннером
    private boolean validItems(Material material) {
        return material == Material.GREEN_BANNER ||
                material == Material.CYAN_BANNER ||
                material == Material.ORANGE_BANNER ||
                material == Material.WHITE_BANNER ||
                material == Material.BAMBOO;
    }

    // Метод для проверки, зачарован ли предмет KNOCKBACK 1
    private boolean validEnchantments(ItemStack item) {
        return item.getEnchantmentLevel(Enchantment.KNOCKBACK) >= 1;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isEnabled) {
            return;
        }
        Player player = event.getPlayer();
        if (event.hasItem()) {
            Material itemMaterial = event.getItem().getType();
            boolean validItem = false;

            // Проверяем, является ли предмет BAMBOO или одним из баннеров
            if (validItems(itemMaterial)) {
                validItem = true;
            }

            // Проверяем, является ли предмет предметом BAMBOO или баннером и зачарован KNOCKBACK 1
            if (validItem && validEnchantments(event.getItem())) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (!playerTimers.containsKey(player) && validItems(player.getInventory().getItemInMainHand().getType()) && player.getCooldown(player.getInventory().getItemInMainHand().getType()) == 0) {
                        BambooTimer timer = new BambooTimer(player);
                        playerTimers.put(player, timer);
                        timer.runTaskTimer(this, 0, 1);
                    } else if (playerTimers.containsKey(player)) {
                        playerTimers.get(player).resetTimer();
                    }

                    // Ensure that the BambooTimer instance is not null before calling handleInput
                    if (playerTimers.get(player) != null) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        playerTimers.get(player).handleInput("R");
                    }
                } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (playerTimers.containsKey(player)) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        playerTimers.get(player).resetTimer();
                        playerTimers.get(player).handleInput("L");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!isEnabled) {
            return;
        }
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (playerTimers.containsKey(player)) {
                // Сбрасываем таймер при ударе сущности ЛКМ(L)
                playerTimers.get(player).resetTimer();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                playerTimers.get(player).handleInput("L");
            }
        }
    }

    private class BambooTimer extends BukkitRunnable {
        private final Player player;
        private int ticksLeft = 30;
        private StringBuilder input = new StringBuilder();

        public BambooTimer(Player player) {
            this.player = player;
            updateActionBar();
        }

        @Override
        public void run() {
            ticksLeft--;
            if (ticksLeft <= 0) {
                // Таймер истек, сбрасываем комбинацию и удаляем таймер
                input.setLength(0);
                updateActionBar();
                playerTimers.remove(player);
                cancel();
            } else {
                updateActionBar();
            }
        }

        public void handleInput(String inputKey) {
            if (!isEnabled) {
                return;
            }
            // Обработка ввода и добавление к комбинации
            input.append(inputKey);
            // Проверка наличия полной комбинации
            if (input.length() == 4) {
                String combo = input.toString();
                switch (combo) {
                    case "RLLL":
                        handleRLLLEffect();
                        break;
                    case "RLLR":
                        handleRLLREffect();
                        break;
                    case "RLRL":
                        handleRLRLEffect();
                        break;
                    case "RLRR":
                        handleRLRREffect();
                        break;
                    case "RRLL":
                        handleRRLLEffect();
                        break;
                    case "RRLR":
                        handleRRLREffect();
                        break;
                    case "RRRL":
                        handleRRRLEffect();
                        break;
                    case "RRRR":
                        handleRRRREffect();
                        break;
                    default:
                        break;
                }
                // Получаем предмет из главной руки игрока
                ItemStack item = player.getInventory().getItemInMainHand();

                // Проверяем, является ли предмет баннером или имеет зачарование KNOCKBACK 1
                if (validItems(item.getType()) || validEnchantments(item)) {
                    // Установка задержки для предмета и блокировка ввода комбинаций
                    player.setCooldown(item.getType(), getCooldownTicks(combo));
                }

                updateActionBar(); // Обновляем ActionBar для отображения финальной комбинации
                playerTimers.remove(player);
                cancel(); // Останавливаем таймер
            }
        }

        public void resetTimer() {
            ticksLeft = 30;
        }

        private void updateActionBar() {
            String actionBarMessage = ChatColor.GRAY + "[" + ChatColor.GREEN + input.toString() + ChatColor.GRAY + "]";
            player.sendActionBar(actionBarMessage);
        }

        private void handleRLLLEffect() {
            new CombinationsResults(player, statsManager).handleRLLLEffect();
        }

        private void handleRLLREffect() {
            new CombinationsResults(player, statsManager).handleRLLREffect();
        }

        private void handleRLRLEffect() {
            new CombinationsResults(player, statsManager).handleRLRLEffect();
        }

        private void handleRLRREffect() {
            new CombinationsResults(player, statsManager).handleRLRREffect();
        }

        private void handleRRLLEffect() {
            new CombinationsResults(player, statsManager).handleRRLLEffect();
        }

        private void handleRRLREffect() {
            new CombinationsResults(player, statsManager).handleRRLREffect();
        }

        private void handleRRRLEffect() {
            new CombinationsResults(player, statsManager).handleRRRLEffect();
        }

        private void handleRRRREffect() {
            new CombinationsResults(player, statsManager).handleRRRREffect();
        }

        private int getCooldownTicks(String combo) {
            // Установите кулдаун в тиках для каждой комбинации
            switch (combo) {
                case "RLLL":
                    return 60;
                case "RLLR":
                    return 20;
                case "RLRL":
                    return 30;
                case "RLRR":
                    return 10;
                case "RRLL":
                    return 20;
                case "RRLR":
                    return 100;
                case "RRRL":
                    return 30;
                case "RRRR":
                    return 200;
                default:
                    return 0;
            }
        }
    }

    public void flagTrue() {
        isEnabled = true;
    }

    public void flagFalse() {
        isEnabled = false;
    }
}