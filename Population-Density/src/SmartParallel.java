import java.util.concurrent.ForkJoinPool;


/**
 * @author M V S Prashanth
 *
 */
public class SmartParallel extends PopulationQueryVersion{
	protected int[][] gridData;
	final ForkJoinPool pool = new ForkJoinPool();

	public SmartParallel(int x, int y, CensusData data) {
		super(x, y, data);
		gridData = new int[x][y];
	}

	@Override
	public void preprocess() {
		if (censusData.data_size == 0)
			return;
		ResultData result = pool.invoke(new PreProcessor(0, censusData.data_size,this));

		setRectangle(result.rectangle);
		setPoupulation(result.pop);

		gridData = pool.invoke(new SmarterPreProcessor(0, censusData.data_size, this));

		for (int i = 1; i < gridData.length; i++) {
			gridData[i][gridData[0].length - 1] += gridData[i - 1][gridData[0].length - 1];
		}

		for (int i = gridData[0].length - 2; i >= 0; i--) {
			gridData[0][i] += gridData[0][i + 1];
		}

		for (int j = gridData[0].length - 1 - 1; j >= 0; j--) {
			for (int i = 1; i < gridData.length; i++) {
				gridData[i][j] += (gridData[i - 1][j] + gridData[i][j + 1] - gridData[i - 1][j + 1]);
			}
		}

	}

	@Override
	public int execute(int west, int south, int east, int north) {

		int population = 0;
		population += gridData[east - 1][south - 1];
		population -= (north == rows ? 0 : gridData[east - 1][north]); // top right
		population -= (west == 1 ? 0 : gridData[west - 1 - 1][south - 1]); // bottom left
		population += (west == 1 || north == rows ? 0 : gridData[west - 1 - 1][north]); // top left
		return population;
	}

}
