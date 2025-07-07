package com.zhouvincent11.projectegregtech.mapper;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ui.GTRecipeTypeUI;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.lowdragmc.lowdraglib.utils.XmlUtils;
import com.mojang.logging.LogUtils;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.zhouvincent11.projectegregtech.Projectegregtech;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static com.zhouvincent11.projectegregtech.mapper.GTCEUModernRecipeMapper.RecipeRelationship.MANY2MANY;
import static com.zhouvincent11.projectegregtech.mapper.GTCEUModernRecipeMapper.RecipeRelationship.MANY2ONE;


@EMCMapper
public class GTCEUModernRecipeMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

    public static final Set<Item> NON_CONSUMED_ITEMS = new HashSet<>();

    static {
        NON_CONSUMED_ITEMS.add(GTItems.INTEGRATED_CIRCUIT.asItem());
        NON_CONSUMED_ITEMS.add(GTItems.TOOL_DATA_STICK.asItem());
        NON_CONSUMED_ITEMS.add(GTItems.TOOL_DATA_ORB.asItem());
        NON_CONSUMED_ITEMS.add(GTItems.TOOL_DATA_MODULE.asItem());

        // Some indices in shape extruders array are unused
        NON_CONSUMED_ITEMS.addAll(Arrays.stream(GTItems.SHAPE_EXTRUDERS).filter(Objects::nonNull).map(entry -> entry.asItem()).toList());
        NON_CONSUMED_ITEMS.addAll(Arrays.stream(GTItems.SHAPE_MOLDS).map(entry -> entry.asItem()).toList());
        NON_CONSUMED_ITEMS.addAll(GTItems.GLASS_LENSES.values().stream().map(entry -> entry.asItem()).toList());

    }

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


    private int getMultiplier (Content output) {
        return Math.ceilDiv(output.maxChance, output.chance);
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
    }

    private void processGTRecipe (GTRecipe recipe, IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector, Set<NormalizedSimpleStack> visitedItems) {

    }

    private void processGTRecipes (String name, List<RecipeHolder<GTRecipe>> recipeHolders, IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector, Set<NormalizedSimpleStack> visitedItems) {
        Projectegregtech.LOGGER.info("Adding {} recipes", name);

        recipeHolders.stream().forEach(recipe -> {
            performCounts(recipe.value());

            List<Content> itemInputs = recipe.value().getInputContents(ItemRecipeCapability.CAP);
            List<Content> fluidInputs = recipe.value().getInputContents(FluidRecipeCapability.CAP);

            List<Content> itemOutputs = recipe.value().getOutputContents(ItemRecipeCapability.CAP);
            List<Content> fluidOutputs = recipe.value().getOutputContents(FluidRecipeCapability.CAP);

            Object2IntMap<NormalizedSimpleStack> inputMap = new Object2IntOpenHashMap<>();

            itemInputs.stream().forEach(content -> {
                if (content.getContent() instanceof SizedIngredient) {
                    SizedIngredient ingredient = (SizedIngredient) content.getContent();

                    if (ingredient.getItems().length <=0 || NON_CONSUMED_ITEMS.contains(ingredient.getItems()[0].getItem())) return;


                    inputMap.put(NSSItem.createItem(ingredient.getItems()[0]), ingredient.count());
                }
                else {
                    Projectegregtech.LOGGER.info("Non-SizedIngredient Item input: {}", content.getContent());
                }
            });

            fluidInputs.stream().forEach(content -> {
                if (content.getContent() instanceof SizedFluidIngredient) {
                    SizedFluidIngredient ingredient = (SizedFluidIngredient) content.getContent();

                    if (ingredient.getFluids().length <=0) return;

                    inputMap.put(NSSFluid.createFluid(ingredient.getFluids()[0]), ingredient.amount());
                }
                else {
                    Projectegregtech.LOGGER.info("Non-SizedFluidIngredient Fluid input: {}", content.getContent());
                }
            });

            Map<NormalizedSimpleStack, Integer> outputMap = new HashMap<>();
            Map<NormalizedSimpleStack, Integer> outputMultiplierMap = new HashMap<>();

            itemOutputs.stream().forEach(content -> {
                if (content.getContent() instanceof SizedIngredient) {
                    SizedIngredient ingredient = (SizedIngredient) content.getContent();


                    NSSItem nssItem = NSSItem.createItem(ingredient.getItems()[0]);

                    outputMap.put(nssItem, ingredient.count());
                    outputMultiplierMap.put(nssItem, getMultiplier(content));
                }
                else {
                    Projectegregtech.LOGGER.info("Non-SizedIngredient Item output: {}", content.getContent());
                }
            });

            fluidOutputs.stream().forEach(content -> {
                if (content.getContent() instanceof SizedFluidIngredient) {
                    SizedFluidIngredient ingredient = (SizedFluidIngredient) content.getContent();

                    NSSFluid nssFluid = NSSFluid.createFluid(ingredient.getFluids()[0]);

                    outputMultiplierMap.put(nssFluid, getMultiplier(content));
                    outputMap.put(nssFluid, ingredient.amount());
                }
                else {
                    Projectegregtech.LOGGER.info("Non-SizedFluidIngredient Fluid output: {}", content.getContent());
                }
            });

//            Projectegregtech.LOGGER.info("{} Recipe - Inputs: {} | Outputs: {}", name, inputMap.keySet().stream().map(NormalizedSimpleStack::toString).reduce((a,b) -> a + ", " + b).orElse(""), outputMap.keySet().stream().map(NormalizedSimpleStack::toString).reduce((a,b) -> a + ", " + b).orElse(""));

            int totalOutputs = outputMap.size();



            for (NormalizedSimpleStack output : outputMap.keySet()) {

                if (visitedItems.contains(output)) {
                    continue;
                }

                int outputAmount = outputMap.get(output);
                int multiplier = outputMultiplierMap.get(output);

//                visitedItems.add(output);

                iMappingCollector.addConversion(outputAmount, output, applyMultiplier(inputMap, multiplier));
            }
            // Parameters: outputAmount, outputItem as NormalizedSimpleStack, input ingredient as mapping

        });

        printStatistics();
    }

    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector, ReloadableServerResources reloadableServerResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
        Projectegregtech.LOGGER.info("Adding custom mappings");
