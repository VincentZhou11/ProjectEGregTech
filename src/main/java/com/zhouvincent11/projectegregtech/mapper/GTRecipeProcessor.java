package com.zhouvincent11.projectegregtech.mapper;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.zhouvincent11.projectegregtech.Projectegregtech;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Map;

public class GTRecipeProcessor {

    public IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector;

    public GTRecipeProcessor(IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector) {
        this.iMappingCollector = iMappingCollector;
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

    private void processGTRecipe (GTRecipe recipe,
                                  IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector, boolean verbose) {
        GTRecipeParser parser = new GTRecipeParser(recipe);
        if (verbose) {
            Projectegregtech.LOGGER.info(parser.toString());}
        mapOnlyMany2One(parser.inputs, parser.outputs, parser.outputMultipliers, iMappingCollector);

    }

    public void processGTRecipes (String name, List<RecipeHolder<GTRecipe>> recipeHolders, boolean verbose) {
        Projectegregtech.LOGGER.info("Adding {} recipes", name);

        recipeHolders.stream().forEach(recipe -> {
            processGTRecipe(recipe.value(), this.iMappingCollector, verbose);
        });

    }

    public void processGTRecipes (String name, List<RecipeHolder<GTRecipe>> recipeHolders) {
        Projectegregtech.LOGGER.info("Adding {} recipes", name);

        recipeHolders.stream().forEach(recipe -> {
            processGTRecipe(recipe.value(), this.iMappingCollector, false);
        });

    }

    private void processVacuumFreezerRecipe(GTRecipe recipe,
                                            IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector,
                                            boolean verbose) {
        GTRecipeParser parser = new GTRecipeParser(recipe);
        if (verbose) {Projectegregtech.LOGGER.info(parser.toString());}

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
            processVacuumFreezerRecipe(recipe.value(), this.iMappingCollector, false);
        });

    }

}
