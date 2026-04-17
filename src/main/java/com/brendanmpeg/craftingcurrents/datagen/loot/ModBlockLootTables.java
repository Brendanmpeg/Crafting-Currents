package com.brendanmpeg.craftingcurrents.datagen.loot;

import com.brendanmpeg.craftingcurrents.block.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.BI_STRAIGHT_SIG_BUS.get());
        this.dropSelf(ModBlocks.STRAIGHT_SIG_BUS.get());
        this.dropSelf(ModBlocks.BI_AND_GATE.get());
        this.dropSelf(ModBlocks.BI_OR_GATE.get());
        //this.add(ModBlocks.ORE.get(), block -> createXOreDrops());
    }

    // All known blocks with know loot tables registered through our deferred register
    // that do not have a noLootTable property will be generated here
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
