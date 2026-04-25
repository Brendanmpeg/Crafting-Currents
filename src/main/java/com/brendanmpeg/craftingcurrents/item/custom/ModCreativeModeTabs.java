package com.brendanmpeg.craftingcurrents.item.custom;

import com.brendanmpeg.craftingcurrents.CraftingCurrents;
import com.brendanmpeg.craftingcurrents.block.ModBlocks;

import com.brendanmpeg.craftingcurrents.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CraftingCurrents.MODID);

    public static final RegistryObject<CreativeModeTab> CRAFTING_CURRENTS_TAB =
            CREATIVE_MODE_TABS.register("crafting_currents",
                    () -> CreativeModeTab.builder()
//                            .withTabsBefore(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                            .icon(() -> ModBlocks.DIRECTIONAL_TEST_BLOCK.get().asItem().getDefaultInstance())
                            .title(Component.translatable("creativetab.craftingcurrents.craftingcurrents"))
                            .displayItems((parameters, output) -> {
                        // Add your items here
//                        output.accept(ModItems.YOUR_ITEM.get());
                        output.accept(ModItems.BASE_PLATE.get());

                        // Add your blocks here — the BlockItem is what goes in the tab
                        output.accept(ModBlocks.DIRECTIONAL_TEST_BLOCK.get());
                        output.accept(ModBlocks.BI_SIG_BUS.get());
                        output.accept(ModBlocks.BI_AND_GATE.get());
                        output.accept(ModBlocks.MONO_SIG_BUS.get());
                        output.accept(ModBlocks.BI_OR_GATE.get());

                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}