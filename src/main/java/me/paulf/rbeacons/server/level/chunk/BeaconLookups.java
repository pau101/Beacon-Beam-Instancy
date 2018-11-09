package me.paulf.rbeacons.server.level.chunk;

import me.paulf.rbeacons.ResponsiveBeacons;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = ResponsiveBeacons.ID)
public final class BeaconLookups {
	private BeaconLookups() {}

	@CapabilityInject(BeaconLookup.class)
	private static Capability<BeaconLookup> capability = null;

	public static void notifyBelow(final World world, final BlockPos pos) {
		BeaconLookups.get(world, pos).notifyBelow(world, pos);
	}

	public static void notifyAround(final World world, final BlockPos pos) {
		final int range = 4;
		final int minX = (pos.getX() - range) >> 4;
		final int minZ = (pos.getZ() - range) >> 4;
		final int maxX = (pos.getX() + range) >> 4;
		final int maxZ = (pos.getZ() + range) >> 4;
		final IChunkProvider provider = world.getChunkProvider();
		for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
			for (int chunkX = minX; chunkX <= maxX; chunkX++) {
				@Nullable final Chunk c = provider.getLoadedChunk(chunkX, chunkZ);
				if (c != null) {
					BeaconLookups.get(c).notifyAround(world, pos, range);
				}
			}
		}
	}

	public static BeaconLookup get(final World world, final BlockPos pos) {
		return BeaconLookups.get(world.getChunk(pos));
	}

	public static BeaconLookup get(final Chunk chunk) {
		final BeaconLookup lookup = chunk.getCapability(BeaconLookups.capability, null);
		if (lookup == null) {
			throw new IllegalStateException(String.format(
				"Missing capability for chunk at %s of %s in %s from %s",
				chunk.getPos(),
				chunk.getClass().getName(),
				chunk.getWorld().getClass().getName(),
				chunk.getWorld().getChunkProvider().getClass().getName()
			));
		}
		return lookup;
	}

	@SubscribeEvent
	public static void onAttachCapabilities(final AttachCapabilitiesEvent<Chunk> event) {
		final Chunk chunk = event.getObject();
		final BeaconLookup lookup = new BeaconLookup(chunk.x, chunk.z);
		event.addCapability(new ResourceLocation(ResponsiveBeacons.ID, "lookup"), new ICapabilityProvider() {
			@Override
			public boolean hasCapability(final @Nonnull Capability<?> capability, final @Nullable EnumFacing facing) {
				return capability == BeaconLookups.capability;
			}

			@Nullable
			@Override
			public <T> T getCapability(final @Nonnull Capability<T> capability, final @Nullable EnumFacing facing) {
				return capability == BeaconLookups.capability ? BeaconLookups.capability.cast(lookup) : null;
			}
		});
	}
}
