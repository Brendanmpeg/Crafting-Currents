package com.brendanmpeg.craftingcurrents.block.custom;

import com.brendanmpeg.craftingcurrents.block.ModBlocks;
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

public class BiStraightSigBus extends Block {

    /* Minecraft States */
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    /* Custom States */
    public static final BooleanProperty SIGNAL_1 = BooleanProperty.create("signal_1");
    public static final BooleanProperty SIGNAL_2 = BooleanProperty.create("signal_2");
    private static final int PROPAGATION_DELAY = 1;

    //class constructor to set the default states
    public BiStraightSigBus(Properties properties) {
        super (properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH)
                .setValue(SIGNAL_1, false)
                .setValue(SIGNAL_2, false));
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
                .setValue(SIGNAL_1, false)
                .setValue(SIGNAL_2, false);
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
        boolean signal_1 = level.getSignal(pos.relative(relativePositions.left), relativePositions.left) > 0;
        boolean signal_2 = level.getSignal(pos.relative(relativePositions.right), relativePositions.right) > 0;
        /* signal transfer logic */
        if (neighborPos.equals(relativePositions.rearNeighbor))
        {
            System.out.println("Rear neighbor");
            level.scheduleTick(pos, this, PROPAGATION_DELAY);
        } else if (neighborPos.equals(relativePositions.leftNeighbor)) {
            System.out.println("Left neighbors signal: " + signal_1);
            if (state.getValue(SIGNAL_1) != signal_1){
                level.setBlock(pos, state.setValue(SIGNAL_1, signal_1), 3);
            }
        } else if (neighborPos.equals(relativePositions.rightNeighbor)) {
            System.out.println("Right neighbors signal: " + signal_2);
            if (state.getValue(SIGNAL_2) != signal_2){
                level.setBlock(pos, state.setValue(SIGNAL_2, signal_2), 3);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

        RelativeDirections relativePositions = new RelativeDirections(pos, state.getValue(FACING));
        BlockState rearNeighborState = level.getBlockState(relativePositions.rearNeighbor);
        boolean leftSignal = level.getSignal(pos.relative(relativePositions.left), relativePositions.left) > 0;
        boolean rightSignal = level.getSignal(pos.relative(relativePositions.right), relativePositions.right) > 0;

        if (rearNeighborState.is(ModBlocks.BI_STRAIGHT_SIG_BUS.get()) &&
                rearNeighborState.getValue(FACING) == relativePositions.front) {
            boolean signal_1 = rearNeighborState.getValue(SIGNAL_1);
            boolean signal_2 = rearNeighborState.getValue(SIGNAL_2);

            if (state.getValue(SIGNAL_1) != signal_1 || state.getValue(SIGNAL_2) != signal_2){
                BlockState newState = state
                        .setValue(SIGNAL_1, signal_1 | leftSignal)
                        .setValue(SIGNAL_2, signal_2 | rightSignal);
                level.setBlock(pos, newState, 3);
            }
        } else {
            BlockState newState = state
                    .setValue(SIGNAL_1, leftSignal)
                    .setValue(SIGNAL_2, rightSignal);
            level.setBlock(pos, newState, 3);
        }
    }

    // Register the properties Ex. FACING
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING)
                .add(SIGNAL_1)
                .add(SIGNAL_2);
    }
}
