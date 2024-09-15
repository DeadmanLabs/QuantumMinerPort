
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.quantumquarry.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.mcreator.quantumquarry.item.SnowGlobeItem;
import net.mcreator.quantumquarry.item.MagicSnowGlobeItem;
import net.mcreator.quantumquarry.item.BiomeMarkerItem;
import net.mcreator.quantumquarry.QuantumQuarryMod;

public class QuantumQuarryModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, QuantumQuarryMod.MODID);
	public static final RegistryObject<Item> QUARRY = block(QuantumQuarryModBlocks.QUARRY);
	public static final RegistryObject<Item> MINER = block(QuantumQuarryModBlocks.MINER);
	public static final RegistryObject<Item> MAGIC_SNOW_GLOBE = REGISTRY.register("magic_snow_globe", () -> new MagicSnowGlobeItem());
	public static final RegistryObject<Item> SNOW_GLOBE = REGISTRY.register("snow_globe", () -> new SnowGlobeItem());
	public static final RegistryObject<Item> BIOME_MARKER = REGISTRY.register("biome_marker", () -> new BiomeMarkerItem());
	public static final RegistryObject<Item> SINGLE_QUARRY_DBG = block(QuantumQuarryModBlocks.SINGLE_QUARRY_DBG);

	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
