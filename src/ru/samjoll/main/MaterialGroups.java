package ru.samjoll.main;

import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;

public class MaterialGroups {
//    Материалы для столба
    public static RecipeChoice.MaterialChoice PILLAR_BLOCKS = new RecipeChoice.MaterialChoice(
        Material.ACACIA_LOG,
        Material.BIRCH_LOG,
        Material.SPRUCE_LOG,
        Material.OAK_LOG,
        Material.JUNGLE_LOG,
        Material.DARK_OAK_LOG,
        Material.STRIPPED_SPRUCE_LOG,
        Material.STRIPPED_JUNGLE_LOG,
        Material.STRIPPED_OAK_LOG,
        Material.STRIPPED_DARK_OAK_LOG,
        Material.STRIPPED_BIRCH_LOG,
        Material.STRIPPED_ACACIA_LOG,
        Material.QUARTZ_PILLAR,
        Material.PURPUR_PILLAR,
        Material.BONE_BLOCK,
        Material.WARPED_STEM,
        Material.CRIMSON_STEM,
        Material.STRIPPED_CRIMSON_STEM,
        Material.STRIPPED_WARPED_STEM,
        Material.POLISHED_BASALT,
        Material.ANCIENT_DEBRIS,
        Material.MUSHROOM_STEM,
        Material.DRIED_KELP_BLOCK
    );

    public static RecipeChoice.MaterialChoice WOOL_BLOCKS = new RecipeChoice.MaterialChoice(
        Material.BLACK_WOOL,
        Material.BLUE_WOOL,
        Material.BROWN_WOOL,
        Material.CYAN_WOOL,
        Material.PURPLE_WOOL,
        Material.RED_WOOL,
        Material.ORANGE_WOOL,
        Material.PINK_WOOL,
        Material.LIME_WOOL,
        Material.LIGHT_GRAY_WOOL,
        Material.WHITE_WOOL,
        Material.YELLOW_WOOL,
        Material.GRAY_WOOL,
        Material.LIGHT_BLUE_WOOL,
        Material.MAGENTA_WOOL,
        Material.GREEN_WOOL
    );
}
