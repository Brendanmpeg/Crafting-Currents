package com.brendanmpeg.craftingcurrents.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class RelativeDirections {

    public final Direction front, back, left, right;

    public final BlockPos frontNeighbor, rearNeighbor, leftNeighbor, rightNeighbor;

    public RelativeDirections(BlockPos pos, Direction facing) {
        this.front = facing;
        this.back = facing.getOpposite();
        this.left = facing.getCounterClockWise();
        this.right = facing.getClockWise();

        this.frontNeighbor = pos.relative(front);
        this.rearNeighbor = pos.relative(back);
        this.leftNeighbor = pos.relative(left);
        this.rightNeighbor = pos.relative(right);
    }
}
