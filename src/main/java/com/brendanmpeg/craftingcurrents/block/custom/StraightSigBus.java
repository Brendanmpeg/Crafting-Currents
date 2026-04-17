package com.brendanmpeg.craftingcurrents.block.custom;

import com.brendanmpeg.craftingcurrents.utils.RelativeDirections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;

public class StraightSigBus extends Block {

    /* Minecraft States */
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    /* Custom States */
    public static final BooleanProperty SIGNAL_1 = BooleanProperty.create("signal_1");
    private static final int PROPAGATION_DELAY = 1;

    //class constructor to set the default states
    public StraightSigBus(Properties properties) {
        super (properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH)
                .setValue(SIGNAL_1, false));
    }

    /* Shaping functions */
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    /* Logical functions */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        Direction facingDirection = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, facingDirection)
                .setValue(SIGNAL_1, false);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (level.isClientSide) return;
        level.scheduleTick(pos, this, PROPAGATION_DELAY, TickPriority.HIGH);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        System.out.println("New State: " + newState);
        if (!level.isClientSide && !newState.is(this)) {
            System.out.println("New State block entered ");
            // Notify the block directly in front that we're gone
            RelativeDirections relativeDirections = new RelativeDirections(pos, state.getValue(FACING));
            level.neighborChanged(relativeDirections.frontNeighbor,this, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) return;

        RelativeDirections relativePositions = new RelativeDirections(pos, state.getValue(FACING));
        BlockState NeighborState = level.getBlockState(neighborPos);

        /* signal transfer logic */
        if (NeighborState.hasProperty(SIGNAL_1))
        {
            // Move this to tick
            System.out.println("Rear neighbor");
            level.scheduleTick(pos, this, PROPAGATION_DELAY);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

    }

    // Register the properties Ex. FACING
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING)
                .add(SIGNAL_1);
    }
}
