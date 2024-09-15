package net.mcreator.quantumquarry.procedures;

import net.mcreator.quantumquarry.init.QuantumQuarryModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.network.NetworkHooks;

public class MinerFindCore {
	public static double[] execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return new double[3];
		double rootX = 0;
		double rootY = 0;
		double rootZ = 0;
		boolean rootFound = true;
		if ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == QuantumQuarryModBlocks.QUARRY.get()) {
			rootX = x + 1;
			rootY = y;
			rootZ = z;
			rootFound = true;
		} else if ((world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == QuantumQuarryModBlocks.QUARRY.get()) {
			rootX = x - 1;
			rootY = y;
			rootZ = z;
			rootFound = true;
		} else if ((world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == QuantumQuarryModBlocks.QUARRY.get()) {
			rootX = x;
			rootY = y + 1;
			rootZ = z;
			rootFound = true;
		} else if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == QuantumQuarryModBlocks.QUARRY.get()) {
			rootX = x;
			rootY = y - 1;
			rootZ = z;
			rootFound = true;
		} else if ((world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == QuantumQuarryModBlocks.QUARRY.get()) {
			rootX = x;
			rootY = y;
			rootZ = z + 1;
			rootFound = true;
		} else if ((world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == QuantumQuarryModBlocks.QUARRY.get()) {
			rootX = x;
			rootY = y;
			rootZ = z - 1;
			rootFound = true;
		}
		if (rootFound) {
			return new double[] { rootX, rootY, rootZ };
		} else {
			return new double[3];
		}
	}
	
	public static boolean validateStructure(LevelAccessor world, double rootX, double rootY, double rootZ, Entity entity) {
		return ((world.getBlockState(BlockPos.containing(rootX + 1, rootY, rootZ))).getBlock() == QuantumQuarryModBlocks.MINER.get()
			&& (world.getBlockState(BlockPos.containing(rootX - 1, rootY, rootZ))).getBlock() == QuantumQuarryModBlocks.MINER.get()
			&& (world.getBlockState(BlockPos.containing(rootX, rootY + 1, rootZ))).getBlock() == QuantumQuarryModBlocks.MINER.get()
			&& (world.getBlockState(BlockPos.containing(rootX, rootY - 1, rootZ))).getBlock() == QuantumQuarryModBlocks.MINER.get()
			&& (world.getBlockState(BlockPos.containing(rootX, rootY, rootZ + 1))).getBlock() == QuantumQuarryModBlocks.MINER.get()
			&& (world.getBlockState(BlockPos.containing(rootX, rootY, rootZ - 1))).getBlock() == QuantumQuarryModBlocks.MINER.get());
	}
}
