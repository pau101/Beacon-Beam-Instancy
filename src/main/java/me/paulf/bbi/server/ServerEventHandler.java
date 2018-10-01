package me.paulf.bbi.server;

import me.paulf.bbi.BeaconBeamInstancy;
import me.paulf.bbi.server.block.InstantBeaconBlock;
import me.paulf.bbi.server.block.InstantStainedGlassBlock;
import me.paulf.bbi.server.block.InstantStainedGlassPane;
import me.paulf.bbi.server.block.entity.InstantBeaconEntity;
import me.paulf.bbi.server.server.capabilities.BeaconLookups;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = BeaconBeamInstancy.ID)
public final class ServerEventHandler {
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
			new InstantBeaconBlock()
				.setTranslationKey("beacon")
				.setRegistryName(new ResourceLocation("beacon")),
			new InstantStainedGlassBlock()
				.setTranslationKey("stainedGlass")
				.setRegistryName(new ResourceLocation("stained_glass")),
			new InstantStainedGlassPane()
				.setTranslationKey("thinStainedGlass")
				.setRegistryName(new ResourceLocation("stained_glass_pane"))
		);
		GameRegistry.registerTileEntity(InstantBeaconEntity.class, new ResourceLocation("beacon"));
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		event.getWorld().addEventListener(new ServerEventHandler.WorldListener());
	}

	private static final class WorldListener implements IWorldEventListener {
		@Override
		public void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
			BeaconLookups.get(world, pos).notifyColumn(world, pos);
		}

		@Override
		public void notifyLightSet(BlockPos pos) {}

		@Override
		public void markBlockRangeForRenderUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {}

		@Override
		public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent sound, SoundCategory category, double x, double y, double z, float volume, float pitch) {}

		@Override
		public void playRecord(SoundEvent sound, BlockPos pos) {}

		@Override
		public void spawnParticle(int particle, boolean ignoreRange, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {}

		@Override
		public void spawnParticle(int id, boolean ignoreRange, boolean alwaysShow, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {}

		@Override
		public void onEntityAdded(Entity entity) {}

		@Override
		public void onEntityRemoved(Entity entity) {}

		@Override
		public void broadcastSound(int sound, BlockPos pos, int data) {}

		@Override
		public void playEvent(EntityPlayer player, int type, BlockPos pos, int data) {}

		@Override
		public void sendBlockBreakProgress(int breaker, BlockPos pos, int progress) {}
	}
}
