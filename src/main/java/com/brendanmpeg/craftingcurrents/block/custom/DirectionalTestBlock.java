package com.brendanmpeg.craftingcurrents.block.custom;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class DirectionalTestBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    //class constructor to set the default states
    public DirectionalTestBlock(Properties properties) {
        super (properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        Direction facingDirection = context.getHorizontalDirection();
        System.out.println("Placing block with facing: " + facingDirection);
        return this.defaultBlockState().setValue(FACING, facingDirection);
    }

    // Register the properties Ex. FACING
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
