
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.quantumquarry.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.mcreator.quantumquarry.block.SingleQuarryDbgBlock;
import net.mcreator.quantumquarry.block.QuarryBlock;
import net.mcreator.quantumquarry.block.MinerBlock;
import net.mcreator.quantumquarry.QuantumQuarryMod;

public class QuantumQuarryModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, QuantumQuarryMod.MODID);
	public static final RegistryObject<Block> QUARRY = REGISTRY.register("quarry", () -> new QuarryBlock());
	public static final RegistryObject<Block> MINER = REGISTRY.register("miner", () -> new MinerBlock());
	public static final RegistryObject<Block> SINGLE_QUARRY_DBG = REGISTRY.register("single_quarry_dbg", () -> new SingleQuarryDbgBlock());
}
