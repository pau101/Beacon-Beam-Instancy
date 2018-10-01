package me.paulf.rbeacons.server.level.chunk;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class BeaconLookup {
	private IntList[] columns;

	private int count;

	public BeaconLookup() {
		this(null, 0);
	}

	private BeaconLookup(IntList[] columns, int count) {
		this.columns = columns;
		this.count = count;
	}

	public void notifyColumn(final World world, final BlockPos source) {
		if (this.columns != null) {
			final int columnIndex = this.col(source);
			final IntList column = this.columns[columnIndex];
			if (column != null) {
				BlockPos.MutableBlockPos pos = null;
				for (IntIterator it = column.iterator(); it.hasNext(); ) {
					final int y = it.nextInt();
					if (y < source.getY()) {
						if (pos == null) {
							pos = new BlockPos.MutableBlockPos(source);
						}
						pos.setY(y);
						final TileEntity entity = world.getTileEntity(pos);
						if (entity instanceof TileEntityBeacon) {
							((TileEntityBeacon) entity).updateBeacon();
						} else {
							it.remove();
							this.drop(columnIndex, column);
							// TODO: log this inconsistency
						}
					}
				}
			}
		}
	}

	public void add(final BlockPos pos) {
		if (this.columns == null) {
			this.columns = new IntList[256];
		}
		final int columnIndex = this.col(pos);
		final IntList column;
		if (this.columns[columnIndex] == null) {
			column = new IntArrayList(1);
			this.columns[columnIndex] = column;
		} else {
			column = this.columns[columnIndex];
		}
		column.add(pos.getY());
		this.count++;
	}

	public void remove(final BlockPos pos) {
		if (this.columns != null) {
			final int columnIndex = this.col(pos);
			final IntList column = this.columns[columnIndex];
			if (column != null && column.rem(pos.getY())) {
				this.drop(columnIndex, column);
			}
		}
	}

	private void drop(int columnIndex, IntList column) {
		this.count--;
		if (column.isEmpty()) {
			this.columns[columnIndex] = null;
			if (this.count == 0) {
				this.columns = null;
			}
		}
	}

	private int col(BlockPos pos) {
		return pos.getX() & 15 | (pos.getZ() & 15) << 4;
	}
}
