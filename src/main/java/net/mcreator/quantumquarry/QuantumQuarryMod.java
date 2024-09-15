/*
 *    MCreator note:
 *
 *    If you lock base mod element files, you can edit this file and it won't get overwritten.
 *    If you change your modid or package, you need to apply these changes to this file MANUALLY.
 *
 *    Settings in @Mod annotation WON'T be changed in case of the base mod element
 *    files lock too, so you need to set them manually here in such case.
 *
 *    If you do not lock base mod element files in Workspace settings, this file
 *    will be REGENERATED on each build.
 *
 */
package net.mcreator.quantumquarry;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.mcreator.quantumquarry.init.QuantumQuarryModTabs;
import net.mcreator.quantumquarry.item.SnowGlobeItem;
import net.mcreator.quantumquarry.init.QuantumQuarryModMenus;
import net.mcreator.quantumquarry.init.QuantumQuarryModItems;
import net.mcreator.quantumquarry.init.QuantumQuarryModBlocks;
import net.mcreator.quantumquarry.debugging.WorldSimulator;
import net.mcreator.quantumquarry.init.QuantumQuarryModBlockEntities;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

@Mod("quantum_quarry")
public class QuantumQuarryMod {
	public static final Logger LOGGER = LogManager.getLogger(QuantumQuarryMod.class);
	public static final String MODID = "quantum_quarry";

	public QuantumQuarryMod() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);

		QuantumQuarryModBlocks.REGISTRY.register(bus);
		QuantumQuarryModBlockEntities.REGISTRY.register(bus);
		QuantumQuarryModItems.REGISTRY.register(bus);

		QuantumQuarryModTabs.REGISTRY.register(bus);

		QuantumQuarryModMenus.REGISTRY.register(bus);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		// Register other setup tasks here
	}

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		workQueue.add(new AbstractMap.SimpleEntry(action, tick));
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		}
	}
	
	@Mod.EventBusSubscriber(modid = MODID)
	public class CommonEvents {
		@SubscribeEvent
		public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
			Player player = event.player;
			for (ItemStack stack : player.getInventory().items) {
				if (stack.getItem() instanceof SnowGlobeItem snowGlobeItem) {
					snowGlobeItem.checkBiomeVisit(player,  stack);
				}
			}
		}
	}

	@Mod.EventBusSubscriber(modid = QuantumQuarryMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public class ModEventHandler {

		@SubscribeEvent
		public static void onCommonSetup(FMLCommonSetupEvent event) {
			event.enqueueWork(() -> {
				WorldSimulator.registerVirtualDimension();
			});
		}
	}
}
