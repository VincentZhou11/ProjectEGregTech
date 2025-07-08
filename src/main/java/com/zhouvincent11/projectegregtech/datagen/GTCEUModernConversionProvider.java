package com.zhouvincent11.projectegregtech.datagen;

import com.gregtechceu.gtceu.data.item.GTItems;
import com.zhouvincent11.projectegregtech.Projectegregtech;
import moze_intel.projecte.api.data.CustomConversionProvider;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GTCEUModernConversionProvider extends CustomConversionProvider {

    public GTCEUModernConversionProvider(@NotNull PackOutput output, @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Projectegregtech.MODID);
    }

    private static NormalizedSimpleStack ingotTag(String ingot) {
        return NSSItem.createTag(Tags.Items.INGOTS.location().withSuffix("/" + ingot));
    }

    private static NormalizedSimpleStack dustTag(String gem) {
        return NSSItem.createTag(Tags.Items.DUSTS.location().withSuffix("/" + gem));
    }

    private static NormalizedSimpleStack gemTag(String gem) {
        return NSSItem.createTag(Tags.Items.GEMS.location().withSuffix("/" + gem));
    }

    private static NormalizedSimpleStack tag(String tag) {
        return NSSFluid.createTag(ResourceLocation.parse("c:"+tag));
    }

    @Override
    protected void addCustomConversions(@NotNull HolderLookup.Provider provider) {
        Projectegregtech.LOGGER.info("Adding custom conversions");

        createConversionBuilder(Projectegregtech.RL("materials"))
                .before(ingotTag("zinc"), 128*9)
                .before(ingotTag("cobalt"), 4096*9)
                .before(ingotTag("antimony"), 4096*9)

                .before(ingotTag("tungsten"), 8192*9)
                .before(ingotTag("manganese"), 8192*9)
                .before(ingotTag("chromium"), 8192*9)
                .before(ingotTag("titanium"), 8192*9)
                .before(ingotTag("gallium"), 8192*9)
                .before(ingotTag("indium"), 8192*9)
                .before(ingotTag("molybdenum"), 8192*9)
                .before(ingotTag("platinum"), 8192*9)
                .before(ingotTag("neodymium"), 8192*9)


                .before(ingotTag("rhodium"), 16384*9)
                .before(ingotTag("palladium"), 16384*9)
                .before(ingotTag("iridium"), 16384*9)
                .before(ingotTag("trinium"), 16384*9)
                .before(ingotTag("niobium"), 16384*9)
                .before(ingotTag("vanadium"), 16384*9)
                .before(ingotTag("samarium"), 16384*9)
                .before(ingotTag("europium"), 16384*9)
                .before(ingotTag("ruthenium"), 16384*9)
                .before(ingotTag("yttrium"), 16384*9)
                .before(ingotTag("tantalum"), 16384*9)



                .before(ingotTag("naquadah"), 32768*9)
                .before(ingotTag("americium"), 32768*9)


                // Plastics
                .before(ingotTag("polyethylene"), 256*9)
                .before(ingotTag("polytetrafluoroethylene"), 512*9)
                .before(ingotTag("polyvinyl_chloride"), 512*9)
                .before(ingotTag("polyphenylene_sulfide"), 512*9)
                .before(ingotTag("polybenzimidazole"), 2048*9)
                .before(ingotTag("epoxy"), 2048*9)


                // Rubbers
                .before(ingotTag("silicone_rubber"), 1024*9)
                .before(ingotTag("styrene_butadiene_rubber"), 2048*9)

                // Dusts\
                .before(dustTag("wood"), 8)
                .before(dustTag("iron"), 256*9)
                .before(dustTag("sulfur"), 128*9)
                .before(dustTag("arsenic"), 128*9)
                .before(dustTag("silicon"), 128*9)
                .before(dustTag("sodium"), 128*9)
                .before(dustTag("boron"), 256*9)
                .before(dustTag("potassium"), 256*9)
                .before(dustTag("phosphorus"), 128*9)
                .before(dustTag("graphite"), 128*9)
                .before(dustTag("barium"), 1024*9)
                .before(dustTag("apatite"), 1024*9)
                .before(dustTag("enriched_naquadah"), 3*32768*9)
                .before(dustTag("meat"), 64)
                .before(dustTag("gelatin"), 64)

                .before(GTItems.STEM_CELLS, 8192)




        ;


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
                .before(tag("glue"), 4)











        ;

//        GTMaterials.Oxygen.asFluidIngredient(0).getFluids()[0];
    }
}
