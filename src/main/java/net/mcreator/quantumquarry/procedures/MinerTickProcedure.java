package net.mcreator.quantumquarry.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

import net.mcreator.quantumquarry.init.QuantumQuarryModBlocks;

public class MinerTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		boolean rootFound = false;
		double rootX = 0;
		double rootY = 0;
		double rootZ = 0;
		double tickCount = 0;
		if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == QuantumQuarryModBlocks.QUARRY.get()) {
			rootX = x;
			rootY = y;
			rootZ = z;
			rootFound = true;
		} else {
			if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == QuantumQuarryModBlocks.QUARRY.get()) {
				rootX = x - 1;
				rootY = y;
				rootZ = z;
				rootFound = true;
			} else {
				if ((world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == QuantumQuarryModBlocks.QUARRY.get()) {
					rootX = x;
					rootY = y + 1;
					rootZ = z;
					rootFound = true;
				} else {
					if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == QuantumQuarryModBlocks.QUARRY.get()) {
						rootX = x;
						rootY = y - 1;
						rootZ = z;
						rootFound = true;
					} else {
						if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == QuantumQuarryModBlocks.QUARRY.get()) {
							rootX = x;
							rootY = y;
							rootZ = z + 1;
							rootFound = true;
						} else {
							if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == QuantumQuarryModBlocks.QUARRY.get()) {
								rootX = x;
								rootY = y;
								rootZ = z - 1;
								rootFound = true;
							} else {
								rootFound = false;
							}
						}
					}
				}
			}
		}
		if (rootFound == true && (world.getBlockState(BlockPos.containing(rootX + 1, rootY, rootZ))).getBlock() == QuantumQuarryModBlocks.MINER.get()
				&& (world.getBlockState(BlockPos.containing(rootX - 1, rootY, rootZ))).getBlock() == QuantumQuarryModBlocks.MINER.get()
				&& (world.getBlockState(BlockPos.containing(rootX, rootY + 1, rootZ))).getBlock() == QuantumQuarryModBlocks.MINER.get()
				&& (world.getBlockState(BlockPos.containing(rootX, rootY - 1, rootZ))).getBlock() == QuantumQuarryModBlocks.MINER.get()
				&& (world.getBlockState(BlockPos.containing(rootX, rootY, rootZ + 1))).getBlock() == QuantumQuarryModBlocks.MINER.get()
				&& (world.getBlockState(BlockPos.containing(rootX, rootY, rootZ - 1))).getBlock() == QuantumQuarryModBlocks.MINER.get()) {
			tickCount = tickCount + 1;
		}
	}
}
