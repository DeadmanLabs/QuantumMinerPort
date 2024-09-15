
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.quantumquarry.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import net.mcreator.quantumquarry.world.inventory.QuantumMinerScreenMenu;
import net.mcreator.quantumquarry.QuantumQuarryMod;

public class QuantumQuarryModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, QuantumQuarryMod.MODID);
	public static final RegistryObject<MenuType<QuantumMinerScreenMenu>> QUANTUM_MINER_SCREEN = REGISTRY.register("quantum_miner_screen", () -> IForgeMenuType.create(QuantumMinerScreenMenu::new));
}
