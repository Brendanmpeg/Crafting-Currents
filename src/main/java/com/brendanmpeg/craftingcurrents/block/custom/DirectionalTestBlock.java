package com.brendanmpeg.craftingcurrents.block.custom;

import com.brendanmpeg.craftingcurrents.utils.RelativeDirections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
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

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) return;

        RelativeDirections relativePositions = new RelativeDirections(pos, state.getValue(FACING));
        BlockState neighborState = level.getBlockState(neighborPos);

        if(!neighborState.isAir()){
            RelativeDirections neighborsRelatives = new RelativeDirections(neighborPos, neighborState.getValue(FACING));
            System.out.println("My FACING output: " + state.getValue(FACING));
            System.out.println("Neighbor FACING output: " + neighborState.getValue(FACING));

            if (neighborPos.relative(neighborState.getValue(FACING)).equals(pos)) {
                System.out.println("I am my neighbors front");
            }
            if (neighborPos.equals(pos.south())) {
                System.out.println("I am my neighbors south. pos.south: " + pos.south());
            }
        }
    }

    // Register the properties Ex. FACING
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
