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

import static com.zhouvincent11.projectegregtech.mapper.GTCEUModernRecipeMapper.RecipeRelationship.MANY2MANY;
import static com.zhouvincent11.projectegregtech.mapper.GTCEUModernRecipeMapper.RecipeRelationship.MANY2ONE;


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




    private Object2IntMap<NormalizedSimpleStack> applyMultiplier (Object2IntMap<NormalizedSimpleStack> input, double multiplier) {
        Object2IntMap<NormalizedSimpleStack> output = new Object2IntOpenHashMap<>();
        for (NormalizedSimpleStack key : input.keySet()) {
            output.put(key, Math.max((int)(input.getInt(key)*multiplier), 1));
        }
        return output;
    }

    public static int totalRecipeCount = 0;
    public static int many2oneRecipeCount = 0;
    public static Set<GTRecipe> many2oneRecipeMap = new HashSet<>();
    public static int many2manyRecipeCount = 0;
    public static Set<GTRecipe> many2manyRecipeMap = new HashSet<>();



    public enum RecipeRelationship {
        MANY2ONE,
        MANY2MANY
    }

    private RecipeRelationship getRecipeRelationship (GTRecipe recipe) {
        List<Content> itemOutputs = recipe.getOutputContents(ItemRecipeCapability.CAP);
        List<Content> fluidOutputs = recipe.getOutputContents(FluidRecipeCapability.CAP);

        if (itemOutputs.size() + fluidOutputs.size() > 1) {
            return MANY2ONE;
        }
        else {
            return MANY2MANY;
        }
    }


    private void performCounts(GTRecipe recipe) {
        RecipeRelationship relationship = getRecipeRelationship(recipe);

        switch (relationship) {
            case MANY2ONE:
                many2manyRecipeCount++;
                many2manyRecipeMap.add(recipe);
                break;
            case MANY2MANY:
                many2oneRecipeCount++;
                many2oneRecipeMap.add(recipe);
                break;
        }
        totalRecipeCount++;
    }

    private void printStatistics() {
        Projectegregtech.LOGGER.info("Total Recipe Count: {}", totalRecipeCount);
        Projectegregtech.LOGGER.info("Many2One Recipe Count: {}, percentage: {}", many2oneRecipeCount, many2oneRecipeCount * 100.0 / totalRecipeCount);
        Projectegregtech.LOGGER.info("Many2Many Recipe Count: {}, percentage: {}", many2manyRecipeCount, many2manyRecipeCount * 100.0 / totalRecipeCount);

        totalRecipeCount = 0;
        many2oneRecipeCount = 0;
        many2manyRecipeCount = 0;
    }

    private void mapOnlyMany2One(Object2IntMap<NormalizedSimpleStack> inputMap,
                                 Map<NormalizedSimpleStack, Integer> outputMap,
                                 Map<NormalizedSimpleStack, Integer> outputMultiplierMap,
                                 IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector) {
        int totalOutputs = outputMap.size();

        if (totalOutputs == 0) {
//            Projectegregtech.LOGGER.info("No outputs for recipe");
        }
        else if (totalOutputs == 1) {
            NormalizedSimpleStack output = outputMap.keySet().stream().findFirst().get();
            int outputAmount = outputMap.get(output);
            int multiplier = outputMultiplierMap.get(output);


            iMappingCollector.addConversion(outputAmount, output, applyMultiplier(inputMap, multiplier));
        }
        else {
//            Projectegregtech.LOGGER.info("Multiple outputs for recipe");
        }
    }

    private void mapAll (Object2IntMap<NormalizedSimpleStack> inputMap,
                         Map<NormalizedSimpleStack, Integer> outputMap,
                         Map<NormalizedSimpleStack, Integer> outputMultiplierMap,
                         IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector) {
        for (NormalizedSimpleStack output : outputMap.keySet()) {

            int outputAmount = outputMap.get(output);
            int multiplier = outputMultiplierMap.get(output);

            iMappingCollector.addConversion(outputAmount, output, applyMultiplier(inputMap, multiplier));
        }

    }






    public static Set<NormalizedSimpleStack> achievableItems = new HashSet<>();
    public static Set<NormalizedSimpleStack> unachievableItems = new HashSet<>();

    
    
    public class GTRecipeProcessor {
        
        public IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector;

        public GTRecipeProcessor(IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector) {
            this.iMappingCollector = iMappingCollector;
        }

        private void processGTRecipe (GTRecipe recipe,
                                      IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector, boolean verbose) {
            performCounts(recipe);
            GTRecipeParser parser = new GTRecipeParser(recipe);
            if (verbose) {Projectegregtech.LOGGER.info(parser.toString());}
            mapOnlyMany2One(parser.inputs, parser.outputs, parser.outputMultipliers, iMappingCollector);

        }

        public void processGTRecipes (String name, List<RecipeHolder<GTRecipe>> recipeHolders, boolean verbose) {
            Projectegregtech.LOGGER.info("Adding {} recipes", name);

            recipeHolders.stream().forEach(recipe -> {
                processGTRecipe(recipe.value(), this.iMappingCollector, verbose);
            });

            printStatistics();
        }

        public void processGTRecipes (String name, List<RecipeHolder<GTRecipe>> recipeHolders) {
            Projectegregtech.LOGGER.info("Adding {} recipes", name);

            recipeHolders.stream().forEach(recipe -> {
                processGTRecipe(recipe.value(), this.iMappingCollector, false);
            });

            printStatistics();
        }

        private void processVacuumFreezerRecipes(GTRecipe recipe,
                                                 IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector,
                                                 boolean verbose) {
            performCounts(recipe);
            GTRecipeParser parser = new GTRecipeParser(recipe);

            // Gas liquefaction recipes
            if(parser.getFluidInputsSize() == 1 && parser.getFluidOutputsSize() == 1 && parser.getItemInputsSize() == 0 && parser.getItemOutputsSize() == 0) {
                mapOnlyMany2One(parser.fluidInputs, parser.fluidOutputs, parser.outputMultipliers, iMappingCollector);
            }
            // Hot ingot cooling with gas coolant, ignore gas coolant
            else if(parser.getFluidInputsSize() == 1 && parser.getFluidOutputsSize() == 1 && parser.getItemInputsSize() == 1 && parser.getItemOutputsSize() == 1) {
                mapOnlyMany2One(parser.itemInputs, parser.itemOutputs, parser.outputMultipliers, iMappingCollector);
            }
            // Hot ingot cooling
            else if(parser.getFluidInputsSize() == 0 && parser.getFluidOutputsSize() == 0 && parser.getItemInputsSize() == 1 && parser.getItemOutputsSize() == 1) {
                mapOnlyMany2One(parser.itemInputs, parser.itemOutputs, parser.outputMultipliers, iMappingCollector);
            }
            else {
                mapOnlyMany2One(parser.inputs, parser.outputs, parser.outputMultipliers, iMappingCollector);
            }
        }

        public void processVacuumFreezerRecipes (String name, List<RecipeHolder<GTRecipe>> recipeHolders) {
            Projectegregtech.LOGGER.info("Adding {} recipes", name);

            recipeHolders.stream().forEach(recipe -> {
                processVacuumFreezerRecipes(recipe.value(), this.iMappingCollector, false);
            });

            printStatistics();
        }

    }

    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector, ReloadableServerResources reloadableServerResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
        Projectegregtech.LOGGER.info("Adding custom mappings");
//        Projectegregtech.LOGGER.info("{}", GTItems.INTEGRATED_CIRCUIT.asItem());
        RecipeManager mgr = ServerLifecycleHooks.getCurrentServer().getRecipeManager();

        
        
        Set<NormalizedSimpleStack> visitedItems = new HashSet<>(); 
        
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