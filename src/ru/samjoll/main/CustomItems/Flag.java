package ru.samjoll.main.CustomItems;

import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.samjoll.main.FlagPrivate;

import java.util.UUID;

public class Flag implements Listener {
//    Главный класс плагина
    FlagPrivate plugin;
//    Идинтификатор предмета
    final public static String itemId = "flag";
//    Предмет
    ItemStack flag;
//    Ключ предмета
    NamespacedKey flagKey;
//    Рецепт крафта предмета
    ShapelessRecipe flagRecipe;
    public Recipe getRecipe() {
        return flagRecipe;
    }

//    Конструктор класса
    public Flag(FlagPrivate plugin) {
        this.plugin = plugin;

        InitItem();
        CreateRecipe();
    }

//    Иницилизация предмета
    void InitItem() {
        flagKey = new NamespacedKey(plugin, itemId);

        flag = new ItemStack(Material.getMaterial(plugin.getConfig().getString("custom-items." + itemId + ".material")));

        ItemMeta flagMeta = flag.getItemMeta();

        flagMeta.setDisplayName(plugin.getConfig().getString("custom-items." + itemId + ".name"));
        flagMeta.setLore(plugin.getConfig().getStringList("custom-items." + itemId + ".lore"));
        flag.addUnsafeEnchantment(Enchantment.LURE, 0);
        flagMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        flagMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        flagMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        flagMeta.setUnbreakable(true);

        flag.setItemMeta(flagMeta);
    }
//    Создание рецепта
    void CreateRecipe() {
        flagRecipe = new ShapelessRecipe(flagKey, flag);

        flagRecipe.addIngredient(Material.GOLDEN_HELMET);
        flagRecipe.addIngredient(Material.getMaterial(plugin.getConfig().getString("custom-items." + FlagCloth.itemId + ".material")));
        flagRecipe.addIngredient(Material.getMaterial(plugin.getConfig().getString("custom-items." + FlagPillar.itemId + ".material")));
    }

    Runnable SetFlag(ItemStack flag, Location flagLoc) {

        final ItemMeta flagMeta = flag.getItemMeta();

        final String[] pillarBlocks = flagMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, "pillar-blocks"), PersistentDataType.STRING).split(",");
        final String[] clothBlocks = flagMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, "cloth-blocks"), PersistentDataType.STRING).split(",");

        return new Runnable() {
            @Override
            public void run() {

                int indexOfPillarBlock = 2;

                for(int i = 0; i < 6; i++) {
                    flagLoc.add(0, 1, 0);

                    flagLoc.getWorld().getBlockAt(flagLoc).setType(Material.getMaterial(pillarBlocks[indexOfPillarBlock]));

                    if((i+1) % 2 == 0) {
                        indexOfPillarBlock--;
                    }
                }

                for(int i = 0; i < 9; i++) {
                    flagLoc.add(1, 0, 0);

                    flagLoc.getWorld().getBlockAt(flagLoc).setType(Material.getMaterial(clothBlocks[i]));

                    if((i+1) % 3 == 0) {
                        flagLoc.add(-3, -1, 0);
                    }
                }
            }
        };
    }

    @EventHandler
    public void PrepareCraftingEvent(PrepareItemCraftEvent e) {
        if(e.getRecipe() instanceof Keyed) {
            if(((Keyed)flagRecipe).getKey().equals(((Keyed)e.getRecipe()).getKey())) {
//                Проверка на наличие предметов
                boolean hasPillar = false;
                boolean hasCloth = false;

                String clothBlocks = "";
                String pillarBlocks = "";

                for(ItemStack item : e.getInventory().getMatrix()) {
                    if(item != null) {
                        if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "cloth-blocks"), PersistentDataType.STRING)) {
                            clothBlocks = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "cloth-blocks"), PersistentDataType.STRING);
                            hasCloth = true;
                        }
                        if(item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "pillar-blocks"), PersistentDataType.STRING)) {
                            pillarBlocks = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "pillar-blocks"), PersistentDataType.STRING);
                            hasPillar = true;
                        }
                    }
                }

                if(hasPillar && hasCloth) {
                    ItemStack newFlag = flag;
                    ItemMeta newFlagMeta = newFlag.getItemMeta();
                    newFlagMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "cloth-blocks"), PersistentDataType.STRING, clothBlocks);
                    newFlagMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "pillar-blocks"), PersistentDataType.STRING, pillarBlocks);
                    newFlagMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "isFlag"), PersistentDataType.STRING, "true");
                    newFlag.setItemMeta(newFlagMeta);

                    e.getInventory().setResult(newFlag);
                    return;
                } else {
                    e.getInventory().setResult(null);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void SetFlagEvent(PlayerInteractEvent e) {
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(e.getHand() == EquipmentSlot.HAND && e.hasItem()) {
                ItemStack item = e.getItem();
                ItemMeta itemMeta = item.getItemMeta();

                if(itemMeta.getPersistentDataContainer().has(new NamespacedKey(plugin, "isFlag"), PersistentDataType.STRING)) {
                    e.setCancelled(true);

                    UUID flagId = UUID.randomUUID();
                    String flagName = e.getPlayer().getName();
                    String flagPattern = itemMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, "cloth-blocks"), PersistentDataType.STRING);

                    try {
                        if(!plugin.flagsDB.onFlagExists(flagPattern)) {
                            plugin.flagsDB.WriteFlag(flagId.toString(), flagName, flagPattern);
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, SetFlag(item, e.getClickedBlock().getLocation()), 3L);
                            item.setAmount(item.getAmount()-1);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }


                }
            }
        }
    }
}
