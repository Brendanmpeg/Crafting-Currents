package com.brendanmpeg.craftingcurrents.datagen;

import com.brendanmpeg.craftingcurrents.CraftingCurrents;
import com.brendanmpeg.craftingcurrents.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvidor extends BlockStateProvider {

    public ModBlockStateProvidor(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CraftingCurrents.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
//        blockWithItem(ModBlocks.BI_AND_GATE);
//        blockWithItem(ModBlocks.BI_OR_GATE);
//        blockWithItem(ModBlocks.STRAIGHT_SIG_BUS);
//        blockWithItem(ModBlocks.BI_STRAIGHT_SIG_BUS);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject){
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }


}