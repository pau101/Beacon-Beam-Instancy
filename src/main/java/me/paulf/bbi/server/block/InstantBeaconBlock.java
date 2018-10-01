package me.paulf.bbi.server.block;

import me.paulf.bbi.server.block.entity.InstantBeaconEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class InstantBeaconBlock extends BlockBeacon {
	public InstantBeaconBlock() {
		this.setLightLevel(1.0F);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new InstantBeaconEntity();
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos source) {}
}
