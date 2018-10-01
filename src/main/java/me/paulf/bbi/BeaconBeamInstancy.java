package me.paulf.bbi;

import me.paulf.bbi.server.level.chunk.BeaconLookup;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;

@Mod(
	modid = BeaconBeamInstancy.ID,
	name = "Beacon Beam Instancy",
	version = "1.0.0",
	dependencies = "required-after:forge@[14.23.4.2705,)"
)
public final class BeaconBeamInstancy {
	public static final String ID = "bbi";

	@Mod.EventHandler
	public void init(FMLPreInitializationEvent event) {
		CapabilityManager.INSTANCE.register(BeaconLookup.class, new Capability.IStorage<BeaconLookup>() {
			@Nullable
			@Override
			public NBTBase writeNBT(Capability<BeaconLookup> capability, BeaconLookup instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<BeaconLookup> capability, BeaconLookup instance, EnumFacing side, NBTBase nbt) {}
		}, BeaconLookup::new);
	}
}
