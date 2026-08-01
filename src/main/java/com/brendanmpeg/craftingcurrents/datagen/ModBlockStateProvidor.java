package com.brendanmpeg.craftingcurrents.datagen;

import com.brendanmpeg.craftingcurrents.CraftingCurrents;
import com.brendanmpeg.craftingcurrents.block.ModBlocks;
import com.brendanmpeg.craftingcurrents.block.custom.MonoSigBus;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvidor extends BlockStateProvider {
    private record CornerCase(Direction conn1, Direction conn2, int rotation) {}

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
        buildMonoSignalBus(ModBlocks.MONO_SIG_BUS.get());
        buildItemModel("mono_sig_bus", "block/mono_sig_bus_straight_off");
        buildItemModel("bi_sig_bus", "block/bi_sig_bus_l0_r0");
    }

    private void buildMonoSignalBus(Block block) {
        String bottomTexture = "block/base_plate";
        String sideTexture = "block/directional_test_block_side";

        // Generate all four model variants via buildComponentModel.
        // Each gets its own texture set — geometry is identical, textures differ.
        ModelFile straightOff = buildComponentModel(
                "block/mono_sig_bus_straight_off", sideTexture, "block/mono_sig_bus_straight_off", bottomTexture);
        ModelFile straightOn = buildComponentModel(
                "block/mono_sig_bus_straight_on", sideTexture, "block/mono_sig_bus_straight_on", bottomTexture);
        ModelFile cornerOff = buildComponentModel(
                "block/mono_sig_bus_corner_off", sideTexture,"block/mono_sig_bus_corner_off", bottomTexture);
        ModelFile cornerOn = buildComponentModel(
                "block/mono_sig_bus_corner_on", sideTexture,"block/mono_sig_bus_corner_on", bottomTexture);

        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);

        for (boolean signal : new boolean[]{false, true}) {
            ModelFile straight = signal ? straightOn : straightOff;
            ModelFile corner   = signal ? cornerOn   : cornerOff;

            // ── CONN1 = 0 (fully disconnected) ───────────────────────────────
            builder.part()
                    .modelFile(straight).rotationY(0).addModel()
                    .condition(MonoSigBus.SIGNAL, signal)
                    .condition(MonoSigBus.CONN1,  Direction.DOWN)
                    .end();

            // ── CONN1 != 0, CONN2 = 0 (single arm) ───────────────────────────
            // N(1) or S(3) → Y=0
            for (Direction c1 : new Direction[]{Direction.NORTH, Direction.SOUTH}) {
                builder.part()
                        .modelFile(straight).rotationY(0).addModel()
                        .condition(MonoSigBus.SIGNAL, signal)
                        .condition(MonoSigBus.CONN1, c1)
                        .condition(MonoSigBus.CONN2, Direction.DOWN)
                        .end();
            }
            // E(2) or W(4) → Y=90
            for (Direction c1 : new Direction[]{Direction.EAST, Direction.WEST}) {
                builder.part()
                        .modelFile(straight).rotationY(90).addModel()
                        .condition(MonoSigBus.SIGNAL, signal)
                        .condition(MonoSigBus.CONN1, c1)
                        .condition(MonoSigBus.CONN2, Direction.DOWN)
                        .end();
            }

            // ── Both connected, same plane (straight) ─────────────────────────
            for (Direction[] pair : new Direction[][]{{Direction.NORTH, Direction.SOUTH}, {Direction.SOUTH, Direction.NORTH}}) {
                builder.part()
                        .modelFile(straight).rotationY(0).addModel()
                        .condition(MonoSigBus.SIGNAL, signal)
                        .condition(MonoSigBus.CONN1, pair[0])
                        .condition(MonoSigBus.CONN2, pair[1])
                        .end();
            }
            for (Direction[] pair : new Direction[][]{{Direction.EAST, Direction.WEST}, {Direction.WEST, Direction.EAST}}) {
                builder.part()
                        .modelFile(straight).rotationY(90).addModel()
                        .condition(MonoSigBus.SIGNAL, signal)
                        .condition(MonoSigBus.CONN1, pair[0])
                        .condition(MonoSigBus.CONN2, pair[1])
                        .end();
            }

            // ── Both connected, different planes (corner) ─────────────────────
            CornerCase[] cornerCases = {
                    new CornerCase(Direction.NORTH, Direction.EAST,  270),
                    new CornerCase(Direction.EAST,  Direction.NORTH, 270),
                    new CornerCase(Direction.EAST,  Direction.SOUTH,   0),
                    new CornerCase(Direction.SOUTH, Direction.EAST,    0),
                    new CornerCase(Direction.SOUTH, Direction.WEST,   90),
                    new CornerCase(Direction.WEST,  Direction.SOUTH,  90),
                    new CornerCase(Direction.WEST,  Direction.NORTH, 180),
                    new CornerCase(Direction.NORTH, Direction.WEST,  180),
            };
            for (CornerCase c : cornerCases) {
                builder.part()
                        .modelFile(corner).rotationY(c.rotation).addModel()
                        .condition(MonoSigBus.SIGNAL, signal)
                        .condition(MonoSigBus.CONN1, c.conn1)
                        .condition(MonoSigBus.CONN2, c.conn2)
                        .end();
            }
        }
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

    private ItemModelBuilder buildItemModel(String modelName, String parentModel) {
        return itemModels().withExistingParent(modelName, modLoc(parentModel));
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