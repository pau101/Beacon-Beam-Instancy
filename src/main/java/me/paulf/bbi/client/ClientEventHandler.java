package me.paulf.bbi.client;

import me.paulf.bbi.BeaconBeamInstancy;
import me.paulf.bbi.server.block.entity.InstantBeaconEntity;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = BeaconBeamInstancy.ID)
public final class ClientEventHandler {
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		ClientRegistry.bindTileEntitySpecialRenderer(InstantBeaconEntity.class, new TileEntityBeaconRenderer());
	}
}
