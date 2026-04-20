package com.brendanmpeg.craftingcurrents.utils;

import com.brendanmpeg.craftingcurrents.CraftingCurrents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks {
        public static final TagKey<Block> CRAFTING_CURRENTS_COMPONENT = tag("crafting_curents_component");
        public static final TagKey<Block> CRAFTING_CURRENTS_GATE = tag("crafting_curents_gate");
        public static final TagKey<Block> CRAFTING_CURRENTS_TEST_BLOCKS = tag("crafting_curents_test_blocks");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(CraftingCurrents.MODID, name));

        }
    }

    public static class Items {}

}