//        Projectegregtech.LOGGER.info("{}", GTItems.INTEGRATED_CIRCUIT.asItem());
        RecipeManager mgr = ServerLifecycleHooks.getCurrentServer().getRecipeManager();

        
        
        Set<NormalizedSimpleStack> visitedItems = new HashSet<>(); 
        
//        int counter = 0;
//
//        Projectegregtech.LOGGER.info("Adding extruder shapes to blacklist");
//        for (ItemEntry<Item> entry: GTItems.SHAPE_EXTRUDERS) {
//            if (entry == null) {
//                Projectegregtech.LOGGER.info("Null entry at index {}", counter);
//            }
//            else {
//                NON_CONSUMED_ITEMS.add(entry.asItem());
//            }
//            counter++;
//        }
//        Projectegregtech.LOGGER.info("Adding molds to blacklist");
//        counter = 0;
//        for (ItemEntry<Item> entry: GTItems.SHAPE_MOLDS) {
//            if (entry == null) {
//                Projectegregtech.LOGGER.info("Null entry at index {}", counter);
//            }
//            else {
//                NON_CONSUMED_ITEMS.add(entry.asItem());
//            }
//            counter++;
//        }


        processGTRecipes("Wiremill", mgr.getAllRecipesFor(GTRecipeTypes.WIREMILL_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Lathe",mgr.getAllRecipesFor(GTRecipeTypes.LATHE_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Mixer",mgr.getAllRecipesFor(GTRecipeTypes.MIXER_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Cutter",mgr.getAllRecipesFor(GTRecipeTypes.CUTTER_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Extruder",mgr.getAllRecipesFor(GTRecipeTypes.EXTRUDER_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Fluid Solidifier",mgr.getAllRecipesFor(GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Macerator",mgr.getAllRecipesFor(GTRecipeTypes.MACERATOR_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Bender",mgr.getAllRecipesFor(GTRecipeTypes.BENDER_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Forming Press",mgr.getAllRecipesFor(GTRecipeTypes.FORMING_PRESS_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Forge Hammer",mgr.getAllRecipesFor(GTRecipeTypes.FORGE_HAMMER_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Assembler",mgr.getAllRecipesFor(GTRecipeTypes.ASSEMBLER_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("CircuitAssembler",mgr.getAllRecipesFor(GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES), iMappingCollector, visitedItems);
//        processGTRecipes("Centrifuge",mgr.getAllRecipesFor(GTRecipeTypes.CENTRIFUGE_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Electric Blast Furnace",mgr.getAllRecipesFor(GTRecipeTypes.BLAST_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Laser Engraver",mgr.getAllRecipesFor(GTRecipeTypes.LASER_ENGRAVER_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Chemical Reactor",mgr.getAllRecipesFor(GTRecipeTypes.CHEMICAL_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Chemical Bath",mgr.getAllRecipesFor(GTRecipeTypes.CHEMICAL_BATH_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Electrolyzer",mgr.getAllRecipesFor(GTRecipeTypes.ELECTROLYZER_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Polarizer",mgr.getAllRecipesFor(GTRecipeTypes.POLARIZER_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Distillation Tower",mgr.getAllRecipesFor(GTRecipeTypes.DISTILLATION_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Fusion Reactor",mgr.getAllRecipesFor(GTRecipeTypes.FUSION_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Fermenting",mgr.getAllRecipesFor(GTRecipeTypes.FERMENTING_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Assembly Line",mgr.getAllRecipesFor(GTRecipeTypes.ASSEMBLY_LINE_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Pyrolyse Oven",mgr.getAllRecipesFor(GTRecipeTypes.PYROLYSE_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Cracking", mgr.getAllRecipesFor(GTRecipeTypes.CRACKING_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Vacuum Freezer", mgr.getAllRecipesFor(GTRecipeTypes.VACUUM_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Extractor", mgr.getAllRecipesFor(GTRecipeTypes.EXTRACTOR_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Polarizer", mgr.getAllRecipesFor(GTRecipeTypes.POLARIZER_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Autoclave", mgr.getAllRecipesFor(GTRecipeTypes.AUTOCLAVE_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Large Chemical Reactor", mgr.getAllRecipesFor(GTRecipeTypes.LARGE_CHEMICAL_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Arc Furnace", mgr.getAllRecipesFor(GTRecipeTypes.ARC_FURNACE_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Compresser", mgr.getAllRecipesFor(GTRecipeTypes.COMPRESSOR_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Brewing", mgr.getAllRecipesFor(GTRecipeTypes.BREWING_RECIPES), iMappingCollector, visitedItems);
        processGTRecipes("Distillery", mgr.getAllRecipesFor(GTRecipeTypes.DISTILLERY_RECIPES), iMappingCollector, visitedItems);


        Projectegregtech.LOGGER.info("Finished adding custom mappings");

    }

}