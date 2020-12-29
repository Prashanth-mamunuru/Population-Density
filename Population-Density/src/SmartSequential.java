
/**
 * @author M V S Prashanth
 *
 */
public class SmartSequential extends PopulationQueryVersion{
	protected int[][] gridData;

	public SmartSequential(int x, int y, CensusData data) {
		super(x, y, data);
		gridData = new int[x][y];
	}

	public int execute(int west, int south, int east, int north) {

		int population = 0;
		population += gridData[east - 1][south - 1];
		population -= (north == rows ? 0 : gridData[east - 1][north]); 
		population -= (west == 1 ? 0 : gridData[west - 1 - 1][south - 1]); 
		population += (west == 1 || north == rows ? 0 : gridData[west - 1 - 1][north]); 
		return population;
	}

	@Override
	public void preprocess() {
		if (censusData.data_size == 0)
			return;

		int pop = 0;
		CensusGroup group = censusData.data[0];

		Rectangle rec = new Rectangle(group.longitude, group.longitude, group.latitude, group.latitude), temp;
		pop += group.population;

		for (int i = 1; i < censusData.data_size; i++) {
			group = censusData.data[i];
			temp = new Rectangle(group.longitude, group.longitude, group.latitude, group.latitude);
			rec = rec.encompass(temp);
			pop += group.population;
		}

		setRectangle(rec);
		setPoupulation(pop);

		int row, col;

		for (int i = 0; i < censusData.data_size; i++) {
			group = censusData.data[i];
			col = (int) ((group.latitude - xAxis) / gridHeight);

			if (group.latitude >= (col + 1) * gridHeight + xAxis)
				col++;
			col = (col == rows ? rows - 1 : col);
			row = (int) ((group.longitude - yAxis) / gridWidth);

			if (group.longitude >= (row + 1) * gridWidth + yAxis)
				col++;
			row = (row == columns ? columns - 1 : row);
			gridData[row][col] += group.population;

		}

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
