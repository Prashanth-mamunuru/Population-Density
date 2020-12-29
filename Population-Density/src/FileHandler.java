import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * @author M V S Prashanth
 * @author Nishitha nallaparaju
 *
 */
public class FileHandler {
	public  final int TOKENS_PER_LINE  = 7;
    public  final int POPULATION_INDEX = 4; // zero-based indices
    public  final int LATITUDE_INDEX   = 5;
    public  final int LONGITUDE_INDEX  = 6;

	
	public  CensusData parse(String filename) {
        CensusData result = new CensusData();

        try {
            BufferedReader inputFile = new BufferedReader(new FileReader(filename));

           

            String oneLine = inputFile.readLine(); 

            
            while ((oneLine = inputFile.readLine()) != null) {
                String[] tokens = oneLine.split(",");
                if(tokens.length != TOKENS_PER_LINE)
                    throw new NumberFormatException();
                int population = Integer.parseInt(tokens[POPULATION_INDEX]);
                if(population != 0)
                    result.add(population,
                            Float.parseFloat(tokens[LATITUDE_INDEX]),
                            Float.parseFloat(tokens[LONGITUDE_INDEX]));
            }

            inputFile.close();
        } catch(IOException ioe) {
        	System.out.println(ioe);
            System.err.println("Error opening/reading/writing input or output file.");
            System.exit(1);
        } catch(NumberFormatException nfe) {
            System.err.println(nfe.toString());
            System.err.println("Error in file format");
            System.exit(1);
        }
        return result;
    }

}
