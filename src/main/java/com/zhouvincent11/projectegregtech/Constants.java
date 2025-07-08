package com.zhouvincent11.projectegregtech;

import com.gregtechceu.gtceu.data.fluid.GTFluids;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.gregtechceu.gtceu.data.material.GTElements;
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

    public static final Set<Fluid> IGNORED_FLUIDS = new HashSet<>();

    public static final Set<TagKey<Fluid>> IGNORED_FLUID_TAGS = new HashSet<>();

    static {
        NON_CONSUMED_ITEMS.add(GTItems.INTEGRATED_CIRCUIT.asItem());
        NON_CONSUMED_ITEMS.add(GTItems.TOOL_DATA_STICK.asItem());
        NON_CONSUMED_ITEMS.add(GTItems.TOOL_DATA_ORB.asItem());
        NON_CONSUMED_ITEMS.add(GTItems.TOOL_DATA_MODULE.asItem());

        // Some indices in shape extruders array are unused
        NON_CONSUMED_ITEMS.addAll(Arrays.stream(GTItems.SHAPE_EXTRUDERS).filter(Objects::nonNull).map(entry -> entry.asItem()).toList());
        NON_CONSUMED_ITEMS.addAll(Arrays.stream(GTItems.SHAPE_MOLDS).map(entry -> entry.asItem()).toList());
        NON_CONSUMED_ITEMS.addAll(GTItems.GLASS_LENSES.values().stream().map(entry -> entry.asItem()).toList());
        Projectegregtech.LOGGER.info("Lenses: {}", GTItems.GLASS_LENSES.values().stream().map(Objects::toString).reduce((a, b) -> a+", "+b));

        TagKey<Item> lenses = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:lenses"));
        NON_CONSUMED_ITEM_TAGS.add(lenses);

        IGNORED_FLUIDS.add(net.minecraft.world.level.material.Fluids.WATER);

        TagKey<Fluid> oxygen = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:oxygen"));
        TagKey<Fluid> nitrogen = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:nitrogen"));
        TagKey<Fluid> hydrogen = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:hydrogen"));
        TagKey<Fluid> neon = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:neon"));
        TagKey<Fluid> argon = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:argon"));
        TagKey<Fluid> chlorine = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:chlorine"));



        IGNORED_FLUID_TAGS.add(oxygen);
        IGNORED_FLUID_TAGS.add(nitrogen);
        IGNORED_FLUID_TAGS.add(hydrogen);
        IGNORED_FLUID_TAGS.add(neon);
        IGNORED_FLUID_TAGS.add(argon);
        IGNORED_FLUID_TAGS.add(chlorine);
    }
}
