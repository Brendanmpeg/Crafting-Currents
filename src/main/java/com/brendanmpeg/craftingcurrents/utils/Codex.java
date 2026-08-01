package com.brendanmpeg.craftingcurrents.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static com.brendanmpeg.craftingcurrents.block.custom.MonoSigBus.*;

public class Codex {

    public BlockState[] getMonoConnections(BlockPos pos, BlockState state, Level level) {
        BlockState neighbor1 = level.getBlockState(pos.relative(state.getValue(CONN1)));
        BlockState neighbor2 = level.getBlockState(pos.relative(state.getValue(CONN2)));
        return new BlockState[]{neighbor1, neighbor2};
    }

    public boolean isComponentBlock(BlockState blockState) {
        return blockState.is(ModTags.Blocks.CRAFTING_CURRENTS_COMPONENT);
    }

    public boolean isGateBlock(BlockState blockState) {
        return blockState.is(ModTags.Blocks.CRAFTING_CURRENTS_GATE);
    }
}
