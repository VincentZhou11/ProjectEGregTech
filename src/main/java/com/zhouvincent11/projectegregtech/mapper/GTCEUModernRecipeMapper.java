package com.zhouvincent11.projectegregtech.mapper;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
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
//        Projectegregtech.LOGGER.info("{}", GTItems.INTEGRATED_CIRCUIT.asItem());
        RecipeManager mgr = ServerLifecycleHooks.getCurrentServer().getRecipeManager();

        GTRecipeProcessor gtRecipeProcessor = new GTRecipeProcessor(iMappingCollector);

        gtRecipeProcessor.processGTRecipes("Wiremill", mgr.getAllRecipesFor(GTRecipeTypes.WIREMILL_RECIPES));
        gtRecipeProcessor.processGTRecipes("Lathe",mgr.getAllRecipesFor(GTRecipeTypes.LATHE_RECIPES));
        gtRecipeProcessor.processGTRecipes("Mixer",mgr.getAllRecipesFor(GTRecipeTypes.MIXER_RECIPES));
        gtRecipeProcessor.processGTRecipes("Cutter",mgr.getAllRecipesFor(GTRecipeTypes.CUTTER_RECIPES));
        gtRecipeProcessor.processGTRecipes("Extruder",mgr.getAllRecipesFor(GTRecipeTypes.EXTRUDER_RECIPES));
        gtRecipeProcessor.processGTRecipes("Fluid Solidifier",mgr.getAllRecipesFor(GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES));
        gtRecipeProcessor.processGTRecipes("Macerator",mgr.getAllRecipesFor(GTRecipeTypes.MACERATOR_RECIPES));
        gtRecipeProcessor.processGTRecipes("Bender",mgr.getAllRecipesFor(GTRecipeTypes.BENDER_RECIPES));
        gtRecipeProcessor.processGTRecipes("Forming Press",mgr.getAllRecipesFor(GTRecipeTypes.FORMING_PRESS_RECIPES));
        gtRecipeProcessor.processGTRecipes("Forge Hammer",mgr.getAllRecipesFor(GTRecipeTypes.FORGE_HAMMER_RECIPES));
        gtRecipeProcessor.processGTRecipes("Assembler",mgr.getAllRecipesFor(GTRecipeTypes.ASSEMBLER_RECIPES));
        gtRecipeProcessor.processGTRecipes("CircuitAssembler",mgr.getAllRecipesFor(GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES));
//        gtRecipeProcessor.processGTRecipes("Centrifuge",mgr.getAllRecipesFor(GTRecipeTypes.CENTRIFUGE_RECIPES));
        gtRecipeProcessor.processGTRecipes("Electric Blast Furnace",mgr.getAllRecipesFor(GTRecipeTypes.BLAST_RECIPES));
        gtRecipeProcessor.processGTRecipes("Alloy Blast Smelter",mgr.getAllRecipesFor(GTRecipeTypes.ALLOY_SMELTER_RECIPES));
        gtRecipeProcessor.processGTRecipes("Laser Engraver",mgr.getAllRecipesFor(GTRecipeTypes.LASER_ENGRAVER_RECIPES));
        gtRecipeProcessor.processGTRecipes("Chemical Reactor",mgr.getAllRecipesFor(GTRecipeTypes.CHEMICAL_RECIPES), true);
        gtRecipeProcessor.processGTRecipes("Chemical Bath",mgr.getAllRecipesFor(GTRecipeTypes.CHEMICAL_BATH_RECIPES));
        gtRecipeProcessor.processGTRecipes("Electrolyzer",mgr.getAllRecipesFor(GTRecipeTypes.ELECTROLYZER_RECIPES));
        gtRecipeProcessor.processGTRecipes("Polarizer",mgr.getAllRecipesFor(GTRecipeTypes.POLARIZER_RECIPES));
        gtRecipeProcessor.processGTRecipes("Distillation Tower",mgr.getAllRecipesFor(GTRecipeTypes.DISTILLATION_RECIPES));
        gtRecipeProcessor.processGTRecipes("Fusion Reactor",mgr.getAllRecipesFor(GTRecipeTypes.FUSION_RECIPES));
        gtRecipeProcessor.processGTRecipes("Fermenting",mgr.getAllRecipesFor(GTRecipeTypes.FERMENTING_RECIPES));
        gtRecipeProcessor.processGTRecipes("Assembly Line",mgr.getAllRecipesFor(GTRecipeTypes.ASSEMBLY_LINE_RECIPES));
        gtRecipeProcessor.processGTRecipes("Pyrolyse Oven",mgr.getAllRecipesFor(GTRecipeTypes.PYROLYSE_RECIPES));
        gtRecipeProcessor.processGTRecipes("Cracking", mgr.getAllRecipesFor(GTRecipeTypes.CRACKING_RECIPES));
        gtRecipeProcessor.processVacuumFreezerRecipes("Vacuum Freezer", mgr.getAllRecipesFor(GTRecipeTypes.VACUUM_RECIPES));
        gtRecipeProcessor.processGTRecipes("Extractor", mgr.getAllRecipesFor(GTRecipeTypes.EXTRACTOR_RECIPES));
        gtRecipeProcessor.processGTRecipes("Polarizer", mgr.getAllRecipesFor(GTRecipeTypes.POLARIZER_RECIPES));
        gtRecipeProcessor.processGTRecipes("Autoclave", mgr.getAllRecipesFor(GTRecipeTypes.AUTOCLAVE_RECIPES));
        gtRecipeProcessor.processGTRecipes("Large Chemical Reactor", mgr.getAllRecipesFor(GTRecipeTypes.LARGE_CHEMICAL_RECIPES));
        gtRecipeProcessor.processGTRecipes("Arc Furnace", mgr.getAllRecipesFor(GTRecipeTypes.ARC_FURNACE_RECIPES));
        gtRecipeProcessor.processGTRecipes("Compresser", mgr.getAllRecipesFor(GTRecipeTypes.COMPRESSOR_RECIPES));
        gtRecipeProcessor.processGTRecipes("Fluid Heater", mgr.getAllRecipesFor(GTRecipeTypes.FLUID_HEATER_RECIPES));

        gtRecipeProcessor.processGTRecipes("Brewing", mgr.getAllRecipesFor(GTRecipeTypes.BREWING_RECIPES), true);
        gtRecipeProcessor.processGTRecipes("Distillery", mgr.getAllRecipesFor(GTRecipeTypes.DISTILLERY_RECIPES), true);


        Projectegregtech.LOGGER.info("Finished adding custom mappings");

    }

}