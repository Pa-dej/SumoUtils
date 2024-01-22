package me.padej.sumoutils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SumoUtilsCommand implements CommandExecutor, TabCompleter {
    private SumoUtils sumoUtils;
    private FileConfiguration statsConfig;

    public SumoUtilsCommand(SumoUtils sumoUtils, FileConfiguration statsConfig) {
        this.sumoUtils = sumoUtils;
        this.statsConfig = statsConfig;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;  // Возвращаем пустой список для некоторых случаев, где нет игрока (например, консоль)
        }

        Player player = (Player) sender;

        // Проверяем наличие права для отображения подсказок для enable и disable
        if (player.hasPermission("sumoutils.event")) {
            completions.add("disable");
            completions.add("enable");
        }

        if (args.length == 1) {
            // Добавляем автодополнение для первого аргумента
            completions.add("stats");
            completions.add("global_stats");
            completions.add("info");

        } else if (args.length == 2 && args[0].equalsIgnoreCase("stats")) {
            ConfigurationSection configSection = statsConfig.getConfigurationSection("");

            if (configSection != null) {
                completions.addAll(configSection.getKeys(false));
            }
        }

        // Используйте StringUtil.copyPartialMatches для более гибкого автодополнения
        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "[🗡] Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("sumoutils")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("enable")) {
                    if (player.hasPermission("sumoutils.event")) {
                        if (sumoUtils != null) {
                            sumoUtils.flagTrue();
                            player.sendMessage(ChatColor.YELLOW + "[🗡] SumoUtils methods are now enabled.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "[🗡] You don't have permission to use this command.");
                    }
                } else if (args[0].equalsIgnoreCase("info")) {
                    player.sendMessage(ChatColor.GRAY + "[===============" + ChatColor.GREEN + " <Sumo Utils> " + ChatColor.GRAY + "================]");
                    player.sendMessage(ChatColor.GRAY + "  Current Version: " + ChatColor.YELLOW + "1.9");
                    player.sendMessage(ChatColor.GRAY + "  " + ChatColor.GRAY + "/su " + ChatColor.AQUA + "disable " + ChatColor.GRAY + "- выключает ввод комбинаций");
                    player.sendMessage(ChatColor.GRAY + "  " + ChatColor.GRAY + "/su " + ChatColor.AQUA + "enable " + ChatColor.GRAY + "- включает ввод комбинаций");
                    player.sendMessage(ChatColor.GRAY + "  " + ChatColor.GRAY + "/su " + ChatColor.AQUA + "stats " + "<playerName> " + ChatColor.GRAY + "- статистика игрока");
                    player.sendMessage(ChatColor.GRAY + "  " + ChatColor.GRAY + "/su " + ChatColor.AQUA + "global_stats " + ChatColor.GRAY + "- статистика всех игроков вместе");
                    player.sendMessage(ChatColor.GRAY + "  " + ChatColor.GRAY + "/su " + ChatColor.AQUA + "info " + ChatColor.GRAY + "- это сообщение");
                } else if (args[0].equalsIgnoreCase("disable")) {
                    if (player.hasPermission("sumoutils.event")) {
                        if (sumoUtils != null) {
                            sumoUtils.flagFalse();
                            player.sendMessage(ChatColor.YELLOW + "[🗡] SumoUtils methods are now disabled.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "[🗡] You don't have permission to use this command.");
                    }
                } else if (args[0].equalsIgnoreCase("global_stats")) {
                    // Код для обработки глобальной статистики
                    File configFile = new File("plugins/SumoUtils/Statistics.yml");
                    FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

                    player.sendMessage(ChatColor.GRAY + "[===<" + ChatColor.GREEN + " Общая статистика " + ChatColor.GRAY + ">===]");

                    int totalPlayers = config.getKeys(false).size();
                    int totalSum = Arrays.asList("RLLL", "RLLR", "RLRL", "RLRR", "RRLL", "RRLR", "RRRL", "RRRR").stream()
                            .mapToInt(combination -> config.getKeys(false).stream()
                                    .mapToInt(playerName -> config.getInt(playerName + "." + combination, 0))
                                    .sum())
                            .sum();

                    for (String combination : Arrays.asList("RLLL", "RLLR", "RLRL", "RLRR", "RRLL", "RRLR", "RRRL", "RRRR")) {
                        int sum = config.getKeys(false).stream()
                                .mapToInt(playerName -> config.getInt(playerName + "." + combination, 0))
                                .sum();

                        double averagePerPlayer = totalPlayers > 0 ? (double) sum / totalPlayers : 0;
                        double percentageOfTotal = totalSum > 0 ? (sum * 100.0) / totalSum : 0;

                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + combination + ChatColor.GRAY + "]: " + ChatColor.YELLOW + sum + " | " + ChatColor.YELLOW + averagePerPlayer + " | " + String.format("%.2f%%", percentageOfTotal));
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "[🗡] Invalid argument. Use /sumoutils enable, /sumoutils stats <playerName>, /sumoutils disable, or /sumoutils global_stats.");
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("stats")) {
                String playerName = args[1];
                // Код для обработки статистики конкретного игрока
                File configFile = new File("plugins/SumoUtils/Statistics.yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

                if (config.getConfigurationSection(playerName) != null) {
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + ChatColor.GRAY + "====<" + ChatColor.GREEN + playerName + ChatColor.GRAY + ">====" + ChatColor.GRAY + "]");

                    int totalSum = 0;

                    // Суммируем все значения, чтобы вычислить проценты
                    for (String combination : Arrays.asList("RLLL", "RLLR", "RLRL", "RLRR", "RRLL", "RRLR", "RRRL", "RRRR")) {
                        totalSum += config.getInt(playerName + "." + combination, 0);
                    }

                    // Вывод статистики для каждой комбинации с процентами
                    for (String combination : Arrays.asList("RLLL", "RLLR", "RLRL", "RLRR", "RRLL", "RRLR", "RRRL", "RRRR")) {
                        int count = config.getInt(playerName + "." + combination, 0);
                        double percentage = totalSum > 0 ? (count * 100.0) / totalSum : 0;
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + combination + ChatColor.GRAY + "]: " + ChatColor.YELLOW + count + " | " + String.format("%.2f%%", percentage));
                    }
                } else {
                    player.sendMessage(ChatColor.YELLOW + "[🗡] No statistics found for player " + playerName + ".");
                }
            } else {
                player.sendMessage(ChatColor.RED + "[🗡] Invalid argument. Use /sumoutils enable, /sumoutils stats <playerName>, /sumoutils disable, or /sumoutils global_stats.");
            }
        }

        return true;
    }
}