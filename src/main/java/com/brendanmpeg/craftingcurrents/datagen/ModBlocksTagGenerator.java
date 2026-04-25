package com.brendanmpeg.craftingcurrents.datagen;

import com.brendanmpeg.craftingcurrents.CraftingCurrents;
import com.brendanmpeg.craftingcurrents.block.ModBlocks;
import com.brendanmpeg.craftingcurrents.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlocksTagGenerator extends BlockTagsProvider {
    public ModBlocksTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CraftingCurrents.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.Blocks.CRAFTING_CURRENTS_COMPONENT)
                .add(ModBlocks.BI_AND_GATE.get())
                .add(ModBlocks.BI_OR_GATE.get())
                .add(ModBlocks.BI_SIG_BUS.get())
                .add(ModBlocks.MONO_SIG_BUS.get());
        this.tag(ModTags.Blocks.CRAFTING_CURRENTS_GATE)
                .add(ModBlocks.BI_OR_GATE.get())
                .add(ModBlocks.BI_AND_GATE.get());
    }
}
