package me.paulf.rbeacons.server.block;

import me.paulf.rbeacons.server.block.entity.ResponsiveBeaconEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ResponsiveBeaconBlock extends BlockBeacon {

	public ResponsiveBeaconBlock() {
		super();
		this.setLightLevel(1);
		this.setSoundType(SoundType.GLASS);
		this.setTranslationKey("beacon");
	}

	@Override
	public boolean hasTileEntity(final IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final World world, final IBlockState state) {
		return new ResponsiveBeaconEntity();
	}

	@Override
	public void neighborChanged(final IBlockState state, final World world, final BlockPos pos, final Block block, final BlockPos source) {}
}
