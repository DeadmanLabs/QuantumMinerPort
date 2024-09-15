package net.mcreator.quantumquarry.debugging;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.RandomSequences;

import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraftforge.registries.IForgeRegistry;

public class WorldSimulator {
	public final MinecraftServer server;
	public final Minecraft client;
	public final ResourceKey<Level> virtualDimension;
	private final ChunkGenerator virtualGenerator;

	public WorldSimulator() {
		this.server = ServerLifecycleHooks.getCurrentServer();
		this.client = Minecraft.getInstance();
		if (this.server == null) {
			RegistryAccess registryAccess = this.client.level.registryAccess();
			Registry<LevelStem> dimensionRegistry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
			this.virtualDimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("quantumquarry", "mining_dimension"));
			this.virtualGenerator = dimensionRegistry.get(LevelStem.OVERWORLD).generator();
		} else {
			RegistryAccess registryAccess = this.server.registryAccess();
			Registry<LevelStem> dimensionRegistry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
			this.virtualDimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("quantumquarry", "mining_dimension"));
			this.virtualGenerator = dimensionRegistry.get(LevelStem.OVERWORLD).generator();
		}
		//registerVirtualDimension();
	}

	public static void registerVirtualDimension() {
        RegistryAccess registryAccess = RegistryAccess.builtin();
        Registry<LevelStem> dimensionRegistry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);

        if (!dimensionRegistry.containsKey(VIRTUAL_DIMENSION.location())) {
            Registry.register(dimensionRegistry, VIRTUAL_DIMENSION.location(), dimensionRegistry.get(LevelStem.OVERWORLD));
        }
    }

	/*
	private ServerLevel createVirtualLevel() {
		RegistryAccess registryAccess = server.registryAccess();
		Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
		Registry<LevelStem> dimensionRegistry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);

		long seed = new Random().nextLong();
		WorldData worldData = server.getWorldData();
		PrimaryLevelData levelData = new PrimaryLevelData(worldData.getLevelSettings(), server.getWorldData().worldGenOptions(), PrimaryLevelData.SpecialWorldProperty.NONE, Lifecycle.stable());

		ChunkProgressListener progressListener = new DummyChunkProgressListener();
		ResourceKey<Level> virtualDimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("quantumquarry", "virtual_dimension"));
		ChunkGenerator chunkGenerator = dimensionRegistry.get(LevelStem.OVERWORLD).generator(); //Change later for biome functionality

		LevelStorageSource source = LevelStorageSource.createDefault(server.getWorldPath(LevelResource.ROOT));
		LevelStorageAccess access = source.createAccess("VirtualWorld");

		return new ServerLevel(
			server,
			(Executor)server,
			access,
			levelData,
			virtualDimension,
			dimensionRegistry.get(LevelStem.OVERWORLD), //again change later for biome functionality
			progressListener,
			true,
			seed,
			List.of(),
			false,
			new RandomSequences(seed)
		);
	}
	*/

	private Biome selectRandomBiome() {
		if (this.server != null) {
			ServerLevel serverLevel = this.server.getLevel(Level.OVERWORLD);
			Registry<Biome> biomeRegistry = serverLevel.registryAccess().registryOrThrow(Registries.BIOME);
			List<Biome> biomes = biomeRegistry.stream().toList();
			return biomes.get(new Random().nextInt(biomes.size()));
		} else {
			Registry<Biome> biomeRegistry = this.client.level.registryAccess().registryOrThrow(Registries.BIOME);
			List<Biome> biomes = biomeRegistry.stream().toList();
			return biomes.get(new Random().nextInt(biomes.size()));
		}
	}

	public ChunkAccess genChunk() {
		Biome randomBiome = selectRandomBiome();
		return genChunk(randomBiome);
	}

	public ChunkAccess genChunk(Biome biome) {
		try {
			Random randomLoc = new Random();
			int chunkX = randomLoc.nextInt(3750000);
			int chunkZ = randomLoc.nextInt(3750000);
			//int chunkX = 0;
			//int chunkZ = 0;
			if (this.server != null) {
				ServerLevel serverLevel = this.server.getLevel(Level.OVERWORLD);
				ChunkAccess chunkAccess = serverLevel.getChunkSource().getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
				return chunkAccess;
			} else {
				ChunkAccess chunkAccess = this.client.level.getChunkSource().getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
				return chunkAccess;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
