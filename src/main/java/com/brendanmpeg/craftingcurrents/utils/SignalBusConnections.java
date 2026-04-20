package com.brendanmpeg.craftingcurrents.utils;

import net.minecraft.util.StringRepresentable;

public enum SignalBusConnections implements StringRepresentable {
    SE("se"),
    SW("sw"),
    NE("ne"),
    NW("nw"),
    NS("ns"),
    EW("ew"),
    NA("na");

    private final String name;

    SignalBusConnections(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
