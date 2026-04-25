package com.brendanmpeg.craftingcurrents.datagen;

import com.brendanmpeg.craftingcurrents.CraftingCurrents;
import com.brendanmpeg.craftingcurrents.block.ModBlocks;
import com.brendanmpeg.craftingcurrents.block.custom.DirectionalTestBlock;
import com.brendanmpeg.craftingcurrents.block.custom.MonoSigBus;
import com.brendanmpeg.craftingcurrents.utils.SignalBusConnections;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvidor extends BlockStateProvider {

    public ModBlockStateProvidor(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CraftingCurrents.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
//        blockWithItem(ModBlocks.BI_AND_GATE);
//        blockWithItem(ModBlocks.BI_OR_GATE);
//        blockWithItem(ModBlocks.MONO_SIG_BUS);
//        blockWithItem(ModBlocks.BI_SIG_BUS);
        blockWithItem(ModBlocks.DIRECTIONAL_TEST_BLOCK);
        buildMonoSignalBus();
    }

    private void buildMonoSignalBus() {
        Block block = ModBlocks.MONO_SIG_BUS.get();
        VariantBlockStateBuilder builder = getVariantBuilder(block);
        String bottomTexture = "block/base_plate";
        String sideTexture = "block/directional_test_block_side";

        for (boolean SIGNAL_1 : new boolean[]{false, true}) {
            String suffix = SIGNAL_1 ? "on" : "off";
            String straightTopTexture = "block/mono_sig_bus_" + suffix + "_straight";
            String cornerTopTexture = "block/mono_sig_bus_" + suffix + "_corner";

            ModelFile straight = buildComponentModel("mono_sig_bus_straight_" + suffix, sideTexture, straightTopTexture, bottomTexture);
            ModelFile corner   = buildComponentModel("mono_sig_bus_corner_" + suffix, sideTexture, cornerTopTexture, bottomTexture);

            // Straight variants — just rotation, same model
            builder.partialState()
                    .with(MonoSigBus.CONN, SignalBusConnections.NS)
                    .with(MonoSigBus.SIGNAL_1, SIGNAL_1)
                    .modelForState().modelFile(straight).rotationY(0).addModel();

            builder.partialState()
                    .with(MonoSigBus.CONN, SignalBusConnections.NA)
                    .with(MonoSigBus.SIGNAL_1, SIGNAL_1)
                    .modelForState().modelFile(straight).rotationY(0).addModel();

            builder.partialState()
                    .with(MonoSigBus.CONN, SignalBusConnections.EW)
                    .with(MonoSigBus.SIGNAL_1, SIGNAL_1)
                    .modelForState().modelFile(straight).rotationY(90).addModel();

            // Corner variants
            builder.partialState()
                    .with(MonoSigBus.CONN, SignalBusConnections.SE)
                    .with(MonoSigBus.SIGNAL_1, SIGNAL_1)
                    .modelForState().modelFile(corner).rotationY(0).addModel();

            builder.partialState()
                    .with(MonoSigBus.CONN, SignalBusConnections.SW)
                    .with(MonoSigBus.SIGNAL_1, SIGNAL_1)
                    .modelForState().modelFile(corner).rotationY(90).addModel();

            builder.partialState()
                    .with(MonoSigBus.CONN, SignalBusConnections.NW)
                    .with(MonoSigBus.SIGNAL_1, SIGNAL_1)
                    .modelForState().modelFile(corner).rotationY(180).addModel();

            builder.partialState()
                    .with(MonoSigBus.CONN, SignalBusConnections.NE)
                    .with(MonoSigBus.SIGNAL_1, SIGNAL_1)
                    .modelForState().modelFile(corner).rotationY(270).addModel();
        }
        registerItemModel(ModBlocks.MONO_SIG_BUS, "block/mono_sig_bus_straight_off");
    }

    private ModelFile buildComponentModel(String modelName, String sideTexturePath, String topTexturePath, String bottomTexturePath) {
        return models()
                .withExistingParent(modelName, "minecraft:block/block")
                .texture("base", modLoc(sideTexturePath))
                .texture("top", modLoc(topTexturePath))
                .texture("bottom", modLoc(bottomTexturePath))
                .texture("particle", modLoc(sideTexturePath))
                .element()
                .from(0, 0, 0)
                .to(16, 2, 16)
                .face(Direction.UP)
                .uvs(0, 0, 16, 16).texture("#top").end()
                .face(Direction.DOWN)
                .uvs(0, 0, 16, 16).texture("#bottom").end()
                .face(Direction.NORTH)
                .uvs(0, 14, 16, 16).texture("#base").end()
                .face(Direction.SOUTH)
                .uvs(0, 14, 16, 16).texture("#base").end()
                .face(Direction.EAST)
                .uvs(0, 14, 16, 16).texture("#base").end()
                .face(Direction.WEST)
                .uvs(0, 14, 16, 16).texture("#base").end()
                .end();
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject){
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void registerItemModel(RegistryObject<Block> block, String blockModelPath) {
        itemModels().withExistingParent(
                ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc(blockModelPath)
        );
    }
}