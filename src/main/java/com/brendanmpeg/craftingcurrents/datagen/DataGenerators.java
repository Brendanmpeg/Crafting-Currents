package com.brendanmpeg.craftingcurrents.datagen;

import com.brendanmpeg.craftingcurrents.CraftingCurrents;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = CraftingCurrents.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatehrData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), ModLootTableProvider.create(packOutput));

        generator.addProvider(event.includeClient(), new ModBlockStateProvidor(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvidor(packOutput, existingFileHelper));

        ModBlocksTagGenerator blocksTagGenerator = generator.addProvider(event.includeServer(),
                new ModBlocksTagGenerator(packOutput, lookupProvider, existingFileHelper));
    }
}
