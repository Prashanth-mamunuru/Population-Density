

/**
 * @author M V S Prashanth
 *
 */
public class SimpleSequential extends PopulationQueryVersion{
    public SimpleSequential(int x, int y, CensusData data) {
        super(x, y, data);
    }

    @Override
    public int execute(int west, int south, int east, int north) {
        if (rectangle == null)
            return 0;

        CensusGroup group;
        int totalPopulation = 0;
        double groupLong, groupLat;

        
        double left = (yAxis + (west - 1) * gridWidth);
        double right = (yAxis + (east) * gridWidth);
        double top = (xAxis + (north) * gridHeight);
        double bottom = (xAxis + (south - 1) * gridHeight);

        for (int i = 0; i < censusData.data_size; i++) {
            group = censusData.data[i];
            groupLong = group.longitude;
            groupLat = group.latitude;
            // Defaults to North and/or East in case of tie
            if (groupLat >= bottom &&
                    (groupLat < top ||
                            (top - rectangle.top) >= 0) &&
                            (groupLong < right ||
                                    (right - rectangle.right) >= 0) &&
                                    groupLong >= left)
                totalPopulation += group.population;
        }

        return totalPopulation;
    }

  
    @Override
    public void preprocess() {
        if (censusData.data_size == 0)
            return;

        int pop = 0;
        CensusGroup group = censusData.data[0];

        Rectangle rec = new Rectangle(group.longitude, group.longitude,
                group.latitude, group.latitude), temp;
        pop += group.population;

        for (int i = 1; i < censusData.data_size; i++) {
            group = censusData.data[i];
            temp = new Rectangle(group.longitude, group.longitude,
                    group.latitude, group.latitude);
            rec = rec.encompass(temp);
            pop += group.population;
        }

        setRectangle(rec);
        setPoupulation(pop);

        
    }
}
