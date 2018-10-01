package me.paulf.bbi.server.block;

import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class InstantStainedGlassPane extends BlockStainedGlassPane {
	public InstantStainedGlassPane() {
		this.setHardness(0.3F);
		this.setSoundType(SoundType.GLASS);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {}
}
