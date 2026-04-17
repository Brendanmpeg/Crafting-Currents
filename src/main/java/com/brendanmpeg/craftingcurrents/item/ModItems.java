package com.brendanmpeg.craftingcurrents.item;

import com.brendanmpeg.craftingcurrents.CraftingCurrents;
import com.brendanmpeg.craftingcurrents.item.custom.BasePlate;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CraftingCurrents.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final RegistryObject<Item> CONFIGURATION_TOOL = ITEMS.register("configuration_tool",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BASE_PLATE = ITEMS.register("base_plate",
            () -> new BasePlate(new Item.Properties()));
}
