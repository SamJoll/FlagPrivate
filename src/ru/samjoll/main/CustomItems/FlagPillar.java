package ru.samjoll.main.CustomItems;

import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.samjoll.main.FlagPrivate;
import ru.samjoll.main.MaterialGroups;

//Кастомный предмет / Столб флага
public class FlagPillar implements Listener {
//    Главный класс плагина
    FlagPrivate plugin;
//    Идинтификатор предмета
    final public static String itemId = "flag-pillar";
//    Предмет
    ItemStack flagPillar;
//    Ключ предмета
    NamespacedKey flagPillarKey;
//    Рецепт крафта предмета
    ShapedRecipe flagPillarRecipe;
    public Recipe getRecipe() {
        return flagPillarRecipe;
    }

//    Конструктор класса
    public FlagPillar(FlagPrivate plugin) {
        this.plugin = plugin;

        InitItem();
        CreateRecipe();
    }

//    Иницилизация предмета
    void InitItem() {
        flagPillarKey = new NamespacedKey(plugin, itemId);

        flagPillar = new ItemStack(Material.getMaterial(plugin.getConfig().getString("custom-items." + itemId + ".material")));

        ItemMeta flagPillarMeta = flagPillar.getItemMeta();
        flagPillarMeta.setDisplayName(plugin.getConfig().getString("custom-items." + itemId + ".name"));
        flagPillarMeta.setLore(plugin.getConfig().getStringList("custom-items." + itemId + ".lore"));
        flagPillar.addUnsafeEnchantment(Enchantment.LURE, 0);
        flagPillarMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        flagPillarMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        flagPillarMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        flagPillarMeta.setUnbreakable(true);

        flagPillar.setItemMeta(flagPillarMeta);
    }
//    Создание рецепта
    void CreateRecipe() {
        flagPillarRecipe = new ShapedRecipe(flagPillarKey, flagPillar);

        flagPillarRecipe.shape(
                "X",
                "X",
                "X"
        );

        flagPillarRecipe.setIngredient('X', MaterialGroups.PILLAR_BLOCKS);
    }

    @EventHandler
    public void PrepareCraftingEvent(PrepareItemCraftEvent e) {
        if(e.getRecipe() instanceof Keyed) {
            if(((Keyed)flagPillarRecipe).getKey().equals(((Keyed)e.getRecipe()).getKey())) {

                StringBuilder pillarBlockMaterials = new StringBuilder();

                for(ItemStack item : e.getInventory().getMatrix()) {
                    if(item != null) {
                        pillarBlockMaterials.append(item.getType().name() + ",");
                    }
                }

                ItemStack newFlagPillar = flagPillar;
                ItemMeta newFlagPillarMeta = newFlagPillar.getItemMeta();
                newFlagPillarMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "pillar-blocks"), PersistentDataType.STRING, pillarBlockMaterials.toString());
                newFlagPillar.setItemMeta(newFlagPillarMeta);

                e.getInventory().setResult(newFlagPillar);
                return;
            }
        }
    }
}
