package me.paulf.rbeacons;

import javax.annotation.Nullable;

import me.paulf.rbeacons.server.BeaconNotifier;
import me.paulf.rbeacons.server.block.ResponsiveBeaconBlock;
import me.paulf.rbeacons.server.block.ResponsiveStainedGlassBlock;
import me.paulf.rbeacons.server.block.ResponsiveStainedGlassPane;
import me.paulf.rbeacons.server.block.entity.ResponsiveBeaconEntity;
import me.paulf.rbeacons.server.event.RegistryAvailableEvent;
import me.paulf.rbeacons.server.level.chunk.BeaconLookup;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLModIdMappingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = ResponsiveBeacons.ID, useMetadata = true)
@EventBusSubscriber
public final class ResponsiveBeacons {

	public static final String ID = "rbeacons";

	@Mod.EventHandler
	public void onInit(final FMLPreInitializationEvent event) {
		CapabilityManager.INSTANCE.register(BeaconLookup.class, new Capability.IStorage<BeaconLookup>() {

			@Nullable
			@Override
			public NBTBase writeNBT(final Capability<BeaconLookup> capability, final BeaconLookup instance, final EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(final Capability<BeaconLookup> capability, final BeaconLookup instance, final EnumFacing side, final NBTBase nbt) {}

		}, () -> {
			throw new UnsupportedOperationException();
		});

		MinecraftForge.EVENT_BUS.register(BeaconNotifier.create());
	}

	@Mod.EventHandler
	public void onMap(final FMLModIdMappingEvent event) {
		MinecraftForge.EVENT_BUS.post(RegistryAvailableEvent.create(ForgeRegistries.BLOCKS));
	}

	@SubscribeEvent
	public static void onRegisterBlock(final RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
				new ResponsiveBeaconBlock()
				.setRegistryName(new ResourceLocation("beacon")),
				new ResponsiveStainedGlassBlock()
				.setRegistryName(new ResourceLocation("stained_glass")),
				new ResponsiveStainedGlassPane()
				.setRegistryName(new ResourceLocation("stained_glass_pane")));

		GameRegistry.registerTileEntity(ResponsiveBeaconEntity.class, new ResourceLocation("beacon"));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onModelRegister(final ModelRegistryEvent event) {
		ClientRegistry.bindTileEntitySpecialRenderer(ResponsiveBeaconEntity.class, new TileEntityBeaconRenderer());
	}
}
