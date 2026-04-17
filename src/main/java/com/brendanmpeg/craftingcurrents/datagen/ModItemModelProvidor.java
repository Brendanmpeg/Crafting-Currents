package com.brendanmpeg.craftingcurrents.datagen;

import com.brendanmpeg.craftingcurrents.CraftingCurrents;
import com.brendanmpeg.craftingcurrents.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvidor extends ItemModelProvider {
    public ModItemModelProvidor(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CraftingCurrents.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.BASE_PLATE);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(CraftingCurrents.MODID, "item/" + item.getId().getPath()));
    }
}
