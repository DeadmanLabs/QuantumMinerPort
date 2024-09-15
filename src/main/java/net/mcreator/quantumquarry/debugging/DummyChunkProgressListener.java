package net.mcreator.quantumquarry.debugging;

import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public class DummyChunkProgressListener implements ChunkProgressListener {
	@Override
	public void updateSpawnPos(ChunkPos chunkPos) {

	}
	@Override
	public void onStatusChange(ChunkPos chunkPos, ChunkStatus newStatus) {

	}
	@Override
	public void stop() {

	}
	@Override
	public void start() {

	}
}