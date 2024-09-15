
package net.mcreator.quantumquarry.network;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.mcreator.quantumquarry.world.inventory.QuantumMinerScreenMenu;
import net.mcreator.quantumquarry.QuantumQuarryMod;
import net.mcreator.quantumquarry.block.entity.MinerBlockEntity;

import java.util.function.Supplier;
import java.util.HashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class QuantumMinerScreenButtonMessage {
	private final int buttonID, x, y, z;

	public QuantumMinerScreenButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public QuantumMinerScreenButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(QuantumMinerScreenButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handler(QuantumMinerScreenButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int buttonID = message.buttonID;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			handleButtonAction(entity, buttonID, x, y, z);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		HashMap guistate = QuantumMinerScreenMenu.guistate;
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {
			MinerBlockEntity miner = (MinerBlockEntity) world.getBlockEntity(new BlockPos(x, y, z));
			if (miner != null) {
				switch (miner.mode) {
					case "Always":
						miner.mode = "High";
						break;
					case "High":
						miner.mode = "Low";
						break;
					case "Low":
						miner.mode = "Always";
						break;
				}
			}
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		QuantumQuarryMod.addNetworkMessage(QuantumMinerScreenButtonMessage.class, QuantumMinerScreenButtonMessage::buffer, QuantumMinerScreenButtonMessage::new, QuantumMinerScreenButtonMessage::handler);
	}
}
