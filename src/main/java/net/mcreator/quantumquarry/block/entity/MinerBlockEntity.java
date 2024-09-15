package net.mcreator.quantumquarry.block.entity;

import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.Capability;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.RandomSource;

import net.mcreator.quantumquarry.world.inventory.QuantumMinerScreenMenu;
import net.mcreator.quantumquarry.debugging.BlockHarvester;
import net.mcreator.quantumquarry.debugging.WorldSimulator;
import net.mcreator.quantumquarry.init.QuantumQuarryModBlockEntities;

import javax.annotation.Nullable;

import org.antlr.v4.parse.ANTLRParser.finallyClause_return;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.IntStream;
import java.util.concurrent.atomic.AtomicBoolean;

import io.netty.buffer.Unpooled;

public class MinerBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MinerBlockEntity.class);
	private NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
	private final LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());

	private WorldSimulator worldSimulator;
	public String mode = "Always";
	private int blocksMined = 0;
	private int quarryLevel = 0;
	private String biome = "None";

	public MinerBlockEntity(BlockPos position, BlockState state) {
		super(QuantumQuarryModBlockEntities.MINER.get(), position, state);
		this.worldSimulator = new WorldSimulator();
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		if (!this.tryLoadLootTable(compound))
			this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.stacks);
		if (compound.get("energyStorage") instanceof IntTag intTag)
			energyStorage.deserializeNBT(intTag);
		if (compound.get("fluidTank") instanceof CompoundTag compoundTag)
			fluidTank.readFromNBT(compoundTag);
		if (compound.contains("redstoneMode"))
			compound.putInt("redstoneMode", redstoneMode);
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		if (!this.trySaveLootTable(compound)) {
			ContainerHelper.saveAllItems(compound, this.stacks);
		}
		compound.put("energyStorage", energyStorage.serializeNBT());
		compound.put("fluidTank", fluidTank.writeToNBT(new CompoundTag()));
		compound.putInt("redstoneMode", redstoneMode);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithFullMetadata();
	}

	@Override
	public int getContainerSize() {
		return stacks.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.stacks)
			if (!itemstack.isEmpty())
				return false;
		return true;
	}

	@Override
	public Component getDefaultName() {
		return Component.literal("miner");
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new QuantumMinerScreenMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
	}

	@Override
	public Component getDisplayName() {
		return Component.literal("Miner");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.stacks;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> stacks) {
		this.stacks = stacks;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return IntStream.range(0, this.getContainerSize()).toArray();
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
		return this.canPlaceItem(index, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		if (index == 0)
			return false;
		if (index == 1)
			return false;
		return true;
	}

	private final EnergyStorage energyStorage = new EnergyStorage(400000, 20000, 0, 0) {
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			int retval = super.receiveEnergy(maxReceive, simulate);
			if (!simulate) {
				setChanged();
				level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
			}
			return retval;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			int retval = super.extractEnergy(maxExtract, simulate);
			if (!simulate) {
				setChanged();
				level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
			}
			return retval;
		}
	};
	private final FluidTank fluidTank = new FluidTank(8000) {
		@Override
		protected void onContentsChanged() {
			super.onContentsChanged();
			setChanged();
			level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
		}
	};
	private int redstoneMode = 0;

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == ForgeCapabilities.ITEM_HANDLER)
			return handlers[facing.ordinal()].cast();
		if (!this.remove && capability == ForgeCapabilities.ENERGY)
			return LazyOptional.of(() -> energyStorage).cast();
		if (!this.remove && capability == ForgeCapabilities.FLUID_HANDLER)
			return LazyOptional.of(() -> fluidTank).cast();
		return super.getCapability(capability, facing);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		for (LazyOptional<? extends IItemHandler> handler : handlers)
			handler.invalidate();
	}

	public WorldSimulator getQuarryWorld() {
		if (this.worldSimulator == null) {
			this.worldSimulator = new WorldSimulator();
		}
		return this.worldSimulator;
	}

	public int getBlocksMined() {
		return this.blocksMined;
	}

	public int getQuarryLevel() {
		return this.quarryLevel;
	}

	public String getBiome() {
		return this.biome;
	}

	public void incrementBlocksMined() {
		this.blocksMined++;
	}

	public void setQuarryLevel(int level) {
		this.quarryLevel = level;
	}

	public void setBiome(String biome) {
		this.biome = biome;
	}

	public void mineChunk(BlockEntity chest) {
		return;
	}

	private void MineAndDrop(BlockEntity chest) {
		BlockHarvester blockHarvester = new BlockHarvester(this.worldSimulator);
		List<ItemStack> minedItems = blockHarvester.mineChunk(this);
		chest.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
			if (capability instanceof IItemHandlerModifiable) {
				IItemHandlerModifiable itemHandler = (IItemHandlerModifiable)capability;
				for (ItemStack mined : minedItems) {
					ItemStack remaining = mined.copy();
					for (int i = 0; i < itemHandler.getSlots(); i++) {
						itemHandler.insertItem(i, remaining, false);
						if (remaining.isEmpty()) {
							break;
						}
					}
				}
			}
		});
	}

	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		if (this.level instanceof ServerLevel serverLevel) {
			if (!serverLevel.isClientSide() && serverLevel.getServer() != null) {
				BlockPos[] adjacentPositions = new BlockPos[] {
					pos.north(), pos.south(), pos.east(), pos.west(), pos.above(), pos.below()
				};
				AtomicBoolean foundSpace = new AtomicBoolean(false);
				for (BlockPos adjacentPos : adjacentPositions) {
					BlockEntity blockEntity = serverLevel.getBlockEntity(adjacentPos);
					if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).isPresent()) {
						blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
							if (capability instanceof IItemHandlerModifiable) {
								IItemHandlerModifiable itemHandler = (IItemHandlerModifiable)capability;
								for (int i = 0; i < itemHandler.getSlots(); i++) {
									if (itemHandler.getStackInSlot(i).isEmpty()) {
										foundSpace.set(true);
										break;
									}
								}
							}
						});
						if (foundSpace.get()) {
							//Mine and drop
							serverLevel.getServer().getPlayerList().broadcastSystemMessage(
								Component.literal("Mining..."), false
							);
							if (this.mode == "Always") {
								this.MineAndDrop(blockEntity);
								serverLevel.getServer().getPlayerList().broadcastSystemMessage(
									Component.literal("Mined and dropped at " + adjacentPos.toShortString()), false
								);
							}
							break;
						} else {
							serverLevel.getServer().getPlayerList().broadcastSystemMessage(
								Component.literal("No space in chest at " + adjacentPos.toShortString()), false
							);
						}
					}
				}
				if (!foundSpace.get()) {
					serverLevel.getServer().getPlayerList().broadcastSystemMessage(
						Component.literal("Tick from Quarry"), false
					);
				}
			}
		}
	}

	public BlockPos getPos() {
		return this.worldPosition;
	}
}
