package me.paulf.bbi.server.block.entity;

import me.paulf.bbi.server.server.capabilities.BeaconLookups;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class InstantBeaconEntity extends TileEntityBeacon {
	@Override
	public void onLoad() {
		this.updateBeacon();
		BeaconLookups.get(this.world, this.pos).add(this.pos);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		BeaconLookups.get(this.world, this.pos).remove(this.pos);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.updateBeacon();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float shouldBeamRender() {
		return super.shouldBeamRender() == 0.0F ? 0.0F : 1.0F;
	}
}
