
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.quantumquarry.init;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;

import net.mcreator.quantumquarry.client.gui.QuantumMinerScreenScreen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class QuantumQuarryModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(QuantumQuarryModMenus.QUANTUM_MINER_SCREEN.get(), QuantumMinerScreenScreen::new);
		});
	}
}
