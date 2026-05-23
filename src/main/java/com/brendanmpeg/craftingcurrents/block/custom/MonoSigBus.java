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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.brendanmpeg.craftingcurrents.block.custom.BiOrGate.FACING;


public class MonoSigBus extends Block {

    /* Minecraft States */
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    /* Custom States */

    public static final BooleanProperty SIGNAL_1 = BooleanProperty.create("signal_1");
//    public static final EnumProperty<SignalBusConnections> CONN = EnumProperty.create("my_property", SignalBusConnections.class);
public static final IntegerProperty CONN = IntegerProperty.create("conn",0, 12);
    public static final IntegerProperty CONN1 = IntegerProperty.create("conn1",0, 4);
    public static final IntegerProperty CONN2 = IntegerProperty.create("conn2",0, 4);
    private static final int PROPAGATION_DELAY = 1;


    //class constructor to set the default states
    public MonoSigBus(Properties properties) {
        super (properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SIGNAL_1, false).setValue(CONN, 0));
    }

    /* Shaping functions */
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    /* Logical functions */
    public void getConn(BlockState blockState, BlockPos blockPos, Level level) {
        Integer connection1 = blockState.getValue(CONN1);
        Integer connection2 = blockState.getValue(CONN2);
        BlockState southState = level.getBlockState(blockPos.south());
        BlockState northState = level.getBlockState(blockPos.north());
        BlockState eastState = level.getBlockState(blockPos.east());
        BlockState westState = level.getBlockState(blockPos.west());

        if (connection1.equals(0)) {
            if (isCompatibleBlock(blockPos, southState, blockPos.south())) {
                level.setBlock(blockPos, blockState.setValue(CONN1, 3), 3);
                System.out.println("Establishing a south connection");
            } else if (isCompatibleBlock(blockPos, northState, blockPos.north())) {
                level.setBlock(blockPos, blockState.setValue(CONN1, 1), 3);
                System.out.println("Establishing a north connection");
            } else if (isCompatibleBlock(blockPos, eastState, blockPos.east())) {
                level.setBlock(blockPos, blockState.setValue(CONN1, 2), 3);
                System.out.println("Establishing a east connection");
            } else if (isCompatibleBlock(blockPos, westState, blockPos.west())) {
                level.setBlock(blockPos, blockState.setValue(CONN1, 4), 3);
                System.out.println("Establishing a west connection");
            }
            System.out.println("Established connection 1");
            return;
        }
        if (connection2.equals(0)) {
            if (connection1 % 2 == 1) {
                if (isCompatibleBlock(blockPos, eastState, blockPos.east())) {
                    level.setBlock(blockPos, blockState.setValue(CONN2, 2), 3);
                    System.out.println("Establishing a east connection 1");
                } else if (isCompatibleBlock(blockPos, westState, blockPos.west())) {
                    level.setBlock(blockPos, blockState.setValue(CONN2, 4), 3);
                    System.out.println("Establishing a west connection 1");
                } else if (isCompatibleBlock(blockPos, southState, blockPos.south()) && connection1 != 3) {
                    level.setBlock(blockPos, blockState.setValue(CONN2, 3), 3);
                    System.out.println("Establishing a south connection 1");
                } else if (isCompatibleBlock(blockPos, northState, blockPos.north()) && connection1 != 1) {
                    level.setBlock(blockPos, blockState.setValue(CONN2, 1), 3);
                    System.out.println("Establishing a north connection 1");
                }
            } else if (connection1 % 2 == 0) {
                if (isCompatibleBlock(blockPos, southState, blockPos.south())) {
                    level.setBlock(blockPos, blockState.setValue(CONN2, 3), 3);
                    System.out.println("Establishing a south connection");
                } else if (isCompatibleBlock(blockPos, northState, blockPos.north())) {
                    level.setBlock(blockPos, blockState.setValue(CONN2, 1), 3);
                    System.out.println("Establishing a north connection");
                } else if (isCompatibleBlock(blockPos, eastState, blockPos.east())  && connection1 != 2) {
                    level.setBlock(blockPos, blockState.setValue(CONN2, 2), 3);
                    System.out.println("Establishing a east connection");
                } else if (isCompatibleBlock(blockPos, westState, blockPos.west())  && connection1 != 4) {
                    level.setBlock(blockPos, blockState.setValue(CONN2, 4), 3);
                    System.out.println("Establishing a west connection");
                }
            }
        }
    }

    private boolean isCompatibleBlock(BlockPos blockPos, BlockState neighborState, BlockPos neighborPos) {
        HashMap<Integer, Direction> directions = new HashMap<>();
        directions.put(1, Direction.NORTH);
        directions.put(3, Direction.SOUTH);
        directions.put(2, Direction.EAST);
        directions.put(4, Direction.WEST);

        if (!isComponentBlock(neighborState)) return false;
        if (isGateBlock(neighborState) && neighborPos.relative(neighborState.getValue(FACING)).equals(blockPos)) return true;
        if (neighborState.getBlock() instanceof MonoSigBus) {
            if (neighborState.getValue(CONN1) == 0 || neighborState.getValue(CONN2) == 0) return true;
            else if (neighborPos.relative(directions.get(neighborState.getValue(CONN1))).equals(blockPos)
            || neighborPos.relative(directions.get(neighborState.getValue(CONN2))).equals(blockPos)) {
                return true;
            }
        }
        System.out.println("Not Compatible");
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
        getConn(state, pos, level);
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
                .add(CONN)
                .add(CONN1)
                .add(CONN2);
    }
}
