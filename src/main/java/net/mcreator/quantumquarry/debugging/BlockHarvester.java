package net.mcreator.quantumquarry.debugging;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.TicketType;
import net.minecraftforge.common.world.ForgeChunkManager;

import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;

import net.mcreator.quantumquarry.block.entity.MinerBlockEntity;

public class BlockHarvester {
    private final WorldSimulator worldSimulator;

    public BlockHarvester(WorldSimulator worldSimulator) {
        this.worldSimulator = worldSimulator;
    }

    public List<ItemStack> mineChunk(MinerBlockEntity owner) {
        if (this.worldSimulator.server != null)
            this.worldSimulator.server.getPlayerList().broadcastSystemMessage(
                Component.literal("Generating Chunk..."), false
            );
        else
            throw new Error("Virtual Level is null or is a client side level");
        try {
            ChunkAccess chunk = worldSimulator.genChunk();
            List<ItemStack> items = new ArrayList<ItemStack>();
            if (this.worldSimulator.virtualDimension != null) {
                Level virtualLevel = this.worldSimulator.server.getLevel(this.worldSimulator.virtualDimension);
                if (virtualLevel != null) { 
                    for (int x = 0; x < 255; x++) {
                    for (int z = 0; z < 255; z++) {
                        for (int y = virtualLevel.getMaxBuildHeight() - 1; y >= virtualLevel.getMinBuildHeight(); y--) {
                                BlockPos blockPos = new BlockPos(chunk.getPos().getMinBlockX() + x, y, chunk.getPos().getMinBlockZ() + z);
                                BlockState blockState = chunk.getBlockState(blockPos);
                                owner.incrementBlocksMined();
                                owner.setQuarryLevel(y);
                                if (blockState.isAir()) continue;
                                items.addAll(simulateMining(virtualLevel,blockPos, blockState));
                            }
                        }
                    }
                } else {
                    virtualLevel = this.worldSimulator.server.getLevel(Level.OVERWORLD);
                    for (int x = 0; x < 255; x++) {
                        for (int z = 0; z < 255; z++) {
                            for (int y = 254; y >= 1; y--) {
                                BlockPos blockPos = new BlockPos(chunk.getPos().getMinBlockX() + x, y, chunk.getPos().getMinBlockZ() + z);
                                BlockState blockState = chunk.getBlockState(blockPos);
                                owner.incrementBlocksMined();
                                owner.setQuarryLevel(y);
                                if (blockState.isAir()) continue;
                                items.addAll(simulateMining(virtualLevel, blockPos, blockState));
                            }
                        }
                    }
                }
            } else {
                throw new Error("Virtual Dimension is null");
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<ItemStack> simulateMining(Level level, BlockPos blockPos, BlockState blockState) {
            List<ItemStack> drops = Block.getDrops(blockState, (ServerLevel)level, blockPos, null);
            //this.virtualLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
            return drops;
    }
}
