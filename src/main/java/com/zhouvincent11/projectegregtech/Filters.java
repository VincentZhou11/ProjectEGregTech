package com.zhouvincent11.projectegregtech;

import com.gregtechceu.gtceu.data.item.GTItems;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Set;
import java.util.function.Function;
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
    public static Predicate<ItemStack> DEFAULT_ITEM_OUTPUT_FILTER = itemStack -> !isMember(itemStack, Constants.IGNORED_ITEM_TAGS);
    public static Predicate<FluidStack> DEFAULT_FLUID_OUTPUT_FILTER = fluidStack -> !isMember(fluidStack, Constants.IGNORED_FLUID_TAGS);


    // Plant ball needed to produce bio-chaff -> bacteria -> wetware production chain
    public static Predicate<ItemStack> MACERATOR_ITEM_INPUT_FILTER = itemStack -> itemStack.getTags().anyMatch(Constants.INGOTS::equals) || itemStack.getItem().equals(GTItems.PLANT_BALL.asItem());
}
