package com.zhouvincent11.projectegregtech;

import com.gregtechceu.gtceu.data.item.GTItems;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Constants {


    public static final TagKey<Item> getItemTagKey(String... locations) {
        ResourceLocation rl = ResourceLocation.parse("c:");
        int x = 0;
        for (String location : locations) {
            rl = rl.withSuffix((x==0 ? "" : "/")+location);
            x++;
        }

        return TagKey.create(Registries.ITEM, rl);
    }

    public static final TagKey<Fluid> getFluidTagKey(String... locations) {
        ResourceLocation rl = ResourceLocation.parse("c:");
        int x = 0;
        for (String location : locations) {
            rl = rl.withSuffix((x==0 ? "" : "/") +location);
            x++;
        }
        return TagKey.create(Registries.FLUID, rl);
    }

    public static final Set<Item> NON_CONSUMED_ITEMS = new HashSet<>();
    public static final Set<TagKey<Item>> NON_CONSUMED_ITEM_TAGS = new HashSet<>();

    public static final Set<TagKey<Fluid>> WASTE_FLUID_TAGS = new HashSet<>();
    public static final Set<TagKey<Item>> WASTE_ITEM_TAGS = new HashSet<>();

    public static final TagKey<Item> INGOTS = getItemTagKey("ingots");
    public static final TagKey<Item> NUGGETS = getItemTagKey("nuggets");
    public static final TagKey<Item> DUSTS = getItemTagKey("dusts");

    // Raw ores and intermediaries
    public static final TagKey<Item> ORES = getItemTagKey("ores");
    public static final TagKey<Item> RAW_MATERIALS = getItemTagKey("raw_materials");
    public static final TagKey<Item> CRUSHED_ORES = getItemTagKey("crushed_ores");
    public static final TagKey<Item> PURIFIED_ORES = getItemTagKey("purified_ores");
    public static final TagKey<Item> REFINED_ORES = getItemTagKey("refined_ores");
    public static final TagKey<Item> IMPURE_DUSTS = getItemTagKey("impure_dusts");
    public static final TagKey<Item> PURE_DUSTS = getItemTagKey("pure_dusts");
    public static final Set<TagKey<Item>> ALL_PROCESSED_ORE_INTERMEDIATES = Set.of(CRUSHED_ORES, PURIFIED_ORES, REFINED_ORES, IMPURE_DUSTS, PURE_DUSTS);

    // Crushed -> Orewasher/Chemical Path -> Purified
    // Purified -> Macerator -> Pure Dust
    // Puredust -> Centrifuge -> Dust

    // Crushed -> Thermal Centrifuge -> Refined
    // Refined -> Macerator - > Dust

    // Crushed -> Macerator -> Impure Dust
    // Impure Dust -> Centrifuge -> Dust

    public static final TagKey<Item> RAW_ORE_BLOCK = getItemTagKey("ore");
    public static final TagKey<Item> RAW_ORE = getItemTagKey("raw_materials");
    public static final TagKey<Item> ORE_GEMS = getItemTagKey("ore_gems");
    public static final TagKey<Item> SANDS = getItemTagKey("sands");
    public static final TagKey<Item> MARBLES = getItemTagKey("marbles");
    public static final TagKey<Item> STONES = getItemTagKey("stones");
    public static final TagKey<Item> CROPS = getItemTagKey("crops");
    public static final TagKey<Item> GEMS = getItemTagKey("gems");


    public static final TagKey<Item> RAW_MEAT = getItemTagKey("foods", "raw_meat");


    public static final TagKey<Item> NETHERRACK = getItemTagKey("netherrack");
    public static final TagKey<Item> BASALT = getItemTagKey("basalts");
    public static final TagKey<Item> BLACKSTONE = getItemTagKey("blackstones");


    public static final TagKey<Fluid> WATER = getFluidTagKey("water");
    public static final TagKey<Fluid> OXYGEN = getFluidTagKey("oxygen");
    public static final TagKey<Fluid> NITROGEN = getFluidTagKey("nitrogen");
    public static final TagKey<Fluid> HYDROGEN = getFluidTagKey("hydrogen");
    public static final TagKey<Fluid> NEON = getFluidTagKey("neon");
    public static final TagKey<Fluid> ARGON = getFluidTagKey("argon");
    public static final TagKey<Fluid> CHLORINE = getFluidTagKey("chlorine");
    public static final TagKey<Fluid> DILUTED_SULFURIC_ACID = getFluidTagKey("diluted_sulfuric_acid");
    public static final TagKey<Fluid> DILUTED_HYDROCHLORIC_ACID = getFluidTagKey("diluted_hydrochloric_acid");
    public static final TagKey<Fluid> SULFURIC_ACID = getFluidTagKey("sulfuric_acid");
    public static final TagKey<Fluid> HYDROCHLORIC_ACID = getFluidTagKey("hydrochloric_acid");
    public static final TagKey<Fluid> SULFURIC_COPPER_SOLUTION = getFluidTagKey("sulfuric_copper_solution");
    public static final TagKey<Fluid> SULFURIC_NICKEL_SOLUTION = getFluidTagKey("sulfuric_nickel_solution");


    public static final TagKey<Item> ASH = getItemTagKey("dusts", "ash");
    public static final TagKey<Item> DARK_ASH = getItemTagKey("dusts","dark_ash");
    public static final TagKey<Item> STONE_DUST = getItemTagKey("dusts", "stone");
    public static final TagKey<Item> TINY_DARK_ASH = getItemTagKey("tiny_dusts", "dark_ash");
    public static final TagKey<Item> TINY_BONE = getItemTagKey("tiny_dusts", "bone");



    public static final TagKey<Item> COPPER_INGOT = getItemTagKey("ingots", "copper");
    public static final Set<TagKey<Item>> IGNORED_OUTPUT_DUSTS = new HashSet<>();


    static {
        NON_CONSUMED_ITEMS.add(GTItems.PROGRAMMED_CIRCUIT.asItem());
        NON_CONSUMED_ITEMS.add(GTItems.TOOL_DATA_STICK.asItem());
        NON_CONSUMED_ITEMS.add(GTItems.TOOL_DATA_ORB.asItem());
        NON_CONSUMED_ITEMS.add(GTItems.TOOL_DATA_MODULE.asItem());

        // Some indices in shape extruders array are unused
        NON_CONSUMED_ITEMS.addAll(Arrays.stream(GTItems.SHAPE_EXTRUDERS).filter(Objects::nonNull).map(ItemProviderEntry::asItem).toList());
        NON_CONSUMED_ITEMS.addAll(Arrays.stream(GTItems.SHAPE_MOLDS).map(ItemProviderEntry::asItem).toList());
        NON_CONSUMED_ITEMS.addAll(GTItems.GLASS_LENSES.values().stream().map(ItemProviderEntry::asItem).toList());
        TagKey<Item> lenses = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:lenses"));
        NON_CONSUMED_ITEM_TAGS.add(lenses);


        WASTE_FLUID_TAGS.add(WATER);
        WASTE_FLUID_TAGS.add(OXYGEN);
        WASTE_FLUID_TAGS.add(NITROGEN);
        WASTE_FLUID_TAGS.add(HYDROGEN);
        WASTE_FLUID_TAGS.add(NEON);
        WASTE_FLUID_TAGS.add(ARGON);
        WASTE_FLUID_TAGS.add(CHLORINE);
        WASTE_FLUID_TAGS.add(DILUTED_SULFURIC_ACID);
        WASTE_FLUID_TAGS.add(DILUTED_HYDROCHLORIC_ACID);
        WASTE_FLUID_TAGS.add(SULFURIC_ACID);
        WASTE_FLUID_TAGS.add(HYDROCHLORIC_ACID);
        WASTE_FLUID_TAGS.add(SULFURIC_COPPER_SOLUTION);
        WASTE_FLUID_TAGS.add(SULFURIC_NICKEL_SOLUTION);


        WASTE_ITEM_TAGS.add(ASH);
        WASTE_ITEM_TAGS.add(DARK_ASH);
        WASTE_ITEM_TAGS.add(TINY_DARK_ASH);
        WASTE_ITEM_TAGS.add(STONE_DUST);
        WASTE_ITEM_TAGS.add(TINY_BONE);

    }
}
