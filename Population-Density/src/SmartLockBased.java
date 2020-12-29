import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author M V S Prashanth
 *
 */
public class SmartLockBased extends PopulationQueryVersion{
public ReentrantLock[][] locks;
	
	protected int[][] gridData;

	public SmartLockBased(int x, int y, CensusData data) {
		super(x, y, data);
		gridData = new int[x][y];
		locks = new ReentrantLock[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				locks[i][j] = new ReentrantLock();
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
	
	@Override
	public void preprocess() {
		final ForkJoinPool pool = new ForkJoinPool();
		if (censusData.data_size == 0)
			return;
		ResultData result = pool.invoke(new PreProcessor(0, censusData.data_size,this));

		setRectangle(result.rectangle);
		setPoupulation(result.pop);
		

		LockedPreProcessor sp = new LockedPreProcessor(0, censusData.data_size, this);
		sp.run();

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

}
