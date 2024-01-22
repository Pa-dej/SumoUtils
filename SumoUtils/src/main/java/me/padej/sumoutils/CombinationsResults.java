package me.padej.sumoutils;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class CombinationsResults {


    private final Player player;
    private final StatisticsManager statsManager;

    public CombinationsResults(Player player, StatisticsManager statsManager) {
        this.player = player;
        this.statsManager = statsManager;
    }

    public void handleRLLLEffect() {
        statsManager.updateStatistics(player.getName(), "RLLL");
        // Создаем сущность GOAT
        Location playerLocation = player.getLocation();

        // Звук козы
        player.playSound(playerLocation, Sound.ENTITY_GOAT_AMBIENT, 1, 1);

        // Получаем направление взгляда игрока без компоненты по оси Y
        Vector playerDirection = new Vector(playerLocation.getDirection().getX(), 0, playerLocation.getDirection().getZ()).normalize();

        // Умножаем направление взгляда на расстояние, чтобы получить точку перед игроком
        Vector spawnOffset = playerDirection.multiply(1.7);

        // Добавляем смещение по оси Y для того, чтобы коза появилась на уровне ног
        Location goatSpawnLocation = playerLocation.clone().add(spawnOffset).add(0, 0.33, 0);

        // Создаем сущность GOAT
        LivingEntity goat = (LivingEntity) playerLocation.getWorld().spawnEntity(goatSpawnLocation, EntityType.GOAT);

        // Отключаем гравитацию & AI для GOAT
        goat.setGravity(false);
        goat.setInvulnerable(true);
        goat.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 110, true));

        // Используем BukkitRunnable
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                moveGoat(goat);

                // Увеличиваем таймер и удаляем GOAT после 40 тиков
                if (++ticks >= 100) {
                    goat.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, goat.getLocation(), 1);
                    goat.remove();
                    cancel(); // Останавливаем выполнение задачи
                }
            }
        }.runTaskTimer(SumoUtils.getPlugin(SumoUtils.class), 0L, 1L);
    }

    private void moveGoat(LivingEntity goat) {
        // Получаем текущую скорость GOAT
        Vector currentVelocity = goat.getVelocity();

        // Получаем направление взгляда GOAT и умножаем на 0.5
        Vector goatDirection = goat.getLocation().getDirection().normalize().multiply(0.083);

        // Добавляем новую скорость к текущей
        Vector newVelocity = currentVelocity.add(goatDirection);

        // Устанавливаем новую скорость GOAT
        goat.setVelocity(newVelocity);

        // Particles
        goat.getWorld().spawnParticle(Particle.CLOUD, goat.getLocation(), 1, 0, 0, 0, 0.01);

        if (goat.getTicksLived() >= 5) {
            // Проверяем столкновение GOAT с другими сущностями
            for (Entity nearbyEntity : goat.getNearbyEntities(1, 1, 1)) {
                if (nearbyEntity.getType() != EntityType.GOAT && nearbyEntity != goat) {
                    // Получаем вектор от козы к сущности и умножаем на 0.9 для снижения силы отталкивания
                    Vector pushDirection = nearbyEntity.getLocation().toVector().subtract(goat.getLocation().toVector()).normalize().multiply(1.5);

                    // Устанавливаем силу отталкивания по Y
                    pushDirection.setY(0.95);

                    // Применяем силу отталкивания к сущности
                    nearbyEntity.setVelocity(pushDirection);

                    // Воспроизводим звук отталкивания
                    goat.getWorld().playSound(goat.getLocation(), Sound.ENTITY_SHULKER_BULLET_HIT, 1.0f, 1.0f);

                    goat.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, goat.getLocation(), 1);
                    return;
                }
            }
        }

        // Уменьшаем таймер и удаляем GOAT после 40 тиков
        if (goat.getTicksLived() >= 100) {
            goat.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, goat.getLocation(), 1);
            goat.remove();
        }
    }

    public void handleRLLREffect() {
        Vector playerDirection = player.getLocation().getDirection().normalize();

        // Подкидываем игрока по Y
        Vector velocity = new Vector(playerDirection.getX() * 0.3, 0.95, playerDirection.getZ() * 0.3);

        // Подкидываем игрока с использованием нового вектора скорости
        player.setVelocity(velocity);

        // Отображаем частицы CLOUD под ногами
        player.getWorld().spawnParticle(
                Particle.CLOUD,
                player.getLocation(),
                25, // количество частиц
                0.4, 0.4, 0.4, // размеры частиц
                0.01 // скорость частиц
        );

        // Воспроизводим звук получения опыта
        player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_SHOOT, 1.0f, 1.0f);

        statsManager.updateStatistics(player.getName(), "RLLR");
    }

    public void handleRLRLEffect() {
        // Получаем сущность, на которую смотрит игрок
        Entity targetEntity = getTargetEntity(player, 5);
        if (targetEntity != null) {
            // Отображаем частицу SWEEP_ATTACK
            targetEntity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, targetEntity.getLocation().add(0, 0.5, 0), 1);
            targetEntity.getWorld().spawnParticle(Particle.CRIT, targetEntity.getLocation().add(0, 0.5, 0), 50, 0.4, 1,0.4, 0.1);
            // Воспроизводим звук PLAYER_SWEEP_ATTACK для всех игроков в радиусе
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
            // Вычисляем новый вектор скорости для подкидывания вверх (0.73 по Y) и сохранения откидывания на 0.5
            Vector velocity = targetEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            velocity.setY(0.53); // Устанавливаем подкидывание вверх по оси Y
            velocity.multiply(1.9); // Устанавливаем откидывание на 0.6

            // Устанавливаем новый вектор скорости сущности
            targetEntity.setVelocity(velocity);

            statsManager.updateStatistics(player.getName(), "RLRL");
        }

    }

    public void handleRLRREffect() {
        // Получаем направление взгляда игрока
        Vector playerDirection = player.getLocation().getDirection().normalize();

        // Вычисляем новый вектор скорости с подкидыванием вперед (0.6) и вверх по оси Y (0.5)
        Vector velocity = new Vector(playerDirection.getX() * 0.7, 0.7, playerDirection.getZ() * 0.7);

        // Подкидываем игрока с использованием нового вектора скорости
        player.setVelocity(velocity);

        // Отображаем частицы CLOUD под ногами
        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 1);

        // Воспроизводим звук получения опыта
        player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_BULLET_HIT, 1.0f, 1.0f);

        statsManager.updateStatistics(player.getName(), "RLRR");
    }

    public void handleRRLLEffect() {
        // Получаем сущность, на которую смотрит игрок
        Entity targetEntity = getTargetEntity(player, 5);

        if (targetEntity != null) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);

            // Отображаем частицу SWEEP_ATTACK под игроком
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0, 0.5, 0), 1);
            targetEntity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, targetEntity.getLocation().add(0, 0.5, 0), 1);

            // Вычисляем новый вектор скорости для подкидывания по оси Y (0.5) и сохранения откидывания на 0.6 в других направлениях
            Vector playerDirection = targetEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            Vector entityDirection = targetEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            playerDirection.setY(0.7); // Устанавливаем подкидывание по оси Y (0.5)
            playerDirection.multiply(1.2); // Сохраняем откидывание на 0.6 в других направлениях
            entityDirection.setY(-0.7); // Устанавливаем подкидывание по оси Y (0.5)
            entityDirection.multiply(-1.2); // Сохраняем откидывание на 0.6 в других направлениях

            // Устанавливаем новый вектор скорости для игрока и существа
            player.setVelocity(playerDirection);
            targetEntity.setVelocity(entityDirection);

            statsManager.updateStatistics(player.getName(), "RRLL");
        }
    }

    public void handleRRLREffect() {
        // Отключаем гравитацию для игрока на 60 тиков
        player.setGravity(false);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1,1);
        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getLocation(), 20, 0.2, 0.5,0.2,0.01);

        statsManager.updateStatistics(player.getName(), "RRLR");

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                // Восстанавливаем гравитацию после 60 тиков
                if (ticks < 60) {
                    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 1, 0.2, 0.2,0.2,0.01);
                    ticks++;
                } else {
                    player.setGravity(true);
                    cancel(); // Остановить таймер после 60 тиков
                }
            }
        }.runTaskTimer(SumoUtils.getPlugin(SumoUtils.class), 0, 1);

        new BukkitRunnable() {
            int ticks = 0;
            int timer = 60;

            @Override
            public void run() {
                // Восстанавливаем гравитацию после 60 тиков
                if (ticks < 60) {
                    // Вывод сообщения в actionbar в зависимости от значения таймера
                    String actionBarMessage;
                    if (timer >= 60) {
                        actionBarMessage = ChatColor.GRAY + "[" + ChatColor.GREEN + "===" + ChatColor.GRAY + "]";
                    } else if (timer >= 40) {
                        actionBarMessage = ChatColor.GRAY + "[" + ChatColor.GREEN + "===" + ChatColor.GRAY + "]";
                    } else if (timer >= 20) {
                        actionBarMessage = ChatColor.GRAY + "[" + ChatColor.GOLD + "==" + ChatColor.GRAY + "]";
                    } else if (timer >= 2) {
                        actionBarMessage = ChatColor.GRAY + "[" + ChatColor.RED + "=" + ChatColor.GRAY + "]";
                    } else if (timer == 1) {
                        actionBarMessage = ChatColor.GRAY + "[]";
                    } else {
                        actionBarMessage = ChatColor.RED + "ERROR";
                        cancel(); // Останавливаем таймер, когда значение достигло 0
                    }

                    // Отправляем сообщение в actionbar
                    player.sendActionBar(actionBarMessage);

                    // Уменьшаем значение таймера
                    timer--;
                    ticks++;
                } else {
                    player.setGravity(true);
                    cancel(); // Остановить таймер после 60 тиков
                }
            }
        }.runTaskTimer(SumoUtils.getPlugin(SumoUtils.class), 0, 1);
    }

    public void handleRRRLEffect() {
        // Получаем сущность, на которую смотрит игрок
        Entity targetEntity = getTargetEntity(player, 5);
        if (targetEntity != null) {

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);

            // Отображаем частицу SWEEP_ATTACK под игроком
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0, 0.5, 0), 1);
            targetEntity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, targetEntity.getLocation().add(0, 0.5, 0), 1);
            targetEntity.getWorld().spawnParticle(Particle.CRIT, targetEntity.getLocation().add(0, 0.5, 0), 50, 0.4, 1, 0.4, 0.01);

            // Подкидываем сущность и игрока силой 0.7
            double playerForce = 0.7;
            double entityForce = 2.4;
            Vector playerDirection = player.getLocation().getDirection().normalize().multiply(playerForce);
            Vector entityDirection = targetEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(entityForce);

            // Устанавливаем вертикальное скорение по Y
            playerDirection.setY(0.7);
            entityDirection.setY(0.7);

            player.setVelocity(playerDirection);
            targetEntity.setVelocity(entityDirection);

            statsManager.updateStatistics(player.getName(), "RRRL");
        }
    }

    public void handleRRRREffect() {
        statsManager.updateStatistics(player.getName(), "RRRR");
        // Надеваем на игрока полный сет незеритовой брони с зачарованием Проклятье Несъемности
        ItemStack netheriteHelmet = new ItemStack(Material.NETHERITE_HELMET, 1);
        ItemStack netheriteChestplate = new ItemStack(Material.NETHERITE_CHESTPLATE, 1);
        ItemStack netheriteLeggings = new ItemStack(Material.NETHERITE_LEGGINGS, 1);
        ItemStack netheriteBoots = new ItemStack(Material.NETHERITE_BOOTS, 1);

        // Создаем меч
        ItemStack netheriteSword = new ItemStack(Material.NETHERITE_SWORD, 1);

        // Получаем метаданные предмета для каждого элемента брони
        ItemMeta helmetMeta = netheriteHelmet.getItemMeta();
        ItemMeta chestplateMeta = netheriteChestplate.getItemMeta();
        ItemMeta leggingsMeta = netheriteLeggings.getItemMeta();
        ItemMeta bootsMeta = netheriteBoots.getItemMeta();

        // Получаем метаданные предмета
        ItemMeta swordMeta = netheriteSword.getItemMeta();

        // Добавляем проклятие на все предметы
        helmetMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        chestplateMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        leggingsMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        bootsMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);

        helmetMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        chestplateMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        leggingsMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        bootsMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);

        // Добавляем проклятие на меч
        swordMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        swordMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        swordMeta.addEnchant(Enchantment.KNOCKBACK, 3, true);

        // Создаем атрибут для сопротивления отталкиванию
        AttributeModifier knockbackResistance = new AttributeModifier(
                UUID.randomUUID(),
                "generic.knockbackResistance",
                5.0, // Уровень сопротивления отталкиванию (можете изменить по необходимости)
                AttributeModifier.Operation.ADD_NUMBER
        );

        // Добавляем атрибут к метаданным каждого предмета
        helmetMeta.addAttributeModifier(
                Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                knockbackResistance
        );
        chestplateMeta.addAttributeModifier(
                Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                knockbackResistance
        );
        leggingsMeta.addAttributeModifier(
                Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                knockbackResistance
        );
        bootsMeta.addAttributeModifier(
                Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                knockbackResistance
        );

        // Создаем атрибут для установки урона
        AttributeModifier damageModifier = new AttributeModifier(
                UUID.randomUUID(),
                "generic.attackDamage",
                (double) 1/32, // Урон меча
                AttributeModifier.Operation.ADD_NUMBER
        );

        // Добавляем атрибут к метаданным меча
        swordMeta.addAttributeModifier(
                Attribute.GENERIC_ATTACK_DAMAGE,
                damageModifier
        );

        // Добавляем атрибут к метаданным меча для скорости атаки
        AttributeModifier attackSpeedModifier = new AttributeModifier(
                UUID.randomUUID(),
                "generic.attackSpeed",
                10.0, // Скорость атаки
                AttributeModifier.Operation.ADD_NUMBER
        );

        // Добавляем атрибут к метаданным меча
        swordMeta.addAttributeModifier(
                Attribute.GENERIC_ATTACK_SPEED,
                attackSpeedModifier
        );

        // Добавляем название мечу
        swordMeta.setDisplayName(ChatColor.GREEN + "Отдача III");

        // Устанавливаем CustomModelData
        swordMeta.setCustomModelData(2001);

        // Скрываем все флаги на броне и мече
        swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        helmetMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        chestplateMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        leggingsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        bootsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        // Устанавливаем обновленные метаданные для каждого элемента брони
        netheriteHelmet.setItemMeta(helmetMeta);
        netheriteChestplate.setItemMeta(chestplateMeta);
        netheriteLeggings.setItemMeta(leggingsMeta);
        netheriteBoots.setItemMeta(bootsMeta);

        // Устанавливаем обновленные метаданные для меча
        netheriteSword.setItemMeta(swordMeta);

        // Устанавливаем прочность элементов брони
        netheriteHelmet.setDurability((short) 402);
        netheriteChestplate.setDurability((short) 587);
        netheriteLeggings.setDurability((short) 550);
        netheriteBoots.setDurability((short) 476);

        netheriteSword.setDurability((short) 2021);

        // Надеваем броню
        if (player.getInventory().getHelmet() == null) {
            player.getInventory().setHelmet(netheriteHelmet);
        }

        if (player.getInventory().getChestplate() == null) {
            player.getInventory().setChestplate(netheriteChestplate);
        }

        if (player.getInventory().getLeggings() == null) {
            player.getInventory().setLeggings(netheriteLeggings);
        }

        if (player.getInventory().getBoots() == null) {
            player.getInventory().setBoots(netheriteBoots);
        }

        // Даем меч игроку (замените "player" на ваш объект Player)
        player.getInventory().addItem(netheriteSword);

        // Звук
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE,  1, 1);
        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation().add(0, 1, 0), 1);

        // Задержка перед удалением брони
        new BukkitRunnable() {

            @Override
            public void run() {
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

                // Звук
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation().add(0, 1, 0), 1);
            }
        }.runTaskLater(SumoUtils.getPlugin(SumoUtils.class), 20 * 5);

        new BukkitRunnable() {
            int ticks = 0;
            int timer = 100;

            @Override
            public void run() {
                // Восстанавливаем гравитацию после 60 тиков
                if (ticks < 100) {
                    // Вывод сообщения в actionbar в зависимости от значения таймера
                    String actionBarMessage;
                    if (timer >= 100) {
                        actionBarMessage = ChatColor.GRAY + "[" + ChatColor.GREEN + "=====" + ChatColor.GRAY + "]";
                    } else if (timer >= 80) {
                        actionBarMessage = ChatColor.GRAY + "[" + ChatColor.GREEN + "=====" + ChatColor.GRAY + "]";
                    } else if (timer >= 60) {
                        actionBarMessage = ChatColor.GRAY + "[" + ChatColor.GREEN + "====" + ChatColor.GRAY + "]";
                    } else if (timer >= 40) {
                        actionBarMessage = ChatColor.GRAY + "[" + ChatColor.GOLD + "===" + ChatColor.GRAY + "]";
                    } else if (timer >= 20) {
                        actionBarMessage = ChatColor.GRAY + "[" + ChatColor.RED + "==" + ChatColor.GRAY + "]";
                    } else if (timer >= 2) {
                        actionBarMessage = ChatColor.GRAY + "[" + ChatColor.RED + "=" + ChatColor.GRAY + "]";
                    } else if (timer == 1) {
                        actionBarMessage = ChatColor.GRAY + "[]";
                    } else {
                        actionBarMessage = ChatColor.RED + "ERROR";
                        cancel(); // Останавливаем таймер, когда значение достигло 0
                    }

                    // Отправляем сообщение в actionbar
                    player.sendActionBar(actionBarMessage);

                    // Уменьшаем значение таймера
                    timer--;
                    ticks++;
                } else {
                    cancel(); // Остановить таймер после 60 тиков
                }
            }
        }.runTaskTimer(SumoUtils.getPlugin(SumoUtils.class), 0, 1);
    }

    private Entity getTargetEntity(Player player, double range) {
        // Получаем ближайшую сущность в указанном радиусе от игрока
        Location playerEyeLocation = player.getEyeLocation();
        for (Entity entity : player.getNearbyEntities(range, range, range)) {

            Location entityLocation = entity.getLocation();
            // Проверяем, что сущность в поле зрения игрока (учитываем высоту глаз)
            if (playerEyeLocation.distanceSquared(entityLocation) <= range * range
                    && playerEyeLocation.getDirection().dot(entityLocation.toVector().subtract(playerEyeLocation.toVector()).normalize()) > 0) {
                return entity;
            }
        }
        return null;
    }
}
