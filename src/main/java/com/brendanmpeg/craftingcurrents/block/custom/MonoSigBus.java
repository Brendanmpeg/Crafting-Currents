package com.brendanmpeg.craftingcurrents.block.custom;

import com.brendanmpeg.craftingcurrents.utils.ModTags;
import com.brendanmpeg.craftingcurrents.utils.RelativeDirections;
import com.brendanmpeg.craftingcurrents.utils.SignalBusConnections;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;

import static com.brendanmpeg.craftingcurrents.block.custom.BiOrGate.FACING;


public class MonoSigBus extends Block {

    /* Minecraft States */
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    /* Custom States */

    public static final BooleanProperty SIGNAL_1 = BooleanProperty.create("signal_1");
    public static final EnumProperty<SignalBusConnections> CONN = EnumProperty.create("my_property", SignalBusConnections.class);
    private static final int PROPAGATION_DELAY = 1;


    //class constructor to set the default states
    public MonoSigBus(Properties properties) {
        super (properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SIGNAL_1, false).setValue(CONN, SignalBusConnections.NA));
    }

    /* Shaping functions */
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    /* Logical functions */
    public void getConn(BlockState blockState, BlockPos blockPos, Level level) {
        SignalBusConnections connection = blockState.getValue(CONN);
        BlockState southState = level.getBlockState(blockPos.south());
        BlockState northState = level.getBlockState(blockPos.north());
        BlockState eastState = level.getBlockState(blockPos.east());
        BlockState westState = level.getBlockState(blockPos.west());

        if (connection.equals(SignalBusConnections.NA)) {
            if (isCompatibleBlock(blockPos, southState, blockPos.south()) && isCompatibleBlock(blockPos, eastState, blockPos.east())) {
                System.out.println("Establishing a South East connection");
                level.setBlock(blockPos, blockState.setValue(CONN, SignalBusConnections.SE), 3);
            } else if (isCompatibleBlock(blockPos, southState, blockPos.south()) && isCompatibleBlock(blockPos, westState, blockPos.west())) {
                System.out.println("Establishing a South West connection");
                level.setBlock(blockPos, blockState.setValue(CONN, SignalBusConnections.SW), 3);
            } else if (isCompatibleBlock(blockPos, northState, blockPos.north()) && isCompatibleBlock(blockPos, eastState, blockPos.east())) {
                System.out.println("Establishing a North East connection");
                level.setBlock(blockPos, blockState.setValue(CONN, SignalBusConnections.NE), 3);
            } else if (isCompatibleBlock(blockPos, northState, blockPos.north()) && isCompatibleBlock(blockPos, westState, blockPos.west())) {
                System.out.println("Establishing a North West connection");
                level.setBlock(blockPos, blockState.setValue(CONN, SignalBusConnections.NW), 3);
            } else if (isCompatibleBlock(blockPos, northState, blockPos.north()) && isCompatibleBlock(blockPos, southState, blockPos.south())) {
                System.out.println("Establishing a North South connection");
                level.setBlock(blockPos, blockState.setValue(CONN, SignalBusConnections.NS), 3);
            } else if (isCompatibleBlock(blockPos, eastState, blockPos.east()) && isCompatibleBlock(blockPos, westState, blockPos.west())) {
                System.out.println("Establishing a East West connection");
                level.setBlock(blockPos, blockState.setValue(CONN, SignalBusConnections.EW), 3);
            }
        } else if (connection.equals(SignalBusConnections.SE)) {
            if (!isCompatibleBlock(blockPos, southState, blockPos.south()) || !isCompatibleBlock(blockPos, eastState, blockPos.east())) {
                System.out.println("Removing a South East connection");
                level.setBlock(blockPos, blockState.setValue(CONN, SignalBusConnections.NA), 3);
            }
        } else if (connection.equals(SignalBusConnections.SW)) {
            if (!isCompatibleBlock(blockPos, southState, blockPos.south()) || !isCompatibleBlock(blockPos, westState, blockPos.west())) {
                System.out.println("Removing a South West connection");
                level.setBlock(blockPos, blockState.setValue(CONN, SignalBusConnections.NA), 3);
            }
        } else if (connection.equals(SignalBusConnections.NE)) {
            if (!isCompatibleBlock(blockPos, northState, blockPos.north()) || !isCompatibleBlock(blockPos, eastState, blockPos.east())) {
                System.out.println("Removing a North East connection");
                level.setBlock(blockPos, blockState.setValue(CONN, SignalBusConnections.NA), 3);
            }
        } else if (connection.equals(SignalBusConnections.NW)) {
            if (!isCompatibleBlock(blockPos, northState, blockPos.north()) || !isCompatibleBlock(blockPos, westState, blockPos.west())) {
                System.out.println("Removing a South East connection");
                level.setBlock(blockPos, blockState.setValue(CONN, SignalBusConnections.NA), 3);
            }
        } else if (connection.equals(SignalBusConnections.NS)) {

            if (!isCompatibleBlock(blockPos, northState, blockPos.north()) || !isCompatibleBlock(blockPos, southState, blockPos.south())) {
                System.out.println("Removing a North South connection");
                level.setBlock(blockPos, blockState.setValue(CONN, SignalBusConnections.NA), 3);
            }
        } else if (connection.equals(SignalBusConnections.EW)) {
            if (!isCompatibleBlock(blockPos, eastState, blockPos.east()) || !isCompatibleBlock(blockPos, westState, blockPos.west())) {
                System.out.println("Removing a East West connection");
                level.setBlock(blockPos, blockState.setValue(CONN, SignalBusConnections.NA), 3);
            }
        }
    }

    private boolean isCompatibleBlock(BlockPos blockPos, BlockState neighborState, BlockPos neighborPos) {
        if (!isComponentBlock(neighborState)) return false;
        if (neighborState.getBlock() instanceof MonoSigBus) return true;
        if (isGateBlock(neighborState) && neighborPos.relative(neighborState.getValue(FACING)).equals(blockPos)) return true;

        return false;
    }

    private boolean isComponentBlock(BlockState blockState) {
        return blockState.is(ModTags.Blocks.CRAFTING_CURRENTS_COMPONENT);
    }

    private boolean isGateBlock(BlockState blockState) {
        return blockState.is(ModTags.Blocks.CRAFTING_CURRENTS_GATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        Direction facingDirection = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(SIGNAL_1, false);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (level.isClientSide) return;
        level.scheduleTick(pos, this, PROPAGATION_DELAY, TickPriority.HIGH);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && !newState.is(this)) {
            // Notify the block directly in front that we're gone
            RelativeDirections relativeDirections = new RelativeDirections(pos, Direction.NORTH);
            level.neighborChanged(relativeDirections.frontNeighbor,this, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) return;

        RelativeDirections relativePositions = new RelativeDirections(pos, Direction.NORTH);
        BlockState neighborState = level.getBlockState(neighborPos);
        getConn(state, pos, level);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

    }

    // Register the properties Ex. FACING
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIGNAL_1)
                .add(CONN);
    }
}
