

/**
 * @author M V S Prashanth
 * @author Nishitha Nallaparaju
 *
 */
public class LockedPreProcessor extends java.lang.Thread{
	int high, low;
	CensusData censusData;
	SmartLockBased smarterQueryVersion;

	LockedPreProcessor(int lo, int hi, SmartLockBased censusData) {
		this.low = lo;
		this.high = hi;
		this.smarterQueryVersion = censusData;
	}

	LockedPreProcessor(int low, int high) {
		this.low = low;
		this.high = high;
	}

	@Override
	public void run() {
		if (high - low < smarterQueryVersion.cutoff) {
			CensusGroup group;
			int row, col;
			for (int i = low; i < high; i++) {
				group = smarterQueryVersion.censusData.data[i];
				col = (int) ((group.latitude - smarterQueryVersion.xAxis) / smarterQueryVersion.gridHeight);

				if (group.latitude >= (col + 1) * smarterQueryVersion.gridHeight + smarterQueryVersion.xAxis)
					col++;
				col = (col == smarterQueryVersion.rows ? smarterQueryVersion.rows - 1 : col); // edge case due to
																								// rounding
				row = (int) ((group.longitude - smarterQueryVersion.yAxis) / smarterQueryVersion.gridWidth);

				if (group.longitude >= (row + 1) * smarterQueryVersion.gridWidth + smarterQueryVersion.yAxis)
					col++;
				row = (row == smarterQueryVersion.columns ? smarterQueryVersion.columns - 1 : row); // edge case due to
																									// rounding

				smarterQueryVersion.locks[row][col].lock();
				try {
					smarterQueryVersion.gridData[row][col] += group.population;
				} finally {
					smarterQueryVersion.locks[row][col].unlock();
				}
			}

		} else {
			LockedPreProcessor left = new LockedPreProcessor(low, (high + low) / 2, smarterQueryVersion);
			LockedPreProcessor right = new LockedPreProcessor((high + low) / 2, high, smarterQueryVersion);

			left.start();
			right.run();
			try {
				left.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
