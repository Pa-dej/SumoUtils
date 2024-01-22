package me.padej.sumoutils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class CreateItems {

    public static ItemStack createNetheriteHelmet() {
        ItemStack netheriteHelmet = new ItemStack(Material.NETHERITE_HELMET, 1);
        ItemMeta helmetMeta = netheriteHelmet.getItemMeta();
        setCommonItemMeta(helmetMeta);
        helmetMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        return setAttributesAndReturn(helmetMeta);
    }

    public static ItemStack createNetheriteChestplate() {
        ItemStack netheriteChestplate = new ItemStack(Material.NETHERITE_CHESTPLATE, 1);
        ItemMeta chestplateMeta = netheriteChestplate.getItemMeta();
        setCommonItemMeta(chestplateMeta);
        chestplateMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        return setAttributesAndReturn(chestplateMeta);
    }

    public static ItemStack createNetheriteLeggings() {
        ItemStack netheriteLeggings = new ItemStack(Material.NETHERITE_LEGGINGS, 1);
        ItemMeta leggingsMeta = netheriteLeggings.getItemMeta();
        setCommonItemMeta(leggingsMeta);
        leggingsMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        return setAttributesAndReturn(leggingsMeta);
    }

    public static ItemStack createNetheriteBoots() {
        ItemStack netheriteBoots = new ItemStack(Material.NETHERITE_BOOTS, 1);
        ItemMeta bootsMeta = netheriteBoots.getItemMeta();
        setCommonItemMeta(bootsMeta);
        bootsMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        return setAttributesAndReturn(bootsMeta);
    }

    public static ItemStack createNetheriteSword() {
        ItemStack netheriteSword = new ItemStack(Material.NETHERITE_SWORD, 1);
        ItemMeta swordMeta = netheriteSword.getItemMeta();
        setCommonItemMeta(swordMeta);
        swordMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        swordMeta.addEnchant(Enchantment.KNOCKBACK, 3, true);
        swordMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        swordMeta.setDisplayName(ChatColor.GRAY + "Отдача III");
        swordMeta.setCustomModelData(2001);
        swordMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, createAttributeModifier(1.0/32));
        swordMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, createAttributeModifier(10.0));
        swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        return setAttributesAndReturn(swordMeta);
    }

    private static void setCommonItemMeta(ItemMeta itemMeta) {
        itemMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        itemMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, createAttributeModifier(5.0));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
    }

    private static ItemStack setAttributesAndReturn(ItemMeta itemMeta) {
        ItemStack itemStack = itemMeta instanceof ItemStack ? (ItemStack)itemMeta : null;
        if (itemStack != null) {
            itemStack.setItemMeta(itemMeta);
            itemStack.setDurability((short)2021);
        }
        return itemStack;
    }

    private static AttributeModifier createAttributeModifier(double value) {
        return new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", value, AttributeModifier.Operation.ADD_NUMBER);
    }
}