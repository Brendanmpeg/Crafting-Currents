package com.brendanmpeg.craftingcurrents.block.custom;

import com.brendanmpeg.craftingcurrents.utils.Codex;
import com.brendanmpeg.craftingcurrents.utils.RelativeDirections;
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

import static com.brendanmpeg.craftingcurrents.block.custom.BiOrGate.FACING;
import static com.brendanmpeg.craftingcurrents.block.custom.BiOrGate.SIGNAL_OUT;


public class MonoSigBus extends Block {

    /* Static Globals */
    Codex codex = new Codex();

    /* Minecraft States */
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    /* Custom States */

    public static final BooleanProperty SIGNAL_1 = BooleanProperty.create("signal_1");
    public static final BooleanProperty ISSOURCE = BooleanProperty.create("is_source");
    public static final IntegerProperty POWERSOURCES = IntegerProperty.create("power_sources",0, 2);
    public static final DirectionProperty CONN1 = DirectionProperty.create("conn1", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN);
    public static final DirectionProperty CONN2 = DirectionProperty.create("conn2", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN);
    private static final int PROPAGATION_DELAY = 1;


    //class constructor to set the default states
    public MonoSigBus(Properties properties) {
        super (properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SIGNAL_1, false).setValue(POWERSOURCES, 0));
    }

    /* Shaping functions */
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    /* Logical functions */
    private boolean iamConn(BlockPos blockPos, BlockPos neighborPos, Direction conn) {
        return neighborPos.relative(conn).equals(blockPos);
    }
    private boolean iamConn(BlockState state, BlockState neighborState) {
        if (state.getValue(CONN1).equals(neighborState.getValue(CONN1).getOpposite())){
            return true;
        } else if (state.getValue(CONN1).equals(neighborState.getValue(CONN2).getOpposite())) {
            return true;
        } else if (state.getValue(CONN2).equals(neighborState.getValue(CONN1).getOpposite())) {
            return true;
        } else if (state.getValue(CONN2).equals(neighborState.getValue(CONN2).getOpposite())) {
            return true;
        }
        return false;
    }



    private BlockState[] getConnectedBlocks (BlockPos blockPos, BlockState blockState) {
        BlockPos conn1 = blockPos.relative(blockState.getValue(CONN1));
        BlockPos conn2 = blockPos.relative(blockState.getValue(CONN2));
        return new BlockState[]{};
    }

    public BlockState isPowered(BlockState state, BlockState neighborState) {
        BlockState newState = state;
        if (codex.isGateBlock(neighborState)) {
            if (neighborState.getValue(SIGNAL_OUT)) {
                newState = newState.setValue(POWERSOURCES, state.getValue(POWERSOURCES) + 1).setValue(ISSOURCE, true);
            } else {
                if (state.getValue(POWERSOURCES) > 0 && state.getValue(ISSOURCE)) {
                    newState = newState.setValue(POWERSOURCES, state.getValue(POWERSOURCES) - 1).setValue(ISSOURCE, false);
                }
                if (newState.getValue(POWERSOURCES) == 0 && state.getValue(SIGNAL_1)) {
                    newState = newState.setValue(SIGNAL_1, false);
                }
            }
        } else if (neighborState.getBlock() instanceof MonoSigBus) {
            if (!iamConn(state,neighborState) && neighborState.getValue(CONN2) == Direction.DOWN) return newState;
            if (!neighborState.getValue(POWERSOURCES).equals(state.getValue(POWERSOURCES))) {
                newState = newState.setValue(POWERSOURCES, neighborState.getValue(POWERSOURCES));
            }
            if (newState.getValue(POWERSOURCES) == 0 && state.getValue(SIGNAL_1)) {
                newState = newState.setValue(SIGNAL_1, false);
            } else if (newState.getValue(POWERSOURCES) > 0 && !state.getValue(SIGNAL_1)) {
                newState = newState.setValue(SIGNAL_1, true);
            }
        }
        return newState;
    }

    public BlockState getNewConnections(BlockState blockState, BlockPos blockPos, Level level) {
        blockState = checkConnections(blockState, blockPos, level);
        Direction connection1 = blockState.getValue(CONN1);
        Direction connection2 = blockState.getValue(CONN2);
        BlockState southState = level.getBlockState(blockPos.south());
        BlockState northState = level.getBlockState(blockPos.north());
        BlockState eastState = level.getBlockState(blockPos.east());
        BlockState westState = level.getBlockState(blockPos.west());
        BlockState retstate = blockState;

        if (connection1.equals(Direction.DOWN)) {
            if (isCompatibleBlock(blockPos, southState, blockPos.south())) {
                retstate = retstate.setValue(CONN1, Direction.SOUTH);
            } else if (isCompatibleBlock(blockPos, northState, blockPos.north())) {
                retstate = retstate.setValue(CONN1, Direction.NORTH);
            } else if (isCompatibleBlock(blockPos, eastState, blockPos.east())) {
                retstate = retstate.setValue(CONN1, Direction.EAST);
            } else if (isCompatibleBlock(blockPos, westState, blockPos.west())) {
                retstate = retstate.setValue(CONN1, Direction.WEST);
            }
        }
        if (connection2.equals(Direction.DOWN)) {
            if (retstate.getValue(CONN1).equals(Direction.SOUTH) || retstate.getValue(CONN1).equals(Direction.NORTH)) {
                if (isCompatibleBlock(blockPos, eastState, blockPos.east())) {
                    retstate = retstate.setValue(CONN2, Direction.EAST);
                } else if (isCompatibleBlock(blockPos, westState, blockPos.west())) {
                    retstate = retstate.setValue(CONN1, Direction.WEST);
                } else if (isCompatibleBlock(blockPos, southState, blockPos.south()) && retstate.getValue(CONN1) != Direction.SOUTH) {
                    retstate = retstate.setValue(CONN2, Direction.SOUTH);
                } else if (isCompatibleBlock(blockPos, northState, blockPos.north()) && retstate.getValue(CONN1) != Direction.NORTH) {
                    retstate = retstate.setValue(CONN2, Direction.NORTH);
                }
            } else if (retstate.getValue(CONN1).equals(Direction.EAST) || retstate.getValue(CONN1).equals(Direction.WEST)) {
                if (isCompatibleBlock(blockPos, southState, blockPos.south())) {
                    retstate = retstate.setValue(CONN2, Direction.SOUTH);
                } else if (isCompatibleBlock(blockPos, northState, blockPos.north())) {
                    retstate = retstate.setValue(CONN2, Direction.NORTH);
                } else if (isCompatibleBlock(blockPos, eastState, blockPos.east())  && retstate.getValue(CONN1) != Direction.EAST) {
                    retstate = retstate.setValue(CONN2, Direction.EAST);
                } else if (isCompatibleBlock(blockPos, westState, blockPos.west())  && retstate.getValue(CONN1) != Direction.WEST) {
                    retstate = retstate.setValue(CONN1, Direction.WEST);
                }
            }
        }
        return retstate;
    }

    public BlockState checkConnections (BlockState blockState, BlockPos blockPos, Level level) {
        if (blockState.getValue(CONN1) != Direction.DOWN){
            BlockPos neighborPos = blockPos.relative(blockState.getValue(CONN1));
            BlockState neighborState = level.getBlockState(neighborPos);
            if (!isCompatibleBlock(blockPos, neighborState, neighborPos)){
                blockState = blockState.setValue(CONN1,Direction.DOWN);
            }
        }
        if (blockState.getValue(CONN2) != Direction.DOWN){
            BlockPos neighborPos = blockPos.relative(blockState.getValue(CONN2));
            BlockState neighborState = level.getBlockState(neighborPos);
            if (!isCompatibleBlock(blockPos, neighborState, neighborPos)){
                blockState = blockState.setValue(CONN2,Direction.DOWN);
            }
        }
        return blockState;
    }

    private boolean isCompatibleBlock(BlockPos blockPos, BlockState neighborState, BlockPos neighborPos) {
        if (!codex.isComponentBlock(neighborState)) return false;
        if (codex.isGateBlock(neighborState) && neighborPos.relative(neighborState.getValue(FACING)).equals(blockPos)) return true;
        if (neighborState.getBlock() instanceof MonoSigBus) {
            if (neighborState.getValue(CONN1) == Direction.DOWN || neighborState.getValue(CONN2) == Direction.DOWN) return true;
            else if (iamConn(blockPos, neighborPos, neighborState.getValue(CONN1)) ||
                    iamConn(blockPos, neighborPos, neighborState.getValue(CONN2))) return true;
        }
        System.out.println("Not Compatible");
        return false;
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState state = this.defaultBlockState().setValue(SIGNAL_1, false)
                .setValue(CONN1, Direction.DOWN)
                .setValue(CONN2, Direction.DOWN)
                .setValue(POWERSOURCES, 0)
                .setValue(ISSOURCE, false);

        BlockState newState = getNewConnections(state, pos, level);
        if (newState.getValue(CONN1) != Direction.DOWN){
            BlockState state1 = isPowered(newState,level.getBlockState(pos.relative(newState.getValue(CONN1))));
            if (state1.getValue(POWERSOURCES) > 0){
                newState = newState.setValue(POWERSOURCES, newState.getValue(POWERSOURCES) + 1).setValue(SIGNAL_1, true);
                if (codex.isGateBlock(state1)){}
            }
        }
        if (newState.getValue(CONN2) != Direction.DOWN){
            BlockState state2 = isPowered(newState, level.getBlockState(pos.relative(newState.getValue(CONN2))));
            if (state2.getValue(POWERSOURCES) > 0){
                newState = newState.setValue(POWERSOURCES, newState.getValue(POWERSOURCES) + 1).setValue(SIGNAL_1, true);
            }
        }

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
            level.neighborChanged(pos.relative(state.getValue(CONN1)),this, pos);
//            level.neighborChanged(pos.relative(state.getValue(CONN2)),this, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) return;
        BlockState neighborState = level.getBlockState(neighborPos);
        BlockState connectionState = getNewConnections(state, pos, level);
        if (!isCompatibleBlock(pos, neighborState,neighborPos)) return;
        BlockState poweredState = isPowered(state, neighborState);
        BlockState newState = state
                .setValue(CONN1, connectionState.getValue(CONN1))
                .setValue(CONN2, connectionState.getValue(CONN2))
                .setValue(POWERSOURCES, poweredState.getValue(POWERSOURCES))
                .setValue(SIGNAL_1, poweredState.getValue(SIGNAL_1))
                .setValue(ISSOURCE, poweredState.getValue(ISSOURCE));

        level.setBlock(pos, newState, 3);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isClientSide) return;

    }

    // Register the properties Ex. FACING
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIGNAL_1)
                .add(POWERSOURCES)
                .add(CONN1)
                .add(CONN2)
                .add(ISSOURCE);
    }
}
