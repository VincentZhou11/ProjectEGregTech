package com.zhouvincent11.projectegregtech.mapper;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import com.zhouvincent11.projectegregtech.Projectegregtech;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;


@EMCMapper
public class GTCEUModernRecipeMapper implements IEMCMapper<NormalizedSimpleStack, Long> {


    @Override
    public String getName() {
        return "GregTechCEuMapper";
    }

    @Override
    public String getTranslationKey() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Maps EMC values for GregTech CEu recipes. ";
    }

    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector, ReloadableServerResources reloadableServerResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
        Projectegregtech.LOGGER.info("Adding custom mappings");

        GTRecipeProcessor gtRecipeProcessor = new GTRecipeProcessor(iMappingCollector);

        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.WIREMILL_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.LATHE_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.MIXER_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.MACERATOR_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.BENDER_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.FORMING_PRESS_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.EXTRUDER_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.CUTTER_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.BLAST_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.VACUUM_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.FORGE_HAMMER_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.ASSEMBLER_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.CENTRIFUGE_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.LASER_ENGRAVER_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.CHEMICAL_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.CHEMICAL_BATH_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.ELECTROLYZER_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.POLARIZER_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.DISTILLATION_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.FUSION_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.FERMENTING_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.ASSEMBLY_LINE_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.PYROLYSE_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.CRACKING_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.EXTRACTOR_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.POLARIZER_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.AUTOCLAVE_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.LARGE_CHEMICAL_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.ARC_FURNACE_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.COMPRESSOR_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.FLUID_HEATER_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.BREWING_RECIPES);
        gtRecipeProcessor.processGTRecipes(GTRecipeTypes.DISTILLERY_RECIPES);


        Projectegregtech.LOGGER.info("Finished adding custom mappings");

    }

}