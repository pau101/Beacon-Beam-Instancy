package me.paulf.rbeacons.server.block;

import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class InstantStainedGlassBlock extends BlockStainedGlass {
	public InstantStainedGlassBlock() {
		super(Material.GLASS);
		this.setHardness(0.3F);
		this.setSoundType(SoundType.GLASS);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {}
}
