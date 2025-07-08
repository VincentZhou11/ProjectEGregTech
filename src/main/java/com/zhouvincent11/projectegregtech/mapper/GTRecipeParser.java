package com.zhouvincent11.projectegregtech.mapper;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.fluid.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.zhouvincent11.projectegregtech.Constants;
import com.zhouvincent11.projectegregtech.Projectegregtech;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GTRecipeParser {

    public Object2IntMap<NormalizedSimpleStack> itemInputs;
    public Object2IntMap<NormalizedSimpleStack> fluidInputs;
    public Object2IntMap<NormalizedSimpleStack> inputs;

    public Map<NormalizedSimpleStack, Integer> itemOutputs = new HashMap<>();
    public Map<NormalizedSimpleStack, Integer> fluidOutputs = new HashMap<>();
    public Map<NormalizedSimpleStack, Integer> outputs;

    public Map<NormalizedSimpleStack, Integer> outputMultipliers = new HashMap<>();

    private int getMultiplier (Content output) {
        return Math.ceilDiv(output.maxChance, output.chance);
    }

    public boolean isItemTagMembership(SizedIngredient ingredient, Set<TagKey<Item>> tags) {
        return ingredient.getItems()[0].getTags().anyMatch(tags::contains);
    }

    private void parseItemInputs(GTRecipe recipe) {
        List<Content> contents = recipe.getInputContents(ItemRecipeCapability.CAP);
        contents.stream().forEach(content -> {
            if (content.getContent() instanceof SizedIngredient ingredient) {

                // Don't count empty items and non-consumed items like casts, molds, etc.
                if (ingredient.getItems().length == 0 || Constants.NON_CONSUMED_ITEMS.contains(ingredient.getItems()[0].getItem()) || isItemTagMembership(ingredient, Constants.NON_CONSUMED_ITEM_TAGS)) return;

                itemInputs.put(NSSItem.createItem(ingredient.getItems()[0]), ingredient.count());
                inputs.put(NSSItem.createItem(ingredient.getItems()[0]), ingredient.count());
            }
            else {
                Projectegregtech.LOGGER.info("Non-SizedIngredient Item input: {}", content.getContent());
            }
        });
    }
    private void parseFluidInputs(GTRecipe recipe) {
        List<Content> contents = recipe.getInputContents(FluidRecipeCapability.CAP);
        contents.stream().forEach(content -> {
            if (content.getContent() instanceof SizedFluidIngredient ingredient) {

                if (ingredient.getFluids().length == 0) return;

                fluidInputs.put(NSSFluid.createFluid(ingredient.getFluids()[0]), ingredient.amount());
                inputs.put(NSSFluid.createFluid(ingredient.getFluids()[0]), ingredient.amount());
            }
            else {
                Projectegregtech.LOGGER.info("Non-SizedFluidIngredient Fluid input: {}", content.getContent());
            }
        });
    }

    public void parseItemOutputs(GTRecipe recipe) {
        List<Content> contents = recipe.getOutputContents(ItemRecipeCapability.CAP);
        contents.stream().forEach(content -> {
            if (content.getContent() instanceof SizedIngredient) {
                SizedIngredient ingredient = (SizedIngredient) content.getContent();


                NSSItem nssItem = NSSItem.createItem(ingredient.getItems()[0]);

                itemOutputs.put(nssItem, ingredient.count());
                outputs.put(nssItem, ingredient.count());
                outputMultipliers.put(nssItem, getMultiplier(content));
            }
            else {
                Projectegregtech.LOGGER.info("Non-SizedIngredient Item output: {}", content.getContent());
            }
        });
    }

    public boolean isFluidTagMembership(SizedFluidIngredient ingredient, Set<TagKey<Fluid>> tags) {
        return ingredient.getFluids()[0].getTags().anyMatch(tags::contains);
    }

    public void parseFluidOutputs(GTRecipe recipe) {
        List<Content> contents = recipe.getOutputContents(FluidRecipeCapability.CAP);


        contents.stream().forEach(content -> {
            if (content.getContent() instanceof SizedFluidIngredient) {
                SizedFluidIngredient ingredient = (SizedFluidIngredient) content.getContent();

                if (isFluidTagMembership(ingredient, Constants.IGNORED_FLUID_TAGS)) {
                    return;
                }

                NSSFluid nssFluid = NSSFluid.createFluid(ingredient.getFluids()[0]);

                fluidOutputs.put(nssFluid, ingredient.amount());
                outputs.put(nssFluid, ingredient.amount());
                outputMultipliers.put(nssFluid, getMultiplier(content));
            }
            else {
                Projectegregtech.LOGGER.info("Non-SizedFluidIngredient Fluid output: {}", content.getContent());
            }
        });
    }

    public GTRecipeParser(GTRecipe recipe) {
        this.itemInputs = new Object2IntOpenHashMap<>();
        this.fluidInputs = new Object2IntOpenHashMap<>();
        this.inputs = new Object2IntOpenHashMap<>();
        this.itemOutputs = new HashMap<>();
        this.fluidOutputs = new HashMap<>();
        this.outputs = new HashMap<>();
        this.outputMultipliers = new HashMap<>();
        this.parseItemInputs(recipe);
        this.parseFluidInputs(recipe);
        this.parseItemOutputs(recipe);
        this.parseFluidOutputs(recipe);
    }

    public int getItemInputsSize() {
        return itemInputs.size();
    }

    public int getFluidInputsSize() {
        return fluidInputs.size();
    }

    public int getItemOutputsSize() {
        return itemOutputs.size();
    }

    public int getFluidOutputsSize() {
        return fluidOutputs.size();
    }

    public int getInputsSize() {
        return inputs.size();
    }

    public Object2IntMap<NormalizedSimpleStack> getInputs() {
        return inputs;
    }

    public int getOutputsSize() {
        return outputs.size();
    }

    public Map<NormalizedSimpleStack, Integer> getOutputs() {
        return outputs;
    }

    public Map<NormalizedSimpleStack, Integer> getOutputMultipliers() {
        return outputMultipliers;
    }

    public String toString() {


        StringBuilder builder = new StringBuilder();
        if (getOutputsSize() > 1) builder.append("Multiple Outputs: ");
        builder.append("GTRecipeParser[");
        builder.append("Item Inputs:");
        itemInputs.forEach((key, value) -> builder.append(key + " x " + value + ","));
        builder.append(", Fluid Inputs:");
        fluidInputs.forEach((key, value) -> builder.append(key + " x " + value + ","));
        builder.append(", Item Outputs:");
        itemOutputs.forEach((key, value) -> builder.append(key + " x " + value + ","));
        builder.append(", Fluid Outputs:");
        fluidOutputs.forEach((key, value) -> builder.append(key + " x " + value + ","));
        builder.append(", Output Multipliers:");
        outputMultipliers.forEach((key, value) -> builder.append(key + " x " + value + ","));
        builder.append("]");
        return builder.toString();
    }
}
