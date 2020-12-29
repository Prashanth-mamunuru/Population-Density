


/**
 * @author M V S Prashanth
 * @author Nishitha Nallaparaju
 */
public abstract class PopulationQueryVersion implements PopulationDensityIF {

	protected final int columns;

	protected final int rows;

	protected Rectangle rectangle = null;

	protected float yAxis = 0;
	protected float xAxis = 0;

	protected float gridWidth = 0;
	protected float gridHeight = 0;

	protected final CensusData censusData;

	protected int totalPopulation = 0;

	protected int cutoff = 100;

	public PopulationQueryVersion(int colums, int rows, CensusData data) {
		if (data == null)
			throw new NullPointerException("Data is not valid");
		if (colums < 1 || rows < 1)
			throw new IndexOutOfBoundsException("values must be greater than 1");
		this.columns = colums;
		this.rows = rows;
		this.censusData = data;

	}

	public int getPopulation() {
		return totalPopulation;
	}

	public void setCutoff(int n) {
		cutoff = n;
	}
	
	public void setRectangle(Rectangle rectangle) {
		this.rectangle=rectangle;


        yAxis = rectangle.left;
        xAxis = rectangle.bottom;
        gridWidth = (rectangle.right - rectangle.left) / columns;
        gridHeight = (rectangle.top - rectangle.bottom) / rows;
	}
	
	public void setPoupulation(int poulation) {
		this.totalPopulation=poulation;
	}

	@Override
	public int execute(int west, int south, int east, int north) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void preprocess() {
		// TODO Auto-generated method stub

	}

}
