package com.zhouvincent11.projectegregtech;

import com.gregtechceu.gtceu.data.fluid.GTFluids;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.gregtechceu.gtceu.data.item.GTMaterialItems;
import com.gregtechceu.gtceu.data.material.GTElements;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.integration.emi.orevein.GTBedrockFluid;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Constants {
    public static final Set<Item> NON_CONSUMED_ITEMS = new HashSet<>();
    public static final Set<TagKey<Item>> NON_CONSUMED_ITEM_TAGS = new HashSet<>();

    public static final Set<TagKey<Fluid>> IGNORED_FLUID_TAGS = new HashSet<>();
    public static final Set<TagKey<Item>> IGNORED_ITEM_TAGS = new HashSet<>();

    public static final TagKey<Item> INGOTS = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots"));
    public static final TagKey<Item> NUGGETS = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets"));
    public static final TagKey<Item> DUSTS = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:dusts"));

    static {
        NON_CONSUMED_ITEMS.add(GTItems.PROGRAMMED_CIRCUIT.asItem());
        NON_CONSUMED_ITEMS.add(GTItems.TOOL_DATA_STICK.asItem());
        NON_CONSUMED_ITEMS.add(GTItems.TOOL_DATA_ORB.asItem());
        NON_CONSUMED_ITEMS.add(GTItems.TOOL_DATA_MODULE.asItem());

        // Some indices in shape extruders array are unused
        NON_CONSUMED_ITEMS.addAll(Arrays.stream(GTItems.SHAPE_EXTRUDERS).filter(Objects::nonNull).map(entry -> entry.asItem()).toList());
        NON_CONSUMED_ITEMS.addAll(Arrays.stream(GTItems.SHAPE_MOLDS).map(entry -> entry.asItem()).toList());
        NON_CONSUMED_ITEMS.addAll(GTItems.GLASS_LENSES.values().stream().map(entry -> entry.asItem()).toList());
//        Projectegregtech.LOGGER.info("Lenses: {}", GTItems.GLASS_LENSES.values().stream().map(Objects::toString).reduce((a, b) -> a+", "+b));

        TagKey<Item> lenses = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:lenses"));
        NON_CONSUMED_ITEM_TAGS.add(lenses);

        TagKey<Fluid> water = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:water"));
        TagKey<Fluid> oxygen = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:oxygen"));
        TagKey<Fluid> nitrogen = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:nitrogen"));
        TagKey<Fluid> hydrogen = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:hydrogen"));
        TagKey<Fluid> neon = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:neon"));
        TagKey<Fluid> argon = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:argon"));
        TagKey<Fluid> chlorine = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:chlorine"));

        TagKey<Fluid> dilutedSulfuricAcid = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:diluted_sulfuric_acid"));
        TagKey<Fluid> dilutedHydrochloricAcid = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:diluted_hydrochloric_acid"));

        TagKey<Fluid> sulfuricAcid = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:sulfuric_acid"));
        TagKey<Fluid> hydrochloricAcid = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:hydrochloric_acid"));



        IGNORED_FLUID_TAGS.add(water);
        IGNORED_FLUID_TAGS.add(oxygen);
        IGNORED_FLUID_TAGS.add(nitrogen);
        IGNORED_FLUID_TAGS.add(hydrogen);
        IGNORED_FLUID_TAGS.add(neon);
        IGNORED_FLUID_TAGS.add(argon);
        IGNORED_FLUID_TAGS.add(chlorine);
        IGNORED_FLUID_TAGS.add(dilutedSulfuricAcid);
        IGNORED_FLUID_TAGS.add(dilutedHydrochloricAcid);
        IGNORED_FLUID_TAGS.add(sulfuricAcid);
        IGNORED_FLUID_TAGS.add(hydrochloricAcid);

        TagKey<Item> ash = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:dusts/ash"));
        TagKey<Item> dark_ash = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:dusts/dark_ash"));
        TagKey<Item> tiny_dark_ash = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:tiny_dusts/dark_ash"));

        IGNORED_ITEM_TAGS.add(ash);
        IGNORED_ITEM_TAGS.add(dark_ash);
        IGNORED_ITEM_TAGS.add(tiny_dark_ash);
    }
}
