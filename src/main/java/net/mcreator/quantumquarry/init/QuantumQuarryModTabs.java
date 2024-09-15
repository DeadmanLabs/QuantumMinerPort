
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.quantumquarry.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.mcreator.quantumquarry.QuantumQuarryMod;

public class QuantumQuarryModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, QuantumQuarryMod.MODID);
	public static final RegistryObject<CreativeModeTab> QUANTUM_MINER = REGISTRY.register("quantum_miner",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.quantum_quarry.quantum_miner")).icon(() -> new ItemStack(QuantumQuarryModBlocks.MINER.get())).displayItems((parameters, tabData) -> {
				tabData.accept(QuantumQuarryModBlocks.QUARRY.get().asItem());
				tabData.accept(QuantumQuarryModBlocks.MINER.get().asItem());
				tabData.accept(QuantumQuarryModItems.MAGIC_SNOW_GLOBE.get());
				tabData.accept(QuantumQuarryModItems.SNOW_GLOBE.get());
				tabData.accept(QuantumQuarryModItems.BIOME_MARKER.get());
				tabData.accept(QuantumQuarryModBlocks.SINGLE_QUARRY_DBG.get().asItem());
			}).withSearchBar().build());
}
