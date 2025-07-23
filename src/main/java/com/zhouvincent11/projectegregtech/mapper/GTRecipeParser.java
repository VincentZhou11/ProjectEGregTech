package com.zhouvincent11.projectegregtech.mapper;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.zhouvincent11.projectegregtech.Filters;
import com.zhouvincent11.projectegregtech.Projectegregtech;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class GTRecipeParser {

    private final Map<ItemStack, Integer> itemInputs;
    private final Map<FluidStack, Integer> fluidInputs;

    private final Predicate<ItemStack> itemInputPredicate;
    private final Predicate<FluidStack> fluidInputPredicate;

    private final Map<ItemStack, Integer> itemOutputs;
    private final Map<FluidStack, Integer> fluidOutputs;

    private final Predicate<ItemStack> itemOutputPredicate;
    private final Predicate<FluidStack> fluidOutputPredicate;


    private Map<NormalizedSimpleStack, Integer> outputMultipliers;

    private int getMultiplier (Content output) {
        return Math.ceilDiv(output.maxChance, output.chance);
    }

    private void parseItemInputs(GTRecipe recipe) {
        List<Content> contents = recipe.getInputContents(ItemRecipeCapability.CAP);
        contents.stream().forEach(content -> {
            if (content.getContent() instanceof SizedIngredient ingredient) {
                // Don't add empty items
                if (ingredient.getItems().length == 0) return;
                itemInputs.put(ingredient.getItems()[0], ingredient.count());
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
                // Don't add empty fluids
                if (ingredient.getFluids().length == 0) return;
                fluidInputs.put(ingredient.getFluids()[0], ingredient.amount());
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
                itemOutputs.put(ingredient.getItems()[0], ingredient.count());
                outputMultipliers.put(NSSItem.createItem(ingredient.getItems()[0]), getMultiplier(content));
            }
            else {
                Projectegregtech.LOGGER.info("Non-SizedIngredient Item output: {}", content.getContent());
            }
        });
    }



    public void parseFluidOutputs(GTRecipe recipe) {
        List<Content> contents = recipe.getOutputContents(FluidRecipeCapability.CAP);

        contents.stream().forEach(content -> {
            if (content.getContent() instanceof SizedFluidIngredient) {
                SizedFluidIngredient ingredient = (SizedFluidIngredient) content.getContent();
                fluidOutputs.put(ingredient.getFluids()[0], ingredient.amount());
                outputMultipliers.put(NSSFluid.createFluid(ingredient.getFluids()[0]), getMultiplier(content));
            }
            else {
                Projectegregtech.LOGGER.info("Non-SizedFluidIngredient Fluid output: {}", content.getContent());
            }
        });

    }

    public boolean isItemTagMember(ItemStack itemsStack, Set<TagKey<Item>> tags) {
        return itemsStack.getTags().anyMatch(tags::contains);
    }

    public boolean isFluidTagMember(FluidStack fluidStack, Set<TagKey<Fluid>> tags) {
        return fluidStack.getTags().anyMatch(tags::contains);
    }

    public Object2IntMap<NormalizedSimpleStack> getFilteredItemInputs() {
        Object2IntMap<NormalizedSimpleStack> filteredItemInputs = new Object2IntOpenHashMap<>();
        this.itemInputs.keySet().stream()
                .filter(this.itemInputPredicate)
                .forEach(itemStack -> {
                    filteredItemInputs.put(NSSItem.createItem(itemStack), this.itemInputs.get(itemStack));
                });
        return filteredItemInputs;
    }

    public Object2IntMap<NormalizedSimpleStack> getFilteredFluidInputs() {
        Object2IntMap<NormalizedSimpleStack> filteredFluidInputs = new Object2IntOpenHashMap<>();
        this.fluidInputs.keySet().stream()
                .filter(this.fluidInputPredicate)
                .forEach(fluidStack -> {
                    filteredFluidInputs.put(NSSFluid.createFluid(fluidStack), this.fluidInputs.get(fluidStack));
                });
        return filteredFluidInputs;
    }

    public Object2IntMap<NormalizedSimpleStack> getFilteredInputs() {
        Object2IntMap<NormalizedSimpleStack> filteredInputs= new Object2IntOpenHashMap<>();
        filteredInputs.putAll(this.getFilteredItemInputs());
        filteredInputs.putAll(this.getFilteredFluidInputs());
        return filteredInputs;
    }

    public Map<NormalizedSimpleStack, Integer> getFilteredItemOutputs() {
        Map<NormalizedSimpleStack, Integer> filterItemOutputs = new HashMap<>();

        // If recipe produces only one item with no fluid, no filtering required
        if (this.itemOutputs.size() == 1 && this.fluidOutputs.isEmpty()) {
            ItemStack itemOutput = this.itemOutputs.keySet().stream().findFirst().get();
            filterItemOutputs.put(NSSItem.createItem(itemOutput), this.itemOutputs.get(itemOutput));
        }
        // Else, filter items
        else  {
            this.itemOutputs.keySet().stream()
                    .filter(this.itemOutputPredicate)
                    .forEach(itemStack -> {
                filterItemOutputs.put(NSSItem.createItem(itemStack), this.itemOutputs.get(itemStack));
            });
        }
        return filterItemOutputs;
    }

    public Map<NormalizedSimpleStack, Integer> getFilteredFluidOutputs() {
        Map<NormalizedSimpleStack, Integer> filterFluidOutputs = new HashMap<>();

        // If recipe produces only one fluid with no items, no filtering required
        if (this.fluidOutputs.size() == 1 && this.itemOutputs.isEmpty()) {
            FluidStack fluidOutput = this.fluidOutputs.keySet().stream().findFirst().get();
            filterFluidOutputs.put(NSSFluid.createFluid(fluidOutput), this.fluidOutputs.get(fluidOutput));
        }
        // Else, filter fluids
        else  {
            this.fluidOutputs.keySet().stream()
                    .filter(this.fluidOutputPredicate)
                    .forEach(fluid -> {
                filterFluidOutputs.put(NSSFluid.createFluid(fluid), this.fluidOutputs.get(fluid));
            });
        }
        return filterFluidOutputs;
    }

    public Map<NormalizedSimpleStack, Integer> getFilteredOutputs() {
        Map<NormalizedSimpleStack, Integer> filteredOutputs = new HashMap<>();
        filteredOutputs.putAll(this.getFilteredItemOutputs());
        filteredOutputs.putAll(this.getFilteredFluidOutputs());
        return filteredOutputs;
    }

    public GTRecipeParser(GTRecipe recipe) {
        this(recipe, null, null, null, null);
    }

    public GTRecipeParser(GTRecipe recipe,
                          Predicate<ItemStack> itemInputPredicate,
                          Predicate<FluidStack> fluidInputPredicate,
                          Predicate<ItemStack> itemOutputPredicate,
                          Predicate<FluidStack> fluidOutputPredicate) {
        this.itemInputs = new HashMap<>();
        this.fluidInputs = new HashMap<>();
        this.itemOutputs = new HashMap<>();
        this.fluidOutputs = new HashMap<>();
        this.outputMultipliers = new HashMap<>();

        if (itemInputPredicate == null) itemInputPredicate = Filters.DEFAULT_ITEM_INPUT_FILTER;
        this.itemInputPredicate = itemInputPredicate;
        if (fluidInputPredicate == null) fluidInputPredicate = Filters.DEFAULT_FLUID_INPUT_FILTER;
        this.fluidInputPredicate = fluidInputPredicate;
        if (itemOutputPredicate == null) itemOutputPredicate = Filters.DEFAULT_ITEM_OUTPUT_FILTER;
        this.itemOutputPredicate = itemOutputPredicate;
        if (fluidOutputPredicate == null) fluidOutputPredicate = Filters.DEFAULT_FLUID_OUTPUT_FILTER;
        this.fluidOutputPredicate = fluidOutputPredicate;

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

    public Map<NormalizedSimpleStack, Integer> getOutputMultipliers() {
        return outputMultipliers;
    }

    public String toString() {


        StringBuilder builder = new StringBuilder();
        if (getFilteredOutputs().size() > 1) builder.append("Multiple Outputs: ");
        builder.append("GTRecipeParser[");
        builder.append("Item Inputs:");
        getFilteredItemInputs().forEach((key, value) -> builder.append(key + " x " + value + ","));
        builder.append("Fluid Inputs:");
        getFilteredFluidInputs().forEach((key, value) -> builder.append(key + " x " + value + ","));
        builder.append("Item Outputs:");
        getFilteredItemOutputs().forEach((key, value) -> builder.append(key + " x " + value + ","));
        builder.append("Fluid Outputs:");
        getFilteredFluidOutputs().forEach((key, value) -> builder.append(key + " x " + value + ","));
        builder.append("Output Multipliers:");
        outputMultipliers.forEach((key, value) -> builder.append(key + " x " + value + ","));
        builder.append("]");
        return builder.toString();
    }
}
