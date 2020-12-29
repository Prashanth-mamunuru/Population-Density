import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 * @author M V S Prashanth
 * @author Nishitha Nallaparaju
 */
public class PopulationQuery {
	private PopulationDensityIF populationDensity;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = args[0];
		String xNum = args[1];
		String yNum = args[2];
		String version = args[3];

		int x = Integer.parseInt(xNum);
		int y = Integer.parseInt(yNum);
		int vNum;
		if (version.equals("-v1")) {
			vNum = 1;
		} else if (version.equals("-v2")) {
			vNum = 2;
		} else if (version.equals("-v3")) {
			vNum = 3;
		} else if (version.equals("-v4")) {
			vNum = 4;
		} else {// (version.equals("-v5"))
			vNum = 5;
		}
		PopulationQuery populationQuery = new PopulationQuery();
		fileName = "src/" + fileName;
//        System.out.println(fileName);
		populationQuery.preprocess(fileName, x, y, vNum);

		boolean fourArgs = true;
		while (fourArgs) {
			System.out.println("Please enter the west, south, east, north co-ordinates of the query rectangle:");

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String userInput = null;

			try {
				userInput = br.readLine();
			} catch (IOException ioe) {
				System.out.println("Input/Output error, trying to read your name!");
				System.exit(1);
			}

			String[] coordinates = userInput.split(" ");
			if (coordinates.length != 4)
				fourArgs = false; // exit the loop
			else {
				int west = Integer.parseInt(coordinates[0]);
				int south = Integer.parseInt(coordinates[1]);
				int east = Integer.parseInt(coordinates[2]);
				int north = Integer.parseInt(coordinates[3]);

				if (populationQuery.validate(west, south, east, north, x, y))
					throw new IndexOutOfBoundsException(" coordinate is outside the bounds of the grid");

				DecimalFormat df = new DecimalFormat("0.00");

				Pair<Integer, Float> pair = populationQuery.singleInteraction(west, south, east, north);
				System.out.println("population inside the given rectangle: " + pair.getElementA());
				String percent = df.format(pair.getElementB());
				System.out.println("percentage of the total population: " + percent);
			}
		}
	}

	public Pair<Integer, Float> singleInteraction(int w, int s, int e, int n) {
		float totPop = (float) populationDensity.getPopulation();
		if (totPop == 0)
			return new Pair<Integer, Float>(0, totPop);
		int areaPop = populationDensity.execute(w, s, e, n);
		return new Pair<Integer, Float>(areaPop, (100 * (float) areaPop / totPop));
	}

	public void preprocess(String filename, int columns, int rows, int versionNum) {

		FileHandler fileHandler = new FileHandler();

		CensusData data = fileHandler.parse(filename.trim());

		if (data == null)
			throw new NullPointerException("No population to process - check your file");
		if (columns < 1 || rows < 1)
			throw new IndexOutOfBoundsException("positive row/column numbers expected");

		switch (versionNum) {
		case 1:
			populationDensity = new SimpleSequential(columns, rows, data);
			populationDensity.preprocess();
			break;
		case 2:
			populationDensity = new SimpleParallel(columns, rows, data);
			populationDensity.preprocess();
			break;
		case 3:
			populationDensity = new SmartSequential(columns, rows, data);
			populationDensity.preprocess();
			break;
		case 4:
			populationDensity = new SmartParallel(columns, rows, data);
			populationDensity.preprocess();
			break;
		case 5:
			populationDensity = new SmartLockBased(columns, rows, data);
			populationDensity.preprocess();
			break;
		default:
			populationDensity = null;
			break;
		}
	}

	private boolean validate(int west, int south, int east, int north, int x, int y) {
		return (west < 1 || west > x || south < 1 || south > y || east < west || east > x || north < south
				|| north > y);
	}

	public CensusData parse(String file) {
		FileHandler fileHandler = new FileHandler();

		return fileHandler.parse(file);
	}

}
