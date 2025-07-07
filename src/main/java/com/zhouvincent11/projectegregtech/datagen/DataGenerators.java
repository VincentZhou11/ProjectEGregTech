package com.zhouvincent11.projectegregtech.datagen;

import com.zhouvincent11.projectegregtech.Projectegregtech;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Projectegregtech.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        final DataGenerator gen = event.getGenerator();
        final PackOutput packOutput = gen.getPackOutput();
        final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        Projectegregtech.LOGGER.info("Initializing Conversion Provider");

        gen.addProvider(event.includeServer(), new GTCEUModernConversionProvider(packOutput, lookupProvider));
    }
}
