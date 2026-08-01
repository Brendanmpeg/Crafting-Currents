package com.brendanmpeg.craftingcurrents.block.custom;

import com.brendanmpeg.craftingcurrents.utils.Codex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;


public class MonoSigBus extends Block {

    /* Static Globals */
    Codex codex = new Codex();

    /* Minecraft States */
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    /* Custom States */

    public static final BooleanProperty SIGNAL = BooleanProperty.create("signal");
    public static final IntegerProperty POWERSOURCES = IntegerProperty.create("power_sources",0, 2);
    public static final DirectionProperty CONN1 = DirectionProperty.create("conn1", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN);
    public static final DirectionProperty CONN2 = DirectionProperty.create("conn2", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN);
    private static final int PROPAGATION_DELAY = 1;


    //class constructor to set the default states
    public MonoSigBus(Properties properties) {
        super (properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SIGNAL, false).setValue(POWERSOURCES, 0));
    }

    /* Shaping functions */
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    /* Logical functions */




    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState newState = null;
        return newState;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (level.isClientSide) return;
        System.out.println("onPlace running");
        level.scheduleTick(pos, this, PROPAGATION_DELAY, TickPriority.HIGH);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && !newState.is(this)) {
            // Notify the CONN blocks directly that we're gone
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) return;
        level.scheduleTick(pos, this, PROPAGATION_DELAY);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isClientSide) return;

    }

    // Register the properties Ex. FACING
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIGNAL)
                .add(POWERSOURCES)
                .add(CONN1)
                .add(CONN2);
    }
}
