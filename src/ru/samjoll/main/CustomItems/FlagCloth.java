package ru.samjoll.main.CustomItems;

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

public class FlagCloth implements Listener {
//    Главный класс плагина
    FlagPrivate plugin;
//    Идинтификатор предмета
    final public static String itemId = "flag-cloth";
//    Предмет
    ItemStack flagCloth;
//    Ключ предмета
    NamespacedKey flagClothKey;
//    Рецепт крафта предмета
    ShapedRecipe flagClothRecipe;
    public Recipe getRecipe() {
        return flagClothRecipe;
    }

//    Конструктор класса
    public FlagCloth(FlagPrivate plugin) {
        this.plugin = plugin;

        InitItem();
        CreateRecipe();
    }

//    Иницилизация предмета
    void InitItem() {
        flagClothKey = new NamespacedKey(plugin, itemId);

        flagCloth = new ItemStack(Material.getMaterial(plugin.getConfig().getString("custom-items." + itemId + ".material")));

        ItemMeta flagClothMeta = flagCloth.getItemMeta();

        flagClothMeta.setDisplayName(plugin.getConfig().getString("custom-items." + itemId + ".name"));
        flagClothMeta.setLore(plugin.getConfig().getStringList("custom-items." + itemId + ".lore"));
        flagCloth.addUnsafeEnchantment(Enchantment.LURE, 0);
        flagClothMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        flagClothMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        flagClothMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        flagClothMeta.setUnbreakable(true);

        flagCloth.setItemMeta(flagClothMeta);
    }
//    Создание рецепта
    void CreateRecipe() {
        flagClothRecipe = new ShapedRecipe(flagClothKey, flagCloth);

        flagClothRecipe.shape("XXX","XXX","XXX");

        flagClothRecipe.setIngredient('X', MaterialGroups.WOOL_BLOCKS);
    }

    @EventHandler
    public void PrepareCraftingEvent(PrepareItemCraftEvent e) {
        if(e.getRecipe() instanceof Keyed) {
            if(((Keyed)flagClothRecipe).getKey().equals(((Keyed)e.getRecipe()).getKey())) {

                StringBuilder clothBlockMaterials = new StringBuilder();

                for(ItemStack item : e.getInventory().getMatrix()) {
                    if(item != null) {
                        clothBlockMaterials.append(item.getType().name() + ",");
                    }
                }

                try {
                    if (!plugin.flagsDB.onFlagExists(clothBlockMaterials.toString())) {
                        ItemStack newFlagCloth = flagCloth;
                        ItemMeta newFlagClothMeta = newFlagCloth.getItemMeta();
                        newFlagClothMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "cloth-blocks"), PersistentDataType.STRING, clothBlockMaterials.toString());
                        newFlagCloth.setItemMeta(newFlagClothMeta);

                        e.getInventory().setResult(newFlagCloth);
                    } else {
                        e.getInventory().setResult(null);
                    }
                    return;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
