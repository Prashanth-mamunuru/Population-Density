import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


/**
 * @author M V S Prashanth
 *
 */
public class SimpleParallel extends PopulationQueryVersion{
	public SimpleParallel(int x, int y, CensusData data) {
		super(x, y, data);

	}

	@SuppressWarnings("serial")
	class Query extends RecursiveTask<Integer> {
		int hi, lo;
		double left, right, top, bottom;

		Query(int lo, int hi, double leftBound, double rightBound, double topBound, double bottomBound) {
			this.lo = lo;
			this.hi = hi;
			this.left = leftBound;
			this.right = rightBound;
			this.top = topBound;
			this.bottom = bottomBound;
		}

		@Override
		protected Integer compute() {
			if (hi - lo < cutoff) {
				CensusGroup group;
				int population = 0;
				double groupLong, groupLat;

				for (int i = lo; i < hi; i++) {
					group = censusData.data[i];
					groupLong = group.longitude;
					groupLat = group.latitude;

					if (groupLat >= bottom && (groupLat < top || (top - rectangle.top) >= 0)
							&& (groupLong < right || (right - rectangle.right) >= 0) && groupLong >= left)
						population += group.population;
				}

				return population;
			} else {
				Query left = new Query(lo, (hi + lo) / 2, this.left, right, top, bottom);
				Query right = new Query((hi + lo) / 2, hi, this.left, this.right, top, bottom);

				left.fork();
				Integer rightAns = right.compute();
				Integer leftAns = left.join();
				return rightAns + leftAns;
			}

		}
	}

	final ForkJoinPool pool = new ForkJoinPool();

	public int execute(int west, int south, int east, int north) {
		if (rectangle == null)
			return 0;
		double leftBound = (yAxis + (west - 1) * gridWidth);
		double rightBound = (yAxis + (east) * gridWidth);
		double topBound = (xAxis + (north) * gridHeight);
		double bottomBound = (xAxis + (south - 1) * gridHeight);
		return pool.invoke(new Query(0, censusData.data_size, leftBound, rightBound, topBound, bottomBound));
	}

	@Override
	public void preprocess() {
		if (censusData.data_size == 0)
			return;

		ResultData result = pool.invoke(new PreProcessor(0, censusData.data_size,this));
		setRectangle(result.rectangle);
		setPoupulation(result.pop);
	}


}
