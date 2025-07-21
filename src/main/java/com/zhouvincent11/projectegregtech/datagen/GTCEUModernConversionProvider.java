package com.zhouvincent11.projectegregtech.datagen;

import com.gregtechceu.gtceu.api.worldgen.OreVeinDefinition;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.gregtechceu.gtceu.data.worldgen.GTOreVeins;
import com.zhouvincent11.projectegregtech.Constants;
import com.zhouvincent11.projectegregtech.Projectegregtech;
import moze_intel.projecte.api.data.CustomConversionProvider;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.lang.module.ResolutionException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class GTCEUModernConversionProvider extends CustomConversionProvider {

    public GTCEUModernConversionProvider(@NotNull PackOutput output, @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Projectegregtech.MODID);
    }

    private static NormalizedSimpleStack ingotTag(String ingot) {
        return NSSItem.createTag(Tags.Items.INGOTS.location().withSuffix("/" + ingot));
    }

    private static NormalizedSimpleStack dustTag(String dust) {
        return NSSItem.createTag(Tags.Items.DUSTS.location().withSuffix("/" + dust));
    }

    private static NormalizedSimpleStack smallDustTag(String dust) {
        return NSSItem.createTag(ResourceLocation.parse("c:small_dusts").withSuffix("/" + dust));
    }

    private static NormalizedSimpleStack gemTag(String gem) {
        return NSSItem.createTag(Tags.Items.GEMS.location().withSuffix("/" + gem));
    }

    private static NormalizedSimpleStack tag(String tag) {
        return NSSFluid.createTag(ResourceLocation.parse("c:" + tag));
    }


    private void buildOreProeccessingProducts(String name, int dupeFactor, int emc) {

        ResourceLocation block_ore = Constants.RAW_ORE_BLOCK.location().withSuffix("/" + name);
        ResourceLocation raw_ore = Constants.RAW_ORE.location().withSuffix("/" + name);
        ResourceLocation crushed_ore = Constants.CRUSHED_ORES.location().withSuffix("/" + name);
        ResourceLocation purified_ore = Constants.PURIFIED_ORES.location().withSuffix("/" + name);
        ResourceLocation refined_ore = Constants.REFINED_ORES.location().withSuffix("/" + name);
        ResourceLocation impure_dust = Constants.IMPURE_DUSTS.location().withSuffix("/" + name);
        ResourceLocation pure_dust = Constants.PURE_DUSTS.location().withSuffix("/" + name);

        createConversionBuilder(Projectegregtech.RL(String.format("%s_ore_processing", name)))
                .before(NSSItem.createTag(block_ore), emc)
                .before(NSSItem.createTag(raw_ore), emc)

                // Crushing may reesult in more than 2 crushed ore i.e. redstone, quartz, lapis
                .conversion(NSSItem.createTag(crushed_ore), dupeFactor).ingredient(NSSItem.createTag(raw_ore)).end()
                // Ore wash
                .conversion(NSSItem.createTag(purified_ore)).ingredient(NSSItem.createTag(crushed_ore)).end()
                // Thermal Centrifuge
                .conversion(NSSItem.createTag(refined_ore)).ingredient(NSSItem.createTag(crushed_ore)).end()
                // Macerator
                .conversion(NSSItem.createTag(impure_dust)).ingredient(NSSItem.createTag(refined_ore)).end()
                // Maccerator
                .conversion(NSSItem.createTag(pure_dust)).ingredient(NSSItem.createTag(impure_dust)).end();

    }


    protected void addMaterialsConversions(@NotNull HolderLookup.Provider provider) {
        Projectegregtech.LOGGER.info("Adding materials conversiions");
        createConversionBuilder(Projectegregtech.RL("materials"))

//                .before(ingotTag("steel"), 128*9)

                // Can be made from ore processing
                .before(ingotTag("zinc"), 1024 * 9)
                .before(ingotTag("cobalt"), 1024 * 9)
                .before(ingotTag("antimony"), 1024 * 9)
                .before(ingotTag("beryllium"), 1024 * 9)
                .before(ingotTag("manganese"), 1024 * 9)

                .before(ingotTag("indium"), 2048 * 9)
                .before(ingotTag("molybdenum"), 2048 * 9)
                .before(ingotTag("neodymium"), 2048 * 9)
                .before(ingotTag("uranium"), 2048 * 9)
                .before(ingotTag("thorium"), 2048 * 9)
                .before(ingotTag("vanadium"), 2048 * 9)
                .before(ingotTag("tantalum"), 2048 * 9)
                .before(ingotTag("niobium"), 2048 * 9)
                .before(ingotTag("gallium"), 2048 * 9)


                .before(ingotTag("chromium"), 4096 * 9)
                .before(ingotTag("titanium"), 8096 * 9)
                .before(ingotTag("tungsten"), 16384 * 9)


                // Platinum Group Processing
                .before(dustTag("platinum_group_sludge"),16384*9)
                // Inert Metal Mixture - 6 required
                .conversion(dustTag("inert_metal_mixture")).ingredient(dustTag("platinum_group_sludge"), 6/2).end()
                .conversion(ingotTag("ruthenium"),5).ingredient(dustTag("inert_metal_mixture"), 6).end()
                .conversion(ingotTag("rhodium")).ingredient(dustTag("inert_metal_mixture"), 6).end()
                // Raw Platinum Powder - 3 required
                .conversion(dustTag("platinum_raw")).ingredient(dustTag("platinum_group_sludge"), 6/3).end()
                .conversion(ingotTag("platinum")).ingredient(dustTag("platinum_raw"),3).end()
                // Raw palladium powder - 5 required
                .conversion(dustTag("palladium_raw")).ingredient(dustTag("platinum_group_sludge"), 6/3).end()
                .conversion(ingotTag("palladium")).ingredient(dustTag("palladium_raw"),5).end()
                // Rarest Metal Mixture - Also produces osmium but osmium already has a defined EMC, 7 required
                .conversion(dustTag("rarest_metal_mixture")).ingredient(dustTag("platinum_group_sludge"), 6/1).end()
                .conversion(ingotTag("iridium"), 5).ingredient(dustTag("rarest_metal_mixture"),7).end()



                // Rare Earth Processing - Produces Cadmium and Neodynmium but already defined previous, percentage based instead of ratios
                .before(dustTag("rare_earth"), 16384 * 9)
                .conversion(smallDustTag("samarium")).ingredient(dustTag("rare_earth"), Math.ceilDivExact(100,35)).end()
                .conversion(smallDustTag("cerium")).ingredient(dustTag("rare_earth"), Math.ceilDivExact(100,55)).end()
                .conversion(smallDustTag("yttrium")).ingredient(dustTag("rare_earth"), Math.ceilDivExact(100,35)).end()
                .conversion(smallDustTag("lanthanum")).ingredient(dustTag("rare_earth"), Math.ceilDivExact(100,25)).end()

                // Naqadah
                .before(ingotTag("naquadah"), 32768 * 9)
                .conversion(dustTag("enriched_naquadah")).ingredient(dustTag("naquadah"), 10/1).end()


                // Impure Naquadah Byproducts
                .before(ingotTag("trinium"), 32768 * 9)

                // Fusion reactor
//                .before(ingotTag("europium"), 32768 * 9)
//                .before(ingotTag("americium"), 32768 * 9)


                // Plastics
                .before(ingotTag("polyethylene"), 256 * 9)
                .before(ingotTag("polytetrafluoroethylene"), 512 * 9)
                .before(ingotTag("polyvinyl_chloride"), 512 * 9)
                .before(ingotTag("polyphenylene_sulfide"), 512 * 9)
                .before(ingotTag("polybenzimidazole"), 2048 * 9)
                .before(ingotTag("epoxy"), 2048 * 9)


                // Rubbers
                .before(ingotTag("silicone_rubber"), 1024 * 9)
                .before(ingotTag("styrene_butadiene_rubber"), 2048 * 9)

                // Dusts\
                .before(dustTag("wood"), 8)
                .before(dustTag("sulfur"), 128 * 9)
                .before(dustTag("arsenic"), 128 * 9)
                .before(dustTag("silicon"), 128 * 9)
                .before(dustTag("sodium"), 128 * 9)
                .before(dustTag("boron"), 256 * 9)
                .before(dustTag("potassium"), 256 * 9)
                .before(dustTag("lithium"), 256 * 9)
                .before(dustTag("magnesium"), 256 * 9)
                .before(dustTag("phosphorus"), 128 * 9)
                .before(dustTag("graphite"), 128 * 9)
                .before(dustTag("barium"), 1024 * 9)
                .before(dustTag("apatite"), 1024 * 9)

                // Wetware computing line
                .before(GTItems.STEM_CELLS, 8192)
                .before(dustTag("gelatin"), 64)

                // Blocks
//                .before(tag("marbles"), 16)
//                .before(tag("cobblestones"), 2);

        ;
    }


    protected void addOreProcessingProducts(@NotNull HolderLookup.Provider provider) {
        /**
         * ruby
         * salt
         * sapphire
         * scheelite
         * sodalite
         * tantalite
         * spessartine
         * sphalerite
         * stibnite
         * tetrahedrite
         * topaz
         * tungstate
         * urananite
         * wulfenite
         * yellow_limonite
         * nether_quartz
         * certus_quartz
         * quartzite
         * graphite
         * bornite
         * chalcocite
         * realgar
         * bastnasite
         * pentlandite
         * spodumene
         * lepidolite
         * glauconite_sand
         * malachite
         * mica
         * barite
         * alunite
         * talc
         * soapstone
         * kyanite
         * pyrochlore
         * oilsands
         * olivine
         * opal
         * amethyst
         * lapis
         * apatite
         * tricalcium_phosphate
         * red_garnet
         * yellow_garnet
         * vanadium_magnetite
         * pollucite
         * bentonite
         * fullers_earth
         * pitchblende
         * monazite
         * trona
         * gypsum
         * zeolite
         * redstone
         * electrotine
         * diatomite
         * granitic_mineral_sand
         * garnet_sand
         * basaltic_mineral_sand
         */

        for (ResourceKey<OreVeinDefinition> key : GTOreVeins.ALL_KEYS) {
            LOGGER.info(String.format("Vein: %s", key.location().getPath()));
        }
        Projectegregtech.LOGGER.info("Adding ore processing conversiions");
        buildOreProeccessingProducts("iron", 2, 256);
        buildOreProeccessingProducts("copper", 2, 256);
        buildOreProeccessingProducts("gold", 2, 256);
        buildOreProeccessingProducts("aluminum", 2, 256);
        buildOreProeccessingProducts("beryllium", 2, 256);
        buildOreProeccessingProducts("cobalt", 2, 256);
        buildOreProeccessingProducts("lead", 2, 256);
        buildOreProeccessingProducts("lithium", 2, 256);
        buildOreProeccessingProducts("molybdenum", 2, 256);
        buildOreProeccessingProducts("neodymium", 2, 256);
        buildOreProeccessingProducts("nickel", 2, 256);
        buildOreProeccessingProducts("palladium", 2, 256);
        buildOreProeccessingProducts("platinum", 2, 256);
        buildOreProeccessingProducts("plutonium_239", 2, 256);
        buildOreProeccessingProducts("silver", 2, 256);
        buildOreProeccessingProducts("thorium", 2, 256);
        buildOreProeccessingProducts("tin", 2, 256);
        buildOreProeccessingProducts("naquadah", 2, 256);
        buildOreProeccessingProducts("almandine", 2, 256);
        buildOreProeccessingProducts("asbestos", 2, 256);
        buildOreProeccessingProducts("hematite", 2, 256);
        buildOreProeccessingProducts("blue_topaz", 2, 256);
        buildOreProeccessingProducts("emerald", 2, 256);
        buildOreProeccessingProducts("goethite", 2, 256);
        buildOreProeccessingProducts("calcite", 2, 256);
        buildOreProeccessingProducts("casitterite", 2, 256);
        buildOreProeccessingProducts("casitterite_sand", 2, 256);
        buildOreProeccessingProducts("chalcopyrite", 2, 256);
        buildOreProeccessingProducts("chromite", 2, 256);
        buildOreProeccessingProducts("cinnabar", 2, 256);
        buildOreProeccessingProducts("coal", 2, 256);
        buildOreProeccessingProducts("cobaltite", 2, 256);
        buildOreProeccessingProducts("sheldonite", 2, 256);
        buildOreProeccessingProducts("diamond", 2, 256);
        buildOreProeccessingProducts("galena", 2, 256);
        buildOreProeccessingProducts("garnierite", 2, 256);
        buildOreProeccessingProducts("green_sapphire", 2, 256);
        buildOreProeccessingProducts("grossulur", 6, 256);
        buildOreProeccessingProducts("ilmenite", 2, 256);
        buildOreProeccessingProducts("bauxite", 2, 256);
        buildOreProeccessingProducts("lazurite", 12, 256);
        buildOreProeccessingProducts("magnesite", 2, 256);
        buildOreProeccessingProducts("magnetite", 2, 256);
        buildOreProeccessingProducts("molybdenite", 2, 256);
        buildOreProeccessingProducts("powellite", 2, 256);
        buildOreProeccessingProducts("ruby", 2, 256);
        buildOreProeccessingProducts("salt", 4, 256);
        buildOreProeccessingProducts("sapphire", 2, 256);
        buildOreProeccessingProducts("scheelite", 2, 256);
        buildOreProeccessingProducts("sodalite", 2, 256);
        buildOreProeccessingProducts("tantalite", 2, 256);
        buildOreProeccessingProducts("spessartine", 2, 256);
        buildOreProeccessingProducts("sphalerite", 2, 256);
        buildOreProeccessingProducts("stibnite", 2, 256);
        buildOreProeccessingProducts("tetrahedrite", 2, 256);
        buildOreProeccessingProducts("topaz", 2, 256);
        buildOreProeccessingProducts("tungstate", 2, 256);
        buildOreProeccessingProducts("urananite", 2, 256);
        buildOreProeccessingProducts("wulfenite", 2, 256);
        buildOreProeccessingProducts("limonite", 2, 256);
        buildOreProeccessingProducts("nether_quartz", 2, 256);
        buildOreProeccessingProducts("certus_quartz", 2, 256);
        buildOreProeccessingProducts("quartzite", 2, 256);
        buildOreProeccessingProducts("graphite", 2, 256);
        buildOreProeccessingProducts("bornite", 2, 256);
        buildOreProeccessingProducts("chalcocite", 2, 256);
        buildOreProeccessingProducts("realgar", 2, 256);
        buildOreProeccessingProducts("bastnasite", 2, 256);
        buildOreProeccessingProducts("pentlandite", 2, 256);
        buildOreProeccessingProducts("spodumene", 2, 256);
        buildOreProeccessingProducts("lepidolite", 2, 256);
        buildOreProeccessingProducts("glauconite_sand", 2, 256);
        buildOreProeccessingProducts("malachite", 2, 256);
        buildOreProeccessingProducts("mica", 2, 256);
        buildOreProeccessingProducts("barite", 2, 256);
        buildOreProeccessingProducts("alunite", 2, 256);
        buildOreProeccessingProducts("talc", 2, 256);
        buildOreProeccessingProducts("soapstone", 2, 256);
        buildOreProeccessingProducts("kyanite", 2, 256);
        buildOreProeccessingProducts("pyrochlore", 2, 256);
        buildOreProeccessingProducts("oilsands", 2, 256);
        buildOreProeccessingProducts("olivine", 2, 256);
        buildOreProeccessingProducts("opal", 2, 256);
        buildOreProeccessingProducts("amethyst", 2, 256);
        buildOreProeccessingProducts("lapis", 12, 256);
        buildOreProeccessingProducts("apatite", 8, 256);
        buildOreProeccessingProducts("tricalcium_phosphate", 2, 256);
        buildOreProeccessingProducts("red_garnet", 8, 256);
        buildOreProeccessingProducts("yellow_garnet", 8, 256);
        buildOreProeccessingProducts("pollucite", 2, 256);
        buildOreProeccessingProducts("bentonite", 2, 256);
        buildOreProeccessingProducts("fullers_earth", 2, 256);
        buildOreProeccessingProducts("pitchblende", 2, 256);
        buildOreProeccessingProducts("monazite", 2, 256);
        buildOreProeccessingProducts("trona", 2, 256);
        buildOreProeccessingProducts("gypsum", 2, 256);
        buildOreProeccessingProducts("zeolite", 2, 256);
        buildOreProeccessingProducts("redstone", 10, 256);
        buildOreProeccessingProducts("electrotine", 2, 256);
        buildOreProeccessingProducts("diatomite", 2, 256);
        buildOreProeccessingProducts("granitic_mineral_sand", 2, 256);
        buildOreProeccessingProducts("garnet_sand", 2, 256);
        buildOreProeccessingProducts("basaltic_mineral_sand", 2, 256);


    }



    //Fluids

    protected void addFluidConversions(@NotNull HolderLookup.Provider provider) {
        Projectegregtech.LOGGER.info("Adding fluid conversions");
        createConversionBuilder(Projectegregtech.RL("fluids"))
                .before(tag("oxygen"), 16)
                .before(tag("hydrogen"), 16)
                .before(tag("nitrogen"), 16)
                .before(tag("helium"), 32)
                .before(tag("neon"), 128)
                .before(tag("radon"), 128)
                .before(tag("argon"), 128)
                .before(tag("hydrogen_sulfide"), 128)
                .before(tag("acetone"), 128)
                .before(tag("phenol"), 128)
                .before(tag("chlorine"), 128)
                .before(tag("fluorine"), 128)

                .before(tag("methanol"), 128)
                .before(tag("ethylene"), 128)
                .before(tag("propane"), 128)
                .before(tag("cumene"), 128)
                .before(tag("benzene"), 128)
                .before(tag("cyclohexane"), 128)
                .before(tag("ammonia"), 128)
                .before(tag("nitric_oxide"), 128)
                .before(tag("glue"), 4);
    }

    @Override
    protected void addCustomConversions(@NotNull HolderLookup.Provider provider) {
        Projectegregtech.LOGGER.info("Adding custom conversions");


        addMaterialsConversions(provider);
//        addOreProcessingProducts(provider);
        addFluidConversions(provider);

    }
}
