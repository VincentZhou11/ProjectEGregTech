package com.zhouvincent11.projectegregtech;

import com.gregtechceu.gtceu.data.item.GTItems;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Set;
import java.util.function.Predicate;

public class Filters {


    public static boolean isMember(ItemStack itemsStack, Set<TagKey<Item>> tags) {
        return itemsStack.getTags().anyMatch(tags::contains);
    }

    public static boolean isMember(FluidStack fluidStack, Set<TagKey<Fluid>> tags) {
        return fluidStack.getTags().anyMatch(tags::contains);
    }

    public static Predicate<ItemStack> DEFAULT_ITEM_INPUT_FILTER = itemStack -> !Constants.NON_CONSUMED_ITEMS.contains(itemStack.getItem())
            && !isMember(itemStack, Constants.NON_CONSUMED_ITEM_TAGS);
    public static Predicate<FluidStack> DEFAULT_FLUID_INPUT_FILTER = fluidStack -> true;
    public static Predicate<ItemStack> DEFAULT_ITEM_OUTPUT_FILTER = itemStack -> !isMember(itemStack, Constants.WASTE_ITEM_TAGS);
    public static Predicate<FluidStack> DEFAULT_FLUID_OUTPUT_FILTER = fluidStack -> !isMember(fluidStack, Constants.WASTE_FLUID_TAGS);


    // Plant ball needed to produce bio-chaff -> bacteria -> wetware production chain
    public static Predicate<ItemStack> MACERATOR_ITEM_INPUT_FILTER = itemStack ->
            itemStack.getTags().anyMatch(Constants.INGOTS::equals)
                    || itemStack.getItem().equals(GTItems.PLANT_BALL.asItem())
                    || itemStack.getTags().anyMatch(Constants.STONES::equals)
                    || itemStack.getTags().anyMatch(Constants.CROPS::equals)
                    || itemStack.getTags().anyMatch(Constants.MARBLES::equals)
                    || itemStack.getTags().anyMatch(Constants.SANDS::equals)
                    || itemStack.getTags().anyMatch(Constants.GEMS::equals)
                    || itemStack.getTags().anyMatch(Constants.NETHERRACK::equals)
                    || itemStack.getTags().anyMatch(Constants.BASALT::equals)
                    || itemStack.getTags().anyMatch(Constants.BLACKSTONE::equals)
                    || itemStack.getTags().anyMatch(Constants.RAW_MEAT::equals)
//                    || itemStack.getTags().anyMatch(Constants.RAW_ORE::equals)
//                    || isMember(itemStack, Constants.ALL_PROCESSED_ORE_INTERMEDIATES)
//                    || itemStack.getTags().anyMatch(Constants.RAW_ORE_BLOCK::equals)

            ;

    public static Predicate<ItemStack> MACERATOR_ITEM_OUTPUT_FILTER = itemStack ->
            !isMember(itemStack, Constants.ALL_PROCESSED_ORE_INTERMEDIATES)
            && DEFAULT_ITEM_OUTPUT_FILTER.test(itemStack)
            && itemStack.getTags().noneMatch(Constants.RAW_MEAT::equals)
            ;

    // Most ore processing intermediates are already assigned EMC values, we just need to assign emc values to byproducts
    // Thermal centrifuge: keep all inputs
    // We only want to keeo outputs that are useful (so not ash, stone dust)
    public static Predicate<ItemStack> ORE_BYPRODUCT_ITEM_OUTPUT_FILTER = itemStack -> !isMember(itemStack, Constants.ALL_PROCESSED_ORE_INTERMEDIATES);

    public static Predicate<FluidStack> IGNORE_ALL_FLUIDS_FILTER = fluidStack -> false;

    public static Predicate<ItemStack> IGNORE_ALL_ITEMS_FILTER = itemStack -> false;

    public static Predicate<FluidStack> INTERESTING_DUSTS_FILTER = fluidStack -> false;

}