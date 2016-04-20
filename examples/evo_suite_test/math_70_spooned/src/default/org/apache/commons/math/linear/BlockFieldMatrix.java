package org.apache.commons.math.linear;


public class BlockFieldMatrix<T extends org.apache.commons.math.FieldElement<T>> extends org.apache.commons.math.linear.AbstractFieldMatrix<T> implements java.io.Serializable {
	public static final int BLOCK_SIZE = 36;

	private static final long serialVersionUID = -4602336630143123183L;

	private final T[][] blocks;

	private final int rows;

	private final int columns;

	private final int blockRows;

	private final int blockColumns;

	public BlockFieldMatrix(final org.apache.commons.math.Field<T> field ,final int rows ,final int columns) throws java.lang.IllegalArgumentException {
		super(field, rows, columns);
		this.rows = rows;
		this.columns = columns;
		blockRows = ((rows + (BLOCK_SIZE)) - 1) / (BLOCK_SIZE);
		blockColumns = ((columns + (BLOCK_SIZE)) - 1) / (BLOCK_SIZE);
		blocks = org.apache.commons.math.linear.BlockFieldMatrix.createBlocksLayout(field, rows, columns);
	}

	public BlockFieldMatrix(final T[][] rawData) throws java.lang.IllegalArgumentException {
		this(rawData.length, rawData[0].length, org.apache.commons.math.linear.BlockFieldMatrix.toBlocksLayout(rawData), false);
	}

	public BlockFieldMatrix(final int rows ,final int columns ,final T[][] blockData ,final boolean copyArray) throws java.lang.IllegalArgumentException {
		super(org.apache.commons.math.linear.AbstractFieldMatrix.extractField(blockData), rows, columns);
		this.rows = rows;
		this.columns = columns;
		blockRows = ((rows + (BLOCK_SIZE)) - 1) / (BLOCK_SIZE);
		blockColumns = ((columns + (BLOCK_SIZE)) - 1) / (BLOCK_SIZE);
		if (copyArray) {
			blocks = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), ((blockRows) * (blockColumns)), -1);
		} else {
			blocks = blockData;
		}
		int index = 0;
		for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
			final int iHeight = blockHeight(iBlock);
			for (int jBlock = 0 ; jBlock < (blockColumns) ; ++jBlock , ++index) {
				if ((blockData[index].length) != (iHeight * (blockWidth(jBlock)))) {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("wrong array shape (block length = {0}, expected {1})", blockData[index].length, (iHeight * (blockWidth(jBlock))));
				} 
				if (copyArray) {
					blocks[index] = blockData[index].clone();
				} 
			}
		}
	}

	public static <T extends org.apache.commons.math.FieldElement<T>>T[][] toBlocksLayout(final T[][] rawData) throws java.lang.IllegalArgumentException {
		final int rows = rawData.length;
		final int columns = rawData[0].length;
		final int blockRows = ((rows + (BLOCK_SIZE)) - 1) / (BLOCK_SIZE);
		final int blockColumns = ((columns + (BLOCK_SIZE)) - 1) / (BLOCK_SIZE);
		for (int i = 0 ; i < (rawData.length) ; ++i) {
			final int length = rawData[i].length;
			if (length != columns) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("some rows have length {0} while others have length {1}", columns, length);
			} 
		}
		final org.apache.commons.math.Field<T> field = org.apache.commons.math.linear.AbstractFieldMatrix.extractField(rawData);
		final T[][] blocks = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(field, (blockRows * blockColumns), -1);
		int blockIndex = 0;
		for (int iBlock = 0 ; iBlock < blockRows ; ++iBlock) {
			final int pStart = iBlock * (BLOCK_SIZE);
			final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
			final int iHeight = pEnd - pStart;
			for (int jBlock = 0 ; jBlock < blockColumns ; ++jBlock) {
				final int qStart = jBlock * (BLOCK_SIZE);
				final int qEnd = java.lang.Math.min((qStart + (BLOCK_SIZE)), columns);
				final int jWidth = qEnd - qStart;
				final T[] block = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(field, (iHeight * jWidth));
				blocks[blockIndex] = block;
				int index = 0;
				for (int p = pStart ; p < pEnd ; ++p) {
					java.lang.System.arraycopy(rawData[p], qStart, block, index, jWidth);
					index += jWidth;
				}
				++blockIndex;
			}
		}
		return blocks;
	}

	public static <T extends org.apache.commons.math.FieldElement<T>>T[][] createBlocksLayout(final org.apache.commons.math.Field<T> field, final int rows, final int columns) {
		final int blockRows = ((rows + (BLOCK_SIZE)) - 1) / (BLOCK_SIZE);
		final int blockColumns = ((columns + (BLOCK_SIZE)) - 1) / (BLOCK_SIZE);
		final T[][] blocks = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(field, (blockRows * blockColumns), -1);
		int blockIndex = 0;
		for (int iBlock = 0 ; iBlock < blockRows ; ++iBlock) {
			final int pStart = iBlock * (BLOCK_SIZE);
			final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
			final int iHeight = pEnd - pStart;
			for (int jBlock = 0 ; jBlock < blockColumns ; ++jBlock) {
				final int qStart = jBlock * (BLOCK_SIZE);
				final int qEnd = java.lang.Math.min((qStart + (BLOCK_SIZE)), columns);
				final int jWidth = qEnd - qStart;
				blocks[blockIndex] = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(field, (iHeight * jWidth));
				++blockIndex;
			}
		}
		return blocks;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> createMatrix(final int rowDimension, final int columnDimension) throws java.lang.IllegalArgumentException {
		return new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , rowDimension , columnDimension);
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> copy() {
		org.apache.commons.math.linear.BlockFieldMatrix<T> copied = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , rows , columns);
		for (int i = 0 ; i < (blocks.length) ; ++i) {
			java.lang.System.arraycopy(blocks[i], 0, copied.blocks[i], 0, blocks[i].length);
		}
		return copied;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> add(final org.apache.commons.math.linear.FieldMatrix<T> m) throws java.lang.IllegalArgumentException {
		try {
			return add(((org.apache.commons.math.linear.BlockFieldMatrix<T>)(m)));
		} catch (java.lang.ClassCastException cce) {
			checkAdditionCompatible(m);
			final org.apache.commons.math.linear.BlockFieldMatrix<T> out = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , rows , columns);
			int blockIndex = 0;
			for (int iBlock = 0 ; iBlock < (out.blockRows) ; ++iBlock) {
				for (int jBlock = 0 ; jBlock < (out.blockColumns) ; ++jBlock) {
					final T[] outBlock = out.blocks[blockIndex];
					final T[] tBlock = blocks[blockIndex];
					final int pStart = iBlock * (BLOCK_SIZE);
					final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
					final int qStart = jBlock * (BLOCK_SIZE);
					final int qEnd = java.lang.Math.min((qStart + (BLOCK_SIZE)), columns);
					int k = 0;
					for (int p = pStart ; p < pEnd ; ++p) {
						for (int q = qStart ; q < qEnd ; ++q) {
							outBlock[k] = tBlock[k].add(m.getEntry(p, q));
							++k;
						}
					}
					++blockIndex;
				}
			}
			return out;
		}
	}

	public org.apache.commons.math.linear.BlockFieldMatrix<T> add(final org.apache.commons.math.linear.BlockFieldMatrix<T> m) throws java.lang.IllegalArgumentException {
		checkAdditionCompatible(m);
		final org.apache.commons.math.linear.BlockFieldMatrix<T> out = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , rows , columns);
		for (int blockIndex = 0 ; blockIndex < (out.blocks.length) ; ++blockIndex) {
			final T[] outBlock = out.blocks[blockIndex];
			final T[] tBlock = blocks[blockIndex];
			final T[] mBlock = m.blocks[blockIndex];
			for (int k = 0 ; k < (outBlock.length) ; ++k) {
				outBlock[k] = tBlock[k].add(mBlock[k]);
			}
		}
		return out;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> subtract(final org.apache.commons.math.linear.FieldMatrix<T> m) throws java.lang.IllegalArgumentException {
		try {
			return subtract(((org.apache.commons.math.linear.BlockFieldMatrix<T>)(m)));
		} catch (java.lang.ClassCastException cce) {
			checkSubtractionCompatible(m);
			final org.apache.commons.math.linear.BlockFieldMatrix<T> out = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , rows , columns);
			int blockIndex = 0;
			for (int iBlock = 0 ; iBlock < (out.blockRows) ; ++iBlock) {
				for (int jBlock = 0 ; jBlock < (out.blockColumns) ; ++jBlock) {
					final T[] outBlock = out.blocks[blockIndex];
					final T[] tBlock = blocks[blockIndex];
					final int pStart = iBlock * (BLOCK_SIZE);
					final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
					final int qStart = jBlock * (BLOCK_SIZE);
					final int qEnd = java.lang.Math.min((qStart + (BLOCK_SIZE)), columns);
					int k = 0;
					for (int p = pStart ; p < pEnd ; ++p) {
						for (int q = qStart ; q < qEnd ; ++q) {
							outBlock[k] = tBlock[k].subtract(m.getEntry(p, q));
							++k;
						}
					}
					++blockIndex;
				}
			}
			return out;
		}
	}

	public org.apache.commons.math.linear.BlockFieldMatrix<T> subtract(final org.apache.commons.math.linear.BlockFieldMatrix<T> m) throws java.lang.IllegalArgumentException {
		checkSubtractionCompatible(m);
		final org.apache.commons.math.linear.BlockFieldMatrix<T> out = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , rows , columns);
		for (int blockIndex = 0 ; blockIndex < (out.blocks.length) ; ++blockIndex) {
			final T[] outBlock = out.blocks[blockIndex];
			final T[] tBlock = blocks[blockIndex];
			final T[] mBlock = m.blocks[blockIndex];
			for (int k = 0 ; k < (outBlock.length) ; ++k) {
				outBlock[k] = tBlock[k].subtract(mBlock[k]);
			}
		}
		return out;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> scalarAdd(final T d) throws java.lang.IllegalArgumentException {
		final org.apache.commons.math.linear.BlockFieldMatrix<T> out = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , rows , columns);
		for (int blockIndex = 0 ; blockIndex < (out.blocks.length) ; ++blockIndex) {
			final T[] outBlock = out.blocks[blockIndex];
			final T[] tBlock = blocks[blockIndex];
			for (int k = 0 ; k < (outBlock.length) ; ++k) {
				outBlock[k] = tBlock[k].add(d);
			}
		}
		return out;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> scalarMultiply(final T d) throws java.lang.IllegalArgumentException {
		final org.apache.commons.math.linear.BlockFieldMatrix<T> out = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , rows , columns);
		for (int blockIndex = 0 ; blockIndex < (out.blocks.length) ; ++blockIndex) {
			final T[] outBlock = out.blocks[blockIndex];
			final T[] tBlock = blocks[blockIndex];
			for (int k = 0 ; k < (outBlock.length) ; ++k) {
				outBlock[k] = tBlock[k].multiply(d);
			}
		}
		return out;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> multiply(final org.apache.commons.math.linear.FieldMatrix<T> m) throws java.lang.IllegalArgumentException {
		try {
			return multiply(((org.apache.commons.math.linear.BlockFieldMatrix<T>)(m)));
		} catch (java.lang.ClassCastException cce) {
			checkMultiplicationCompatible(m);
			final org.apache.commons.math.linear.BlockFieldMatrix<T> out = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , rows , m.getColumnDimension());
			final T zero = getField().getZero();
			int blockIndex = 0;
			for (int iBlock = 0 ; iBlock < (out.blockRows) ; ++iBlock) {
				final int pStart = iBlock * (BLOCK_SIZE);
				final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
				for (int jBlock = 0 ; jBlock < (out.blockColumns) ; ++jBlock) {
					final int qStart = jBlock * (BLOCK_SIZE);
					final int qEnd = java.lang.Math.min((qStart + (BLOCK_SIZE)), m.getColumnDimension());
					final T[] outBlock = out.blocks[blockIndex];
					for (int kBlock = 0 ; kBlock < (blockColumns) ; ++kBlock) {
						final int kWidth = blockWidth(kBlock);
						final T[] tBlock = blocks[((iBlock * (blockColumns)) + kBlock)];
						final int rStart = kBlock * (BLOCK_SIZE);
						int k = 0;
						for (int p = pStart ; p < pEnd ; ++p) {
							final int lStart = (p - pStart) * kWidth;
							final int lEnd = lStart + kWidth;
							for (int q = qStart ; q < qEnd ; ++q) {
								T sum = zero;
								int r = rStart;
								for (int l = lStart ; l < lEnd ; ++l) {
									sum = sum.add(tBlock[l].multiply(m.getEntry(r, q)));
									++r;
								}
								outBlock[k] = outBlock[k].add(sum);
								++k;
							}
						}
					}
					++blockIndex;
				}
			}
			return out;
		}
	}

	public org.apache.commons.math.linear.BlockFieldMatrix<T> multiply(org.apache.commons.math.linear.BlockFieldMatrix<T> m) throws java.lang.IllegalArgumentException {
		checkMultiplicationCompatible(m);
		final org.apache.commons.math.linear.BlockFieldMatrix<T> out = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , rows , m.columns);
		final T zero = getField().getZero();
		int blockIndex = 0;
		for (int iBlock = 0 ; iBlock < (out.blockRows) ; ++iBlock) {
			final int pStart = iBlock * (BLOCK_SIZE);
			final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
			for (int jBlock = 0 ; jBlock < (out.blockColumns) ; ++jBlock) {
				final int jWidth = out.blockWidth(jBlock);
				final int jWidth2 = jWidth + jWidth;
				final int jWidth3 = jWidth2 + jWidth;
				final int jWidth4 = jWidth3 + jWidth;
				final T[] outBlock = out.blocks[blockIndex];
				for (int kBlock = 0 ; kBlock < (blockColumns) ; ++kBlock) {
					final int kWidth = blockWidth(kBlock);
					final T[] tBlock = blocks[((iBlock * (blockColumns)) + kBlock)];
					final T[] mBlock = m.blocks[((kBlock * (m.blockColumns)) + jBlock)];
					int k = 0;
					for (int p = pStart ; p < pEnd ; ++p) {
						final int lStart = (p - pStart) * kWidth;
						final int lEnd = lStart + kWidth;
						for (int nStart = 0 ; nStart < jWidth ; ++nStart) {
							T sum = zero;
							int l = lStart;
							int n = nStart;
							while (l < (lEnd - 3)) {
								sum = sum.add(tBlock[l].multiply(mBlock[n])).add(tBlock[(l + 1)].multiply(mBlock[(n + jWidth)])).add(tBlock[(l + 2)].multiply(mBlock[(n + jWidth2)])).add(tBlock[(l + 3)].multiply(mBlock[(n + jWidth3)]));
								l += 4;
								n += jWidth4;
							}
							while (l < lEnd) {
								sum = sum.add(tBlock[l++].multiply(mBlock[n]));
								n += jWidth;
							}
							outBlock[k] = outBlock[k].add(sum);
							++k;
						}
					}
				}
				++blockIndex;
			}
		}
		return out;
	}

	@java.lang.Override
	public T[][] getData() {
		final T[][] data = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), getRowDimension(), getColumnDimension());
		final int lastColumns = (columns) - (((blockColumns) - 1) * (BLOCK_SIZE));
		for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
			final int pStart = iBlock * (BLOCK_SIZE);
			final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
			int regularPos = 0;
			int lastPos = 0;
			for (int p = pStart ; p < pEnd ; ++p) {
				final T[] dataP = data[p];
				int blockIndex = iBlock * (blockColumns);
				int dataPos = 0;
				for (int jBlock = 0 ; jBlock < ((blockColumns) - 1) ; ++jBlock) {
					java.lang.System.arraycopy(blocks[blockIndex++], regularPos, dataP, dataPos, BLOCK_SIZE);
					dataPos += BLOCK_SIZE;
				}
				java.lang.System.arraycopy(blocks[blockIndex], lastPos, dataP, dataPos, lastColumns);
				regularPos += BLOCK_SIZE;
				lastPos += lastColumns;
			}
		}
		return data;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> getSubMatrix(final int startRow, final int endRow, final int startColumn, final int endColumn) throws org.apache.commons.math.linear.MatrixIndexException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		final org.apache.commons.math.linear.BlockFieldMatrix<T> out = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , ((endRow - startRow) + 1) , ((endColumn - startColumn) + 1));
		final int blockStartRow = startRow / (BLOCK_SIZE);
		final int rowsShift = startRow % (BLOCK_SIZE);
		final int blockStartColumn = startColumn / (BLOCK_SIZE);
		final int columnsShift = startColumn % (BLOCK_SIZE);
		int pBlock = blockStartRow;
		for (int iBlock = 0 ; iBlock < (out.blockRows) ; ++iBlock) {
			final int iHeight = out.blockHeight(iBlock);
			int qBlock = blockStartColumn;
			for (int jBlock = 0 ; jBlock < (out.blockColumns) ; ++jBlock) {
				final int jWidth = out.blockWidth(jBlock);
				final int outIndex = (iBlock * (out.blockColumns)) + jBlock;
				final T[] outBlock = out.blocks[outIndex];
				final int index = (pBlock * (blockColumns)) + qBlock;
				final int width = blockWidth(qBlock);
				final int heightExcess = (iHeight + rowsShift) - (BLOCK_SIZE);
				final int widthExcess = (jWidth + columnsShift) - (BLOCK_SIZE);
				if (heightExcess > 0) {
					if (widthExcess > 0) {
						final int width2 = blockWidth((qBlock + 1));
						copyBlockPart(blocks[index], width, rowsShift, BLOCK_SIZE, columnsShift, BLOCK_SIZE, outBlock, jWidth, 0, 0);
						copyBlockPart(blocks[(index + 1)], width2, rowsShift, BLOCK_SIZE, 0, widthExcess, outBlock, jWidth, 0, (jWidth - widthExcess));
						copyBlockPart(blocks[(index + (blockColumns))], width, 0, heightExcess, columnsShift, BLOCK_SIZE, outBlock, jWidth, (iHeight - heightExcess), 0);
						copyBlockPart(blocks[((index + (blockColumns)) + 1)], width2, 0, heightExcess, 0, widthExcess, outBlock, jWidth, (iHeight - heightExcess), (jWidth - widthExcess));
					} else {
						copyBlockPart(blocks[index], width, rowsShift, BLOCK_SIZE, columnsShift, (jWidth + columnsShift), outBlock, jWidth, 0, 0);
						copyBlockPart(blocks[(index + (blockColumns))], width, 0, heightExcess, columnsShift, (jWidth + columnsShift), outBlock, jWidth, (iHeight - heightExcess), 0);
					}
				} else {
					if (widthExcess > 0) {
						final int width2 = blockWidth((qBlock + 1));
						copyBlockPart(blocks[index], width, rowsShift, (iHeight + rowsShift), columnsShift, BLOCK_SIZE, outBlock, jWidth, 0, 0);
						copyBlockPart(blocks[(index + 1)], width2, rowsShift, (iHeight + rowsShift), 0, widthExcess, outBlock, jWidth, 0, (jWidth - widthExcess));
					} else {
						copyBlockPart(blocks[index], width, rowsShift, (iHeight + rowsShift), columnsShift, (jWidth + columnsShift), outBlock, jWidth, 0, 0);
					}
				}
				++qBlock;
			}
			++pBlock;
		}
		return out;
	}

	private void copyBlockPart(final T[] srcBlock, final int srcWidth, final int srcStartRow, final int srcEndRow, final int srcStartColumn, final int srcEndColumn, final T[] dstBlock, final int dstWidth, final int dstStartRow, final int dstStartColumn) {
		final int length = srcEndColumn - srcStartColumn;
		int srcPos = (srcStartRow * srcWidth) + srcStartColumn;
		int dstPos = (dstStartRow * dstWidth) + dstStartColumn;
		for (int srcRow = srcStartRow ; srcRow < srcEndRow ; ++srcRow) {
			java.lang.System.arraycopy(srcBlock, srcPos, dstBlock, dstPos, length);
			srcPos += srcWidth;
			dstPos += dstWidth;
		}
	}

	@java.lang.Override
	public void setSubMatrix(final T[][] subMatrix, final int row, final int column) throws org.apache.commons.math.linear.MatrixIndexException {
		final int refLength = subMatrix[0].length;
		if (refLength < 1) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("matrix must have at least one column");
		} 
		final int endRow = (row + (subMatrix.length)) - 1;
		final int endColumn = (column + refLength) - 1;
		checkSubMatrixIndex(row, endRow, column, endColumn);
		for (final T[] subRow : subMatrix) {
			if ((subRow.length) != refLength) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("some rows have length {0} while others have length {1}", refLength, subRow.length);
			} 
		}
		final int blockStartRow = row / (BLOCK_SIZE);
		final int blockEndRow = (endRow + (BLOCK_SIZE)) / (BLOCK_SIZE);
		final int blockStartColumn = column / (BLOCK_SIZE);
		final int blockEndColumn = (endColumn + (BLOCK_SIZE)) / (BLOCK_SIZE);
		for (int iBlock = blockStartRow ; iBlock < blockEndRow ; ++iBlock) {
			final int iHeight = blockHeight(iBlock);
			final int firstRow = iBlock * (BLOCK_SIZE);
			final int iStart = java.lang.Math.max(row, firstRow);
			final int iEnd = java.lang.Math.min((endRow + 1), (firstRow + iHeight));
			for (int jBlock = blockStartColumn ; jBlock < blockEndColumn ; ++jBlock) {
				final int jWidth = blockWidth(jBlock);
				final int firstColumn = jBlock * (BLOCK_SIZE);
				final int jStart = java.lang.Math.max(column, firstColumn);
				final int jEnd = java.lang.Math.min((endColumn + 1), (firstColumn + jWidth));
				final int jLength = jEnd - jStart;
				final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
				for (int i = iStart ; i < iEnd ; ++i) {
					java.lang.System.arraycopy(subMatrix[(i - row)], (jStart - column), block, (((i - firstRow) * jWidth) + (jStart - firstColumn)), jLength);
				}
			}
		}
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> getRowMatrix(final int row) throws org.apache.commons.math.linear.MatrixIndexException {
		checkRowIndex(row);
		final org.apache.commons.math.linear.BlockFieldMatrix<T> out = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , 1 , columns);
		final int iBlock = row / (BLOCK_SIZE);
		final int iRow = row - (iBlock * (BLOCK_SIZE));
		int outBlockIndex = 0;
		int outIndex = 0;
		T[] outBlock = out.blocks[outBlockIndex];
		for (int jBlock = 0 ; jBlock < (blockColumns) ; ++jBlock) {
			final int jWidth = blockWidth(jBlock);
			final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
			final int available = (outBlock.length) - outIndex;
			if (jWidth > available) {
				java.lang.System.arraycopy(block, (iRow * jWidth), outBlock, outIndex, available);
				outBlock = out.blocks[++outBlockIndex];
				java.lang.System.arraycopy(block, (iRow * jWidth), outBlock, 0, (jWidth - available));
				outIndex = jWidth - available;
			} else {
				java.lang.System.arraycopy(block, (iRow * jWidth), outBlock, outIndex, jWidth);
				outIndex += jWidth;
			}
		}
		return out;
	}

	@java.lang.Override
	public void setRowMatrix(final int row, final org.apache.commons.math.linear.FieldMatrix<T> matrix) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException {
		try {
			setRowMatrix(row, ((org.apache.commons.math.linear.BlockFieldMatrix<T>)(matrix)));
		} catch (java.lang.ClassCastException cce) {
			super.setRowMatrix(row, matrix);
		}
	}

	public void setRowMatrix(final int row, final org.apache.commons.math.linear.BlockFieldMatrix<T> matrix) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException {
		checkRowIndex(row);
		final int nCols = getColumnDimension();
		if (((matrix.getRowDimension()) != 1) || ((matrix.getColumnDimension()) != nCols)) {
			throw new org.apache.commons.math.linear.InvalidMatrixException("dimensions mismatch: got {0}x{1} but expected {2}x{3}" , matrix.getRowDimension() , matrix.getColumnDimension() , 1 , nCols);
		} 
		final int iBlock = row / (BLOCK_SIZE);
		final int iRow = row - (iBlock * (BLOCK_SIZE));
		int mBlockIndex = 0;
		int mIndex = 0;
		T[] mBlock = matrix.blocks[mBlockIndex];
		for (int jBlock = 0 ; jBlock < (blockColumns) ; ++jBlock) {
			final int jWidth = blockWidth(jBlock);
			final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
			final int available = (mBlock.length) - mIndex;
			if (jWidth > available) {
				java.lang.System.arraycopy(mBlock, mIndex, block, (iRow * jWidth), available);
				mBlock = matrix.blocks[++mBlockIndex];
				java.lang.System.arraycopy(mBlock, 0, block, (iRow * jWidth), (jWidth - available));
				mIndex = jWidth - available;
			} else {
				java.lang.System.arraycopy(mBlock, mIndex, block, (iRow * jWidth), jWidth);
				mIndex += jWidth;
			}
		}
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> getColumnMatrix(final int column) throws org.apache.commons.math.linear.MatrixIndexException {
		checkColumnIndex(column);
		final org.apache.commons.math.linear.BlockFieldMatrix<T> out = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , rows , 1);
		final int jBlock = column / (BLOCK_SIZE);
		final int jColumn = column - (jBlock * (BLOCK_SIZE));
		final int jWidth = blockWidth(jBlock);
		int outBlockIndex = 0;
		int outIndex = 0;
		T[] outBlock = out.blocks[outBlockIndex];
		for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
			final int iHeight = blockHeight(iBlock);
			final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
			for (int i = 0 ; i < iHeight ; ++i) {
				if (outIndex >= (outBlock.length)) {
					outBlock = out.blocks[++outBlockIndex];
					outIndex = 0;
				} 
				outBlock[outIndex++] = block[((i * jWidth) + jColumn)];
			}
		}
		return out;
	}

	@java.lang.Override
	public void setColumnMatrix(final int column, final org.apache.commons.math.linear.FieldMatrix<T> matrix) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException {
		try {
			setColumnMatrix(column, ((org.apache.commons.math.linear.BlockFieldMatrix<T>)(matrix)));
		} catch (java.lang.ClassCastException cce) {
			super.setColumnMatrix(column, matrix);
		}
	}

	void setColumnMatrix(final int column, final org.apache.commons.math.linear.BlockFieldMatrix<T> matrix) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException {
		checkColumnIndex(column);
		final int nRows = getRowDimension();
		if (((matrix.getRowDimension()) != nRows) || ((matrix.getColumnDimension()) != 1)) {
			throw new org.apache.commons.math.linear.InvalidMatrixException("dimensions mismatch: got {0}x{1} but expected {2}x{3}" , matrix.getRowDimension() , matrix.getColumnDimension() , nRows , 1);
		} 
		final int jBlock = column / (BLOCK_SIZE);
		final int jColumn = column - (jBlock * (BLOCK_SIZE));
		final int jWidth = blockWidth(jBlock);
		int mBlockIndex = 0;
		int mIndex = 0;
		T[] mBlock = matrix.blocks[mBlockIndex];
		for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
			final int iHeight = blockHeight(iBlock);
			final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
			for (int i = 0 ; i < iHeight ; ++i) {
				if (mIndex >= (mBlock.length)) {
					mBlock = matrix.blocks[++mBlockIndex];
					mIndex = 0;
				} 
				block[((i * jWidth) + jColumn)] = mBlock[mIndex++];
			}
		}
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldVector<T> getRowVector(final int row) throws org.apache.commons.math.linear.MatrixIndexException {
		checkRowIndex(row);
		final T[] outData = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), columns);
		final int iBlock = row / (BLOCK_SIZE);
		final int iRow = row - (iBlock * (BLOCK_SIZE));
		int outIndex = 0;
		for (int jBlock = 0 ; jBlock < (blockColumns) ; ++jBlock) {
			final int jWidth = blockWidth(jBlock);
			final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
			java.lang.System.arraycopy(block, (iRow * jWidth), outData, outIndex, jWidth);
			outIndex += jWidth;
		}
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(outData , false);
	}

	@java.lang.Override
	public void setRowVector(final int row, final org.apache.commons.math.linear.FieldVector<T> vector) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException {
		try {
			setRow(row, ((org.apache.commons.math.linear.ArrayFieldVector<T>)(vector)).getDataRef());
		} catch (java.lang.ClassCastException cce) {
			super.setRowVector(row, vector);
		}
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldVector<T> getColumnVector(final int column) throws org.apache.commons.math.linear.MatrixIndexException {
		checkColumnIndex(column);
		final T[] outData = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), rows);
		final int jBlock = column / (BLOCK_SIZE);
		final int jColumn = column - (jBlock * (BLOCK_SIZE));
		final int jWidth = blockWidth(jBlock);
		int outIndex = 0;
		for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
			final int iHeight = blockHeight(iBlock);
			final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
			for (int i = 0 ; i < iHeight ; ++i) {
				outData[outIndex++] = block[((i * jWidth) + jColumn)];
			}
		}
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(outData , false);
	}

	@java.lang.Override
	public void setColumnVector(final int column, final org.apache.commons.math.linear.FieldVector<T> vector) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException {
		try {
			setColumn(column, ((org.apache.commons.math.linear.ArrayFieldVector<T>)(vector)).getDataRef());
		} catch (java.lang.ClassCastException cce) {
			super.setColumnVector(column, vector);
		}
	}

	@java.lang.Override
	public T[] getRow(final int row) throws org.apache.commons.math.linear.MatrixIndexException {
		checkRowIndex(row);
		final T[] out = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), columns);
		final int iBlock = row / (BLOCK_SIZE);
		final int iRow = row - (iBlock * (BLOCK_SIZE));
		int outIndex = 0;
		for (int jBlock = 0 ; jBlock < (blockColumns) ; ++jBlock) {
			final int jWidth = blockWidth(jBlock);
			final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
			java.lang.System.arraycopy(block, (iRow * jWidth), out, outIndex, jWidth);
			outIndex += jWidth;
		}
		return out;
	}

	@java.lang.Override
	public void setRow(final int row, final T[] array) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException {
		checkRowIndex(row);
		final int nCols = getColumnDimension();
		if ((array.length) != nCols) {
			throw new org.apache.commons.math.linear.InvalidMatrixException("dimensions mismatch: got {0}x{1} but expected {2}x{3}" , 1 , array.length , 1 , nCols);
		} 
		final int iBlock = row / (BLOCK_SIZE);
		final int iRow = row - (iBlock * (BLOCK_SIZE));
		int outIndex = 0;
		for (int jBlock = 0 ; jBlock < (blockColumns) ; ++jBlock) {
			final int jWidth = blockWidth(jBlock);
			final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
			java.lang.System.arraycopy(array, outIndex, block, (iRow * jWidth), jWidth);
			outIndex += jWidth;
		}
	}

	@java.lang.Override
	public T[] getColumn(final int column) throws org.apache.commons.math.linear.MatrixIndexException {
		checkColumnIndex(column);
		final T[] out = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), rows);
		final int jBlock = column / (BLOCK_SIZE);
		final int jColumn = column - (jBlock * (BLOCK_SIZE));
		final int jWidth = blockWidth(jBlock);
		int outIndex = 0;
		for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
			final int iHeight = blockHeight(iBlock);
			final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
			for (int i = 0 ; i < iHeight ; ++i) {
				out[outIndex++] = block[((i * jWidth) + jColumn)];
			}
		}
		return out;
	}

	@java.lang.Override
	public void setColumn(final int column, final T[] array) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException {
		checkColumnIndex(column);
		final int nRows = getRowDimension();
		if ((array.length) != nRows) {
			throw new org.apache.commons.math.linear.InvalidMatrixException("dimensions mismatch: got {0}x{1} but expected {2}x{3}" , array.length , 1 , nRows , 1);
		} 
		final int jBlock = column / (BLOCK_SIZE);
		final int jColumn = column - (jBlock * (BLOCK_SIZE));
		final int jWidth = blockWidth(jBlock);
		int outIndex = 0;
		for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
			final int iHeight = blockHeight(iBlock);
			final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
			for (int i = 0 ; i < iHeight ; ++i) {
				block[((i * jWidth) + jColumn)] = array[outIndex++];
			}
		}
	}

	@java.lang.Override
	public T getEntry(final int row, final int column) throws org.apache.commons.math.linear.MatrixIndexException {
		try {
			final int iBlock = row / (BLOCK_SIZE);
			final int jBlock = column / (BLOCK_SIZE);
			final int k = ((row - (iBlock * (BLOCK_SIZE))) * (blockWidth(jBlock))) + (column - (jBlock * (BLOCK_SIZE)));
			return blocks[((iBlock * (blockColumns)) + jBlock)][k];
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new org.apache.commons.math.linear.MatrixIndexException("no entry at indices ({0}, {1}) in a {2}x{3} matrix" , row , column , getRowDimension() , getColumnDimension());
		}
	}

	@java.lang.Override
	public void setEntry(final int row, final int column, final T value) throws org.apache.commons.math.linear.MatrixIndexException {
		try {
			final int iBlock = row / (BLOCK_SIZE);
			final int jBlock = column / (BLOCK_SIZE);
			final int k = ((row - (iBlock * (BLOCK_SIZE))) * (blockWidth(jBlock))) + (column - (jBlock * (BLOCK_SIZE)));
			blocks[((iBlock * (blockColumns)) + jBlock)][k] = value;
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new org.apache.commons.math.linear.MatrixIndexException("no entry at indices ({0}, {1}) in a {2}x{3} matrix" , row , column , getRowDimension() , getColumnDimension());
		}
	}

	@java.lang.Override
	public void addToEntry(final int row, final int column, final T increment) throws org.apache.commons.math.linear.MatrixIndexException {
		try {
			final int iBlock = row / (BLOCK_SIZE);
			final int jBlock = column / (BLOCK_SIZE);
			final int k = ((row - (iBlock * (BLOCK_SIZE))) * (blockWidth(jBlock))) + (column - (jBlock * (BLOCK_SIZE)));
			final T[] blockIJ = blocks[((iBlock * (blockColumns)) + jBlock)];
			blockIJ[k] = blockIJ[k].add(increment);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new org.apache.commons.math.linear.MatrixIndexException("no entry at indices ({0}, {1}) in a {2}x{3} matrix" , row , column , getRowDimension() , getColumnDimension());
		}
	}

	@java.lang.Override
	public void multiplyEntry(final int row, final int column, final T factor) throws org.apache.commons.math.linear.MatrixIndexException {
		try {
			final int iBlock = row / (BLOCK_SIZE);
			final int jBlock = column / (BLOCK_SIZE);
			final int k = ((row - (iBlock * (BLOCK_SIZE))) * (blockWidth(jBlock))) + (column - (jBlock * (BLOCK_SIZE)));
			final T[] blockIJ = blocks[((iBlock * (blockColumns)) + jBlock)];
			blockIJ[k] = blockIJ[k].multiply(factor);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new org.apache.commons.math.linear.MatrixIndexException("no entry at indices ({0}, {1}) in a {2}x{3} matrix" , row , column , getRowDimension() , getColumnDimension());
		}
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> transpose() {
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		final org.apache.commons.math.linear.BlockFieldMatrix<T> out = new org.apache.commons.math.linear.BlockFieldMatrix<T>(getField() , nCols , nRows);
		int blockIndex = 0;
		for (int iBlock = 0 ; iBlock < (blockColumns) ; ++iBlock) {
			for (int jBlock = 0 ; jBlock < (blockRows) ; ++jBlock) {
				final T[] outBlock = out.blocks[blockIndex];
				final T[] tBlock = blocks[((jBlock * (blockColumns)) + iBlock)];
				final int pStart = iBlock * (BLOCK_SIZE);
				final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), columns);
				final int qStart = jBlock * (BLOCK_SIZE);
				final int qEnd = java.lang.Math.min((qStart + (BLOCK_SIZE)), rows);
				int k = 0;
				for (int p = pStart ; p < pEnd ; ++p) {
					final int lInc = pEnd - pStart;
					int l = p - pStart;
					for (int q = qStart ; q < qEnd ; ++q) {
						outBlock[k] = tBlock[l];
						++k;
						l += lInc;
					}
				}
				++blockIndex;
			}
		}
		return out;
	}

	@java.lang.Override
	public int getRowDimension() {
		return rows;
	}

	@java.lang.Override
	public int getColumnDimension() {
		return columns;
	}

	@java.lang.Override
	public T[] operate(final T[] v) throws java.lang.IllegalArgumentException {
		if ((v.length) != (columns)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", v.length, columns);
		} 
		final T[] out = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), rows);
		final T zero = getField().getZero();
		for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
			final int pStart = iBlock * (BLOCK_SIZE);
			final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
			for (int jBlock = 0 ; jBlock < (blockColumns) ; ++jBlock) {
				final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
				final int qStart = jBlock * (BLOCK_SIZE);
				final int qEnd = java.lang.Math.min((qStart + (BLOCK_SIZE)), columns);
				int k = 0;
				for (int p = pStart ; p < pEnd ; ++p) {
					T sum = zero;
					int q = qStart;
					while (q < (qEnd - 3)) {
						sum = sum.add(block[k].multiply(v[q])).add(block[(k + 1)].multiply(v[(q + 1)])).add(block[(k + 2)].multiply(v[(q + 2)])).add(block[(k + 3)].multiply(v[(q + 3)]));
						k += 4;
						q += 4;
					}
					while (q < qEnd) {
						sum = sum.add(block[k++].multiply(v[q++]));
					}
					out[p] = out[p].add(sum);
				}
			}
		}
		return out;
	}

	@java.lang.Override
	public T[] preMultiply(final T[] v) throws java.lang.IllegalArgumentException {
		if ((v.length) != (rows)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", v.length, rows);
		} 
		final T[] out = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), columns);
		final T zero = getField().getZero();
		for (int jBlock = 0 ; jBlock < (blockColumns) ; ++jBlock) {
			final int jWidth = blockWidth(jBlock);
			final int jWidth2 = jWidth + jWidth;
			final int jWidth3 = jWidth2 + jWidth;
			final int jWidth4 = jWidth3 + jWidth;
			final int qStart = jBlock * (BLOCK_SIZE);
			final int qEnd = java.lang.Math.min((qStart + (BLOCK_SIZE)), columns);
			for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
				final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
				final int pStart = iBlock * (BLOCK_SIZE);
				final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
				for (int q = qStart ; q < qEnd ; ++q) {
					int k = q - qStart;
					T sum = zero;
					int p = pStart;
					while (p < (pEnd - 3)) {
						sum = sum.add(block[k].multiply(v[p])).add(block[(k + jWidth)].multiply(v[(p + 1)])).add(block[(k + jWidth2)].multiply(v[(p + 2)])).add(block[(k + jWidth3)].multiply(v[(p + 3)]));
						k += jWidth4;
						p += 4;
					}
					while (p < pEnd) {
						sum = sum.add(block[k].multiply(v[p++]));
						k += jWidth;
					}
					out[q] = out[q].add(sum);
				}
			}
		}
		return out;
	}

	@java.lang.Override
	public T walkInRowOrder(final org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException {
		visitor.start(rows, columns, 0, ((rows) - 1), 0, ((columns) - 1));
		for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
			final int pStart = iBlock * (BLOCK_SIZE);
			final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
			for (int p = pStart ; p < pEnd ; ++p) {
				for (int jBlock = 0 ; jBlock < (blockColumns) ; ++jBlock) {
					final int jWidth = blockWidth(jBlock);
					final int qStart = jBlock * (BLOCK_SIZE);
					final int qEnd = java.lang.Math.min((qStart + (BLOCK_SIZE)), columns);
					final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
					int k = (p - pStart) * jWidth;
					for (int q = qStart ; q < qEnd ; ++q) {
						block[k] = visitor.visit(p, q, block[k]);
						++k;
					}
				}
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInRowOrder(final org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException {
		visitor.start(rows, columns, 0, ((rows) - 1), 0, ((columns) - 1));
		for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
			final int pStart = iBlock * (BLOCK_SIZE);
			final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
			for (int p = pStart ; p < pEnd ; ++p) {
				for (int jBlock = 0 ; jBlock < (blockColumns) ; ++jBlock) {
					final int jWidth = blockWidth(jBlock);
					final int qStart = jBlock * (BLOCK_SIZE);
					final int qEnd = java.lang.Math.min((qStart + (BLOCK_SIZE)), columns);
					final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
					int k = (p - pStart) * jWidth;
					for (int q = qStart ; q < qEnd ; ++q) {
						visitor.visit(p, q, block[k]);
						++k;
					}
				}
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInRowOrder(final org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
		for (int iBlock = startRow / (BLOCK_SIZE) ; iBlock < (1 + (endRow / (BLOCK_SIZE))) ; ++iBlock) {
			final int p0 = iBlock * (BLOCK_SIZE);
			final int pStart = java.lang.Math.max(startRow, p0);
			final int pEnd = java.lang.Math.min(((iBlock + 1) * (BLOCK_SIZE)), (1 + endRow));
			for (int p = pStart ; p < pEnd ; ++p) {
				for (int jBlock = startColumn / (BLOCK_SIZE) ; jBlock < (1 + (endColumn / (BLOCK_SIZE))) ; ++jBlock) {
					final int jWidth = blockWidth(jBlock);
					final int q0 = jBlock * (BLOCK_SIZE);
					final int qStart = java.lang.Math.max(startColumn, q0);
					final int qEnd = java.lang.Math.min(((jBlock + 1) * (BLOCK_SIZE)), (1 + endColumn));
					final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
					int k = (((p - p0) * jWidth) + qStart) - q0;
					for (int q = qStart ; q < qEnd ; ++q) {
						block[k] = visitor.visit(p, q, block[k]);
						++k;
					}
				}
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInRowOrder(final org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
		for (int iBlock = startRow / (BLOCK_SIZE) ; iBlock < (1 + (endRow / (BLOCK_SIZE))) ; ++iBlock) {
			final int p0 = iBlock * (BLOCK_SIZE);
			final int pStart = java.lang.Math.max(startRow, p0);
			final int pEnd = java.lang.Math.min(((iBlock + 1) * (BLOCK_SIZE)), (1 + endRow));
			for (int p = pStart ; p < pEnd ; ++p) {
				for (int jBlock = startColumn / (BLOCK_SIZE) ; jBlock < (1 + (endColumn / (BLOCK_SIZE))) ; ++jBlock) {
					final int jWidth = blockWidth(jBlock);
					final int q0 = jBlock * (BLOCK_SIZE);
					final int qStart = java.lang.Math.max(startColumn, q0);
					final int qEnd = java.lang.Math.min(((jBlock + 1) * (BLOCK_SIZE)), (1 + endColumn));
					final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
					int k = (((p - p0) * jWidth) + qStart) - q0;
					for (int q = qStart ; q < qEnd ; ++q) {
						visitor.visit(p, q, block[k]);
						++k;
					}
				}
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInOptimizedOrder(final org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException {
		visitor.start(rows, columns, 0, ((rows) - 1), 0, ((columns) - 1));
		int blockIndex = 0;
		for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
			final int pStart = iBlock * (BLOCK_SIZE);
			final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
			for (int jBlock = 0 ; jBlock < (blockColumns) ; ++jBlock) {
				final int qStart = jBlock * (BLOCK_SIZE);
				final int qEnd = java.lang.Math.min((qStart + (BLOCK_SIZE)), columns);
				final T[] block = blocks[blockIndex];
				int k = 0;
				for (int p = pStart ; p < pEnd ; ++p) {
					for (int q = qStart ; q < qEnd ; ++q) {
						block[k] = visitor.visit(p, q, block[k]);
						++k;
					}
				}
				++blockIndex;
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInOptimizedOrder(final org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException {
		visitor.start(rows, columns, 0, ((rows) - 1), 0, ((columns) - 1));
		int blockIndex = 0;
		for (int iBlock = 0 ; iBlock < (blockRows) ; ++iBlock) {
			final int pStart = iBlock * (BLOCK_SIZE);
			final int pEnd = java.lang.Math.min((pStart + (BLOCK_SIZE)), rows);
			for (int jBlock = 0 ; jBlock < (blockColumns) ; ++jBlock) {
				final int qStart = jBlock * (BLOCK_SIZE);
				final int qEnd = java.lang.Math.min((qStart + (BLOCK_SIZE)), columns);
				final T[] block = blocks[blockIndex];
				int k = 0;
				for (int p = pStart ; p < pEnd ; ++p) {
					for (int q = qStart ; q < qEnd ; ++q) {
						visitor.visit(p, q, block[k]);
						++k;
					}
				}
				++blockIndex;
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInOptimizedOrder(final org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
		for (int iBlock = startRow / (BLOCK_SIZE) ; iBlock < (1 + (endRow / (BLOCK_SIZE))) ; ++iBlock) {
			final int p0 = iBlock * (BLOCK_SIZE);
			final int pStart = java.lang.Math.max(startRow, p0);
			final int pEnd = java.lang.Math.min(((iBlock + 1) * (BLOCK_SIZE)), (1 + endRow));
			for (int jBlock = startColumn / (BLOCK_SIZE) ; jBlock < (1 + (endColumn / (BLOCK_SIZE))) ; ++jBlock) {
				final int jWidth = blockWidth(jBlock);
				final int q0 = jBlock * (BLOCK_SIZE);
				final int qStart = java.lang.Math.max(startColumn, q0);
				final int qEnd = java.lang.Math.min(((jBlock + 1) * (BLOCK_SIZE)), (1 + endColumn));
				final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
				for (int p = pStart ; p < pEnd ; ++p) {
					int k = (((p - p0) * jWidth) + qStart) - q0;
					for (int q = qStart ; q < qEnd ; ++q) {
						block[k] = visitor.visit(p, q, block[k]);
						++k;
					}
				}
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInOptimizedOrder(final org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
		for (int iBlock = startRow / (BLOCK_SIZE) ; iBlock < (1 + (endRow / (BLOCK_SIZE))) ; ++iBlock) {
			final int p0 = iBlock * (BLOCK_SIZE);
			final int pStart = java.lang.Math.max(startRow, p0);
			final int pEnd = java.lang.Math.min(((iBlock + 1) * (BLOCK_SIZE)), (1 + endRow));
			for (int jBlock = startColumn / (BLOCK_SIZE) ; jBlock < (1 + (endColumn / (BLOCK_SIZE))) ; ++jBlock) {
				final int jWidth = blockWidth(jBlock);
				final int q0 = jBlock * (BLOCK_SIZE);
				final int qStart = java.lang.Math.max(startColumn, q0);
				final int qEnd = java.lang.Math.min(((jBlock + 1) * (BLOCK_SIZE)), (1 + endColumn));
				final T[] block = blocks[((iBlock * (blockColumns)) + jBlock)];
				for (int p = pStart ; p < pEnd ; ++p) {
					int k = (((p - p0) * jWidth) + qStart) - q0;
					for (int q = qStart ; q < qEnd ; ++q) {
						visitor.visit(p, q, block[k]);
						++k;
					}
				}
			}
		}
		return visitor.end();
	}

	private int blockHeight(final int blockRow) {
		return blockRow == ((blockRows) - 1) ? (rows) - (blockRow * (BLOCK_SIZE)) : BLOCK_SIZE;
	}

	private int blockWidth(final int blockColumn) {
		return blockColumn == ((blockColumns) - 1) ? (columns) - (blockColumn * (BLOCK_SIZE)) : BLOCK_SIZE;
	}
}

