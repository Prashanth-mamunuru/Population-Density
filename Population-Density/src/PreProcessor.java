import java.util.concurrent.RecursiveTask;


/**
 * @author M V S Prashanth
 * @author Nishitha Nallaparaju
 *
 */
@SuppressWarnings("serial")
class PreProcessor extends RecursiveTask<ResultData>  {
	 int high, low;
	    PopulationQueryVersion populationQuery;

	    
	    PreProcessor(int lo, int hi,PopulationQueryVersion populationQuery) {
	        this.low  = lo;
	        this.high = hi;
	        this.populationQuery=populationQuery;
	    }
	    PreProcessor(int lo, int hi) {
	        this.low  = lo;
	        this.high = hi;
	    }

	    
	    @Override
	    protected ResultData compute() {
	        if(high - low < populationQuery.cutoff) {
	            CensusGroup group = populationQuery.censusData.data[low];
	            int pop = group.population;
	            Rectangle rec = new Rectangle(group.longitude, group.longitude,
	                    group.latitude, group.latitude), temp;
	            for (int i = low + 1; i < high; i++) {
	                group = populationQuery.censusData.data[i];
	                temp = new Rectangle(group.longitude, group.longitude,
	                        group.latitude, group.latitude);
	                rec = rec.encompass(temp);
	                pop += group.population;
	            }
	              
	            return new ResultData(rec, pop);
	        } else {
	            PreProcessor left = new PreProcessor(low, (high+low)/2,populationQuery);
	            PreProcessor right = new PreProcessor((high+low)/2, high,populationQuery);

	            left.fork(); 
	            ResultData rightAns = right.compute();
	            ResultData leftAns = left.join();
	            return new ResultData(rightAns.rectangle.encompass(leftAns.rectangle),
	                    rightAns.pop + leftAns.pop);
	        }

	    }

}
