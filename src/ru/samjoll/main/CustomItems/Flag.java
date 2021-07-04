package ru.samjoll.main.CustomItems;

import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.metadata.BlockMetadataStore;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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

//    Функция установки флага
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

                flagLoc.add(0, 1, 0);
                flagLoc.getWorld().getBlockAt(flagLoc).setType(Material.getMaterial(plugin.getConfig().getString("flag-knob-material")));
                flagLoc.add(0, -1, 0);

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

//    Проверка территории на наличие флага
    boolean isFlagZone(Location point) throws Exception {
        for (String flagId : plugin.flagsDB.GetFlagsId()) {
            int offset = plugin.getConfig().getInt("protection-distance");
            Location flagLoc = plugin.flagsDB.GetFlagLoc(flagId);
            int x = flagLoc.getBlockX() - offset;
            int z = flagLoc.getBlockZ() - offset;
            int dx = flagLoc.getBlockX() + offset;
            int dz = flagLoc.getBlockZ() + offset;

            if (point.getBlockX() >= x && point.getBlockZ() >= z
                    && point.getBlockX() <= dx && point.getBlockZ() <= dz) {
                return true;
            }
        }

        return false;
    }
    String GetFlagInZone(Location point) {
        try {
            for (String flagId : plugin.flagsDB.GetFlagsId()) {
                int offset = plugin.getConfig().getInt("protection-distance");
                Location flagLoc = plugin.flagsDB.GetFlagLoc(flagId);
                int x = flagLoc.getBlockX() - offset;
                int z = flagLoc.getBlockZ() - offset;
                int dx = flagLoc.getBlockX() + offset;
                int dz = flagLoc.getBlockZ() + offset;

                if(point.getBlockX() >= x && point.getBlockZ() >= z
                        && point.getBlockX() <= dx && point.getBlockZ() <= dz ) {
                    return flagId;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    @EventHandler
    public void PrepareCraftingEvent(PrepareItemCraftEvent e) throws Exception {
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

                if(!plugin.flagsDB.onFlagExists(clothBlocks)) {
                    if (hasPillar && hasCloth) {
                        ItemStack newFlag = flag;
                        ItemMeta newFlagMeta = newFlag.getItemMeta();
                        newFlagMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "cloth-blocks"), PersistentDataType.STRING, clothBlocks);
                        newFlagMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "pillar-blocks"), PersistentDataType.STRING, pillarBlocks);
                        newFlagMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "isFlag"), PersistentDataType.STRING, "true");
                        newFlag.setItemMeta(newFlagMeta);

                        e.getInventory().setResult(newFlag);
                    } else {
                        e.getInventory().setResult(null);
                    }
                } else {
                    e.getView().getPlayer().sendMessage(String.valueOf(plugin.GetLangFileLine("ru", "exceptions.flag-exists")));
                    e.getInventory().setResult(null);
                }

                return;
            }
        }
    }

    @EventHandler
    public void SetFlagEvent(PlayerInteractEvent e) throws Exception{
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(e.getHand() == EquipmentSlot.HAND && e.hasItem()) {
                ItemStack item = e.getItem();
                ItemMeta itemMeta = item.getItemMeta();

                if(itemMeta.getPersistentDataContainer().has(new NamespacedKey(plugin, "isFlag"), PersistentDataType.STRING)) {
                    if (plugin.getConfig().getStringList("available-worlds").contains(e.getPlayer().getWorld().getName())) {
                        final Location flagLoc = e.getClickedBlock().getLocation();
                        final int protectionDistance = plugin.getConfig().getInt("protection-distance");

                        final String playerId = e.getPlayer().getUniqueId().toString();
                        final UUID flagId = UUID.randomUUID();
                        final String flagName = e.getPlayer().getName();
                        final String flagPattern = itemMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, "cloth-blocks"), PersistentDataType.STRING);
                        if(plugin.playersDB.GetPlayerFlag(playerId) == null) {
                            if (!plugin.flagsDB.onFlagExists(flagPattern)) {
                                if (!isFlagZone(flagLoc) &&
                                        !isFlagZone(flagLoc.clone().add(-protectionDistance, 0, 0)) &&
                                        !isFlagZone(flagLoc.clone().add(protectionDistance, 0, 0)) &&
                                        !isFlagZone(flagLoc.clone().add(0, 0, -protectionDistance)) &&
                                        !isFlagZone(flagLoc.clone().add(0, 0, protectionDistance)) &&
                                        !isFlagZone(flagLoc.clone().add(protectionDistance, 0, protectionDistance)) &&
                                        !isFlagZone(flagLoc.clone().add(protectionDistance, 0, -protectionDistance)) &&
                                        !isFlagZone(flagLoc.clone().add(-protectionDistance, 0, -protectionDistance)) &&
                                        !isFlagZone(flagLoc.clone().add(-protectionDistance, 0, protectionDistance))) {

                                    e.setCancelled(true);

                                    plugin.flagsDB.WriteFlag(flagId.toString(), flagName, flagPattern);
                                    plugin.flagsDB.WriteFlagLoc(flagId.toString(), flagLoc.clone().add(0, 1, 0).toVector(), flagLoc.getWorld().getName());
                                    plugin.flagsDB.WriteFlagKnobStrength(flagId.toString(), plugin.getConfig().getInt("start-flag-strength"), flagLoc.clone().add(0, 7, 0));
                                    plugin.playersDB.SetPlayerFlag(playerId, flagId.toString(), flagPattern);
                                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, SetFlag(item, e.getClickedBlock().getLocation()), 3L);
                                    item.setAmount(item.getAmount() - 1);
                                } else {
                                    e.getPlayer().sendMessage(String.valueOf(plugin.GetLangFileLine("ru", "exceptions.flag-is-near")));
                                }
                            } else {
                                e.getPlayer().sendMessage(String.valueOf(plugin.GetLangFileLine("ru", "exceptions.flag-exists")));
                            }
                        } else {
                            e.getPlayer().sendMessage(String.valueOf(plugin.GetLangFileLine("ru", "exceptions.player-has-flag")));
                        }
                    }
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void FlagKnobProtectionEvent(BlockBreakEvent e) throws Exception {
        Block brokenBlock = e.getBlock();
        Location blockLoc = brokenBlock.getLocation();

        String flagId = plugin.flagsDB.GetFlagIdWithKnobLoc(blockLoc);

        if(flagId != null) {
            e.setCancelled(true);

            Player player = e.getPlayer();

            if(!flagId.equals(plugin.playersDB.GetPlayerFlag(player.getUniqueId().toString()))) {
                int flagStrength = plugin.flagsDB.GetCurrentFlagKnobStrength(flagId);

                plugin.flagsDB.ChangeFlagKnobStrength(flagId, flagStrength-1);
                flagStrength--;

                if(flagStrength <= 0) {
                    plugin.playersDB.RemovePlayerFlag(flagId);
                    plugin.playersDB.RemoveFlag(flagId);
                    plugin.flagsDB.RemoveFlag(flagId);
                    blockLoc.getWorld().getBlockAt(blockLoc).setType(Material.NETHERRACK);
                    blockLoc.getWorld().getBlockAt(blockLoc).getRelative(BlockFace.UP).setType(Material.FIRE);
                }
            }
        }
    }

    @EventHandler
    public void FlagBreakBlockProtectionEvent(BlockBreakEvent e) throws Exception {
        final Block brokenBlock = e.getBlock();
        final Location blockLoc = brokenBlock.getLocation();

        if (isFlagZone(blockLoc)) {
            if (!GetFlagInZone(blockLoc).equals(plugin.playersDB.GetPlayerFlag(e.getPlayer().getUniqueId().toString()))) {
                if (!plugin.playersDB.IsPlacedByPlayerBlock(e.getPlayer().getUniqueId().toString(), brokenBlock.getType().name(), blockLoc)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }

        plugin.playersDB.RemoveBlock(brokenBlock.getType().name(), blockLoc);
    }

    @EventHandler
    public void FlagPlaceBlockProtectionEvent(BlockPlaceEvent e) throws Exception {
        final Block placedBlock = e.getBlockPlaced();
        final Location blockLoc = placedBlock.getLocation();

        if(isFlagZone(blockLoc)) {
            if(!GetFlagInZone(blockLoc).equals(plugin.playersDB.GetPlayerFlag(e.getPlayer().getUniqueId().toString()))) {
                if(!plugin.getConfig().getStringList("available-blocks").contains(placedBlock.getType().name())) {
                    e.setCancelled(true);
                    return;
                } else {
                    String playerId = e.getPlayer().getUniqueId().toString();
                    String blockId = placedBlock.getType().name();

                    plugin.playersDB.SetPlayerBlock(playerId, blockId, blockLoc);
                }
            }
        }
    }
}
