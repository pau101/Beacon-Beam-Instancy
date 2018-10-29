package me.paulf.rbeacons.server.level.chunk;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import javax.annotation.Nullable;

public final class BeaconLookup {
	private final int x;

	private final int z;

	@Nullable
	private IntList[] columns;

	@Nullable
	private Int2ObjectMap<IntList> sectors;

	private int count;

	public BeaconLookup(final int x, final int z) {
		this(x, z, null, null, 0);
	}

	private BeaconLookup(final int x, final int z, @Nullable final IntList[] columns, @Nullable final Int2ObjectMap<IntList> sectors, final int count) {
		this.x = x;
		this.z = z;
		this.columns = columns;
		this.sectors = sectors;
		this.count = count;
	}

	public void notifyBelow(final World world, final BlockPos source) {
		if (this.columns != null) {
			final int columnIndex = this.toColumnIndex(source);
			@Nullable final IntList column = this.columns[columnIndex];
			if (column != null) {
				final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(source);
				for (final IntIterator it = column.iterator(); it.hasNext(); ) {
					final int y = it.nextInt();
					if (y < source.getY()) {
						pos.setY(y);
						if (this.update(world, pos)) {
							it.remove();
							this.cullColumn(columnIndex, column);
							if (!this.sectorRemove(pos)) {
								throw new IllegalStateException();
							}
							this.count--;
						}
					}
				}
			}
			this.cull();
		}
	}

	public void notifyAround(final World world, final BlockPos source) {
		final int r = 4;
		final int minX = (source.getX() - r) >> 4;
		final int minZ = (source.getZ() - r) >> 4;
		final int maxX = (source.getX() + r) >> 4;
		final int maxZ = (source.getZ() + r) >> 4;
		final int minY = source.getY() + 1;
		final int maxY = source.getY() + r;
		final IChunkProvider provider = world.getChunkProvider();
		for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
			for (int chunkX = minX; chunkX <= maxX; chunkX++) {
				@Nullable final Chunk c = provider.getLoadedChunk(chunkX, chunkZ);
				if (c != null) {
					BeaconLookups.get(c).notifyAround(world, source, minY, maxY);
				}
			}
		}
	}

	private void notifyAround(final World world, final BlockPos source, final int minY, final int maxY) {
		if (this.sectors != null) {
			for (int sectorIndex = this.toSectorIndex(minY); sectorIndex <= this.toSectorIndex(maxY); sectorIndex++) {
				@Nullable final IntList sector = this.sectors.get(sectorIndex);
				if (sector != null) {
					final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
					for (final IntIterator it = sector.iterator(); it.hasNext(); ) {
						this.fromSectorValue(sectorIndex, pos, it.nextInt());
						final int py = pos.getY();
						if (py >= minY && py <= maxY) {
							final int dy = py - source.getY();
							if (Math.abs(pos.getX() - source.getX()) <= dy && Math.abs(pos.getZ() - source.getZ()) <= dy) {
								if (this.update(world, pos)) {
									it.remove();
									this.cullSector(sectorIndex, sector);
									if (!this.columnRemove(pos)) {
										throw new IllegalStateException();
									}
									this.count--;
								}
							}
						}
					}
				}
			}
			this.cull();
		}
	}

	private boolean update(final IBlockAccess world, final BlockPos pos) {
		@Nullable final TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof TileEntityBeacon) {
			((TileEntityBeacon) entity).updateBeacon();
		} else {
			return true;
		}
		return false;
	}

	public void add(final BlockPos pos) {
		if (this.columns == null) {
			this.columns = new IntList[256];
		}
		final int columnIndex = this.toColumnIndex(pos);
		final IntList column;
		if (this.columns[columnIndex] == null) {
			column = new IntArrayList(1);
			this.columns[columnIndex] = column;
		} else {
			column = this.columns[columnIndex];
		}
		column.add(pos.getY());
		final int sectorIndex = this.toSectorIndex(pos);
		if (this.sectors == null) {
			this.sectors = new Int2ObjectOpenHashMap<>(4);
		}
		@Nullable final IntList tmp = this.sectors.get(sectorIndex);
		final IntList sector;
		if (tmp == null) {
			sector = new IntArrayList(1);
			this.sectors.put(sectorIndex, sector);
		} else {
			sector = tmp;
		}
		sector.add(this.toSectorValue(pos));
		this.count++;
	}

	public void remove(final BlockPos pos) {
		boolean cRemove = this.columnRemove(pos);
		boolean sRemove = this.sectorRemove(pos);
		if (cRemove != sRemove) {
			throw new IllegalStateException();
		}
		if (sRemove) {
			this.count--;
			this.cull();
		}
	}

	private void cull() {
		if (this.count == 0) {
			this.columns = null;
			this.sectors = null;
		}
	}

	private boolean columnRemove(final BlockPos pos) {
		if (this.columns != null) {
			final int columnIndex = this.toColumnIndex(pos);
			@Nullable final IntList column = this.columns[columnIndex];
			if (column != null && column.rem(pos.getY())) {
				this.cullColumn(columnIndex, column);
				return true;
			}
		}
		return false;
	}

	private void cullColumn(final int columnIndex, final IntList column) {
		if (this.columns != null && column.isEmpty()) {
			this.columns[columnIndex] = null;
		}
	}

	private boolean sectorRemove(final BlockPos pos) {
		if (this.sectors != null) {
			final int sectorIndex = this.toSectorIndex(pos);
			@Nullable final IntList sector = this.sectors.get(sectorIndex);
			if (sector != null && sector.rem(this.toSectorValue(pos))) {
				this.cullSector(sectorIndex, sector);
				return true;
			}
		}
		return false;
	}

	private void cullSector(final int sectorIndex, final IntList sector) {
		if (this.sectors != null && sector.isEmpty()) {
			this.sectors.remove(sectorIndex);
		}
	}

	private int toColumnIndex(final BlockPos pos) {
		return pos.getX() & 15 | (pos.getZ() & 15) << 4;
	}

	private int toSectorValue(final BlockPos pos) {
		return (pos.getX() & 15) << 8 | (pos.getY() & 15) << 4 | (pos.getZ() & 15);
	}

	private void fromSectorValue(final int sectorIndex, final BlockPos.MutableBlockPos pos, final int index) {
		pos.setPos((this.x << 4) + (index >> 8 & 15), (sectorIndex << 4) + (index >> 4 & 15), (this.z << 4) + (index & 15));
	}

	private int toSectorIndex(final BlockPos pos) {
		return this.toSectorIndex(pos.getY());
	}

	private int toSectorIndex(final int y) {
		return y >> 4;
	}
}
