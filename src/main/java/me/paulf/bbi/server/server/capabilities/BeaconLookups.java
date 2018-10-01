package me.paulf.bbi.server.server.capabilities;

import me.paulf.bbi.BeaconBeamInstancy;
import me.paulf.bbi.server.ServerEventHandler;
import me.paulf.bbi.server.block.entity.InstantBeaconEntity;
import me.paulf.bbi.server.level.chunk.BeaconLookup;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = BeaconBeamInstancy.ID)
public final class BeaconLookups {
	private BeaconLookups() {}

	@CapabilityInject(BeaconLookup.class)
	private static Capability<BeaconLookup> capability = null;

	public static BeaconLookup get(World world, BlockPos pos) {
		Chunk chunk = world.getChunk(pos);
		BeaconLookup map = chunk.getCapability(BeaconLookups.capability, null);
		if (map == null) {
			throw new IllegalStateException(String.format("Missing capability for chunk at %s of %s in %s from %s", chunk.getPos(), chunk.getClass().getName(), world.getClass().getName(), world.getChunkProvider().getClass().getName()));
		}
		return map;
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Chunk> event) {
		event.addCapability(new ResourceLocation(BeaconBeamInstancy.ID, "beacon_map"), new ICapabilityProvider() {
			private final BeaconLookup map = BeaconLookups.capability.getDefaultInstance();

			@Override
			public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
				return capability == BeaconLookups.capability;
			}

			@Nullable
			@Override
			public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
				return capability == BeaconLookups.capability ? BeaconLookups.capability.cast(map) : null;
			}
		});
	}
}
