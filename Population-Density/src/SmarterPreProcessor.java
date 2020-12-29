import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;


/**
 * @author M V S Prashanth
 *
 */
@SuppressWarnings("serial")
public class SmarterPreProcessor extends RecursiveTask<int[][]> {

	int hi, lo;
	PopulationQueryVersion populationQueryVerison;
	final ForkJoinPool pool = new ForkJoinPool();

	public SmarterPreProcessor(int lo, int hi, PopulationQueryVersion populationQueryVerison) {
		this.lo = lo;
		this.hi = hi;
		this.populationQueryVerison = populationQueryVerison;
	}

	public SmarterPreProcessor(int lo, int hi) {
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	protected int[][] compute() {
		if (hi - lo < populationQueryVerison.cutoff) {
			CensusGroup group;
			int row, col;
			int[][] g = new int[populationQueryVerison.columns][populationQueryVerison.rows];

			for (int i = lo; i < hi; i++) {
				group = populationQueryVerison.censusData.data[i];
				col = (int) ((group.latitude - populationQueryVerison.xAxis) / populationQueryVerison.gridHeight);

				if (group.latitude >= (col + 1) * populationQueryVerison.gridHeight + populationQueryVerison.xAxis)
					col++;
				col = (col == populationQueryVerison.rows ? populationQueryVerison.rows - 1 : col);
				row = (int) ((group.longitude - populationQueryVerison.yAxis) / populationQueryVerison.gridWidth);

				if (group.longitude >= (row + 1) * populationQueryVerison.gridWidth + populationQueryVerison.yAxis)
					col++;
				row = (row == populationQueryVerison.columns ? populationQueryVerison.columns - 1 : row); // edge case
																											// due to
																											// rounding
				g[row][col] += group.population;

			}
			return g;

		} else {
			SmarterPreProcessor right = new SmarterPreProcessor((hi + lo) / 2, hi, populationQueryVerison);
			SmarterPreProcessor left = new SmarterPreProcessor(lo, (hi + lo) / 2, populationQueryVerison);

			left.fork();
			int[][] gRight = right.compute();
			int[][] gLeft = left.join();

			pool.invoke(new AddGrids(0, populationQueryVerison.columns, gLeft, gRight));
			return gRight;
		}

	}

	class AddGrids extends RecursiveAction {
		int xhi, xlo;
		int[][] l, r;

		AddGrids(int xlo, int xhi, int[][] l, int[][] r) {
			this.xlo = xlo;
			this.xhi = xhi;
			this.l = l;
			this.r = r;
		}

		@Override
		protected void compute() {

			if ((xhi - xlo) < populationQueryVerison.cutoff) {
				for (int i = xlo; i < xhi; i++) {
					for (int j = 0; j < populationQueryVerison.rows; j++)
						r[i][j] += l[i][j];
				}
			} else {
				AddGrids left = new AddGrids(xlo, (xhi + xlo) / 2, l, r);
				AddGrids right = new AddGrids((xhi + xlo) / 2, xhi, l, r);

				left.fork();
				right.compute();
				left.join();
			}

		}
	}

}
