package com.happysg.createbiomechanical.registry;

import com.happysg.createbiomechanical.Biomechanical;
import com.happysg.createbiomechanical.content.station.StationBlock;
import com.happysg.createbiomechanical.content.station.StationBlockItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

import static com.happysg.createbiomechanical.Biomechanical.REGISTRATE;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class BMBlocks {

    public static final BlockEntry<StationBlock> STATION = REGISTRATE
            .block("station", StationBlock::new)
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .transform(axeOrPickaxe())
            .initialProperties(SharedProperties::softMetal)
            .addLayer(() -> RenderType::cutoutMipped)
            .item(StationBlockItem::new)
            .model(AssetLookup.customBlockItemModel("_", "item"))
            .build()
            .register();

    public static final BlockEntry<Block> POWER_CORE = REGISTRATE
            .block("power_core", Block::new)
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
            .transform(axeOrPickaxe())
            .initialProperties(SharedProperties::softMetal)
            .addLayer(() -> RenderType::cutoutMipped)
            .simpleItem()
            .register();

    public static final BlockEntry<Block> BIONIC_CASING = REGISTRATE
            .block("bionic_casing", Block::new)
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
            .transform(axeOrPickaxe())
            .initialProperties(SharedProperties::softMetal)
            .addLayer(() -> RenderType::cutoutMipped)
            .simpleItem()
            .register();

    public static void register() {
        Biomechanical.LOGGER.info("Registering Blocks for " + Biomechanical.MODID);
    }
}
