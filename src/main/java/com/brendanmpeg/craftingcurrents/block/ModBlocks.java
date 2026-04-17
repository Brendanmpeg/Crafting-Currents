package com.brendanmpeg.craftingcurrents.block;

import com.brendanmpeg.craftingcurrents.CraftingCurrents;
import com.brendanmpeg.craftingcurrents.block.custom.*;
import com.brendanmpeg.craftingcurrents.block.custom.*;
import com.brendanmpeg.craftingcurrents.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    //DO NOT DELETE - THIS ADDS BLOCKS TO THE GAME
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CraftingCurrents.MODID);

    //CALL THIS METHOD WHEN CREATING NEW BLOCKS
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    //DO NOT DELETE - THIS METHOD IS USED BY 'REGISTERBLOCK', CALL THAT METHOD TO REGISTER YOUR BLOCK
    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    //DO NOT DELETE - THIS REGISTORS BLOCKS INTO THE GAME
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    /* Block Declarations */
    public static final RegistryObject<Block> DIRECTIONAL_TEST_BLOCK = registerBlock("directional_test_block",
            () -> new DirectionalTestBlock(BlockBehaviour.Properties.of().instabreak().lightLevel(s ->15)));

    public static final RegistryObject<Block> BI_STRAIGHT_SIG_BUS = registerBlock("bi_straight_sig_bus",
            () -> new BiStraightSigBus(BlockBehaviour.Properties.of().instabreak()));

    public static final RegistryObject<Block> STRAIGHT_SIG_BUS = registerBlock("straight_sig_bus",
            () -> new StraightSigBus(BlockBehaviour.Properties.of().instabreak()));

    public static final RegistryObject<Block> BI_AND_GATE = registerBlock("bi_and_gate",
            () -> new BiAndGate(BlockBehaviour.Properties.of().instabreak()));

    public static final RegistryObject<Block> BI_OR_GATE = registerBlock("bi_or_gate",
            () -> new BiOrGate(BlockBehaviour.Properties.of().instabreak()));
}
