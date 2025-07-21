package com.zhouvincent11.projectegregtech.mapper;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import com.zhouvincent11.projectegregtech.Filters;
import com.zhouvincent11.projectegregtech.Projectegregtech;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.function.BiConsumer;

public class GTRecipeProcessor {

    public final IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector;
    public final RecipeManager recipeManager;

    public GTRecipeProcessor(IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector) {
        this.iMappingCollector = iMappingCollector;
        this.recipeManager = ServerLifecycleHooks.getCurrentServer().getRecipeManager();
        Projectegregtech.LOGGER.info("GTRecipeProcessor initialized");
    }

    private Object2IntMap<NormalizedSimpleStack> applyMultiplier (Object2IntMap<NormalizedSimpleStack> input, double multiplier) {
        Object2IntMap<NormalizedSimpleStack> output = new Object2IntOpenHashMap<>();
        for (NormalizedSimpleStack key : input.keySet()) {
            output.put(key, Math.max((int)(input.getInt(key)*multiplier), 1));
        }
        return output;
    }

    private void mapOnlyMany2One(Object2IntMap<NormalizedSimpleStack> inputMap,
                                 Map<NormalizedSimpleStack, Integer> outputMap,
                                 Map<NormalizedSimpleStack, Integer> outputMultiplierMap,
                                 boolean verbose) {
        int totalOutputs = outputMap.size();
        int totalInputs = inputMap.size();

        if (totalInputs == 0) {
            if(verbose) Projectegregtech.LOGGER.info("No inputs for recipe, ignoring");
        }
        else if (totalOutputs == 0) {
            if(verbose) Projectegregtech.LOGGER.info("No outputs for recipe, ignoring");
        }
        else if (totalOutputs == 1) {
            NormalizedSimpleStack output = outputMap.keySet().stream().findFirst().get();
            int outputAmount = outputMap.get(output);
            int multiplier = outputMultiplierMap.get(output);
            this.iMappingCollector.addConversion(outputAmount, output, applyMultiplier(inputMap, multiplier));
            if (verbose) Projectegregtech.LOGGER.info("Mapped {} to {}x{}", inputMap, outputAmount, output);
        }
        else {
            if(verbose) Projectegregtech.LOGGER.info("Multiple outputs for recipe, ignoring");
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

    private void processGTRecipe (GTRecipe recipe, boolean verbose) {
        GTRecipeParser parser = new GTRecipeParser(recipe);
        if (verbose) Projectegregtech.LOGGER.info(parser.toString());
        mapOnlyMany2One(parser.getFilteredInputs(), parser.getFilteredOutputs(), parser.getOutputMultipliers(), verbose);

    }

    public void processGTRecipes (GTRecipeType type, boolean verbose) {
        Projectegregtech.LOGGER.info("Adding {} recipes", type.getName());


        BiConsumer<GTRecipe, Boolean> consumer;
        if (type == GTRecipeTypes.VACUUM_RECIPES) {
            consumer = this::processVacuumFreezerRecipe;
        }
        else if (type == GTRecipeTypes.MACERATOR_RECIPES) {
            consumer = this::processMaceratorRecipe;
        }
//        else if (type == GTRecipeTypes.THERMAL_CENTRIFUGE_RECIPES) {
//            consumer = this::processThermalCentrifugeRecipe;
//        }
//        else if (type == GTRecipeTypes.ORE_WASHER_RECIPES) {
//            consumer = this::processOreWasherRecipe;
//        }
        else {
            consumer = this::processGTRecipe;
        }

        this.recipeManager.getAllRecipesFor(type).stream().forEach(recipe -> {
            consumer.accept(recipe.value(), verbose);
        });

    }

    public void processGTRecipes (GTRecipeType type) {
        processGTRecipes(type, false);
    }

    private void processVacuumFreezerRecipe(GTRecipe recipe, boolean verbose) {
        GTRecipeParser parser = new GTRecipeParser(recipe);
        if (verbose) {Projectegregtech.LOGGER.info(parser.toString());}

        // Gas liquefaction recipes
        if(parser.getFluidInputsSize() == 1 && parser.getFluidOutputsSize() == 1 && parser.getItemInputsSize() == 0 && parser.getItemOutputsSize() == 0) {
            mapOnlyMany2One(parser.getFilteredFluidInputs(), parser.getFilteredFluidOutputs(), parser.getOutputMultipliers(), verbose);
        }
        // Hot ingot cooling with gas coolant, ignore gas coolant
        else if(parser.getFluidInputsSize() == 1 && parser.getFluidOutputsSize() == 1 && parser.getItemInputsSize() == 1 && parser.getItemOutputsSize() == 1) {
            mapOnlyMany2One(parser.getFilteredItemInputs(), parser.getFilteredItemOutputs(), parser.getOutputMultipliers(), verbose);
        }
        // Hot ingot cooling
        else if(parser.getFluidInputsSize() == 0 && parser.getFluidOutputsSize() == 0 && parser.getItemInputsSize() == 1 && parser.getItemOutputsSize() == 1) {
            mapOnlyMany2One(parser.getFilteredItemInputs(), parser.getFilteredItemOutputs(), parser.getOutputMultipliers(), verbose);
        }
        else {
            mapOnlyMany2One(parser.getFilteredInputs(), parser.getFilteredOutputs(), parser.getOutputMultipliers(), verbose);
        }
    }

    private void processMaceratorRecipe(GTRecipe recipe, boolean verbose) {
        GTRecipeParser parser = new GTRecipeParser(recipe,
                Filters.MACERATOR_ITEM_INPUT_FILTER,
                null,
                Filters.MACERATOR_ITEM_OUTPUT_FILTER,
                null);
        if (verbose) {Projectegregtech.LOGGER.info(parser.toString());}
        mapOnlyMany2One(parser.getFilteredInputs(), parser.getFilteredOutputs(), parser.getOutputMultipliers(), verbose);
    }


    // We ignore the part that crushed ore turns to refined ore. We only want to assign values to the chance oupts
    private void processThermalCentrifugeRecipe(GTRecipe recipe, boolean verbose) {
        GTRecipeParser parser = new GTRecipeParser(recipe, null, null,
                Filters.ORE_BYPRODUCT_ITEM_OUTPUT_FILTER, null);
        if (verbose) {Projectegregtech.LOGGER.info(parser.toString());}
        mapOnlyMany2One(parser.getFilteredInputs(), parser.getFilteredOutputs(), parser.getOutputMultipliers(), verbose);
    }

    private void processOreWasherRecipe(GTRecipe recipe, boolean verbose) {
        GTRecipeParser parser = new GTRecipeParser(recipe,
                null,
                Filters.IGNORE_ALL_FLUIDS_FILTER,
                Filters.ORE_BYPRODUCT_ITEM_OUTPUT_FILTER,
                null);
        if (verbose) {Projectegregtech.LOGGER.info(parser.toString());}
    }

}
