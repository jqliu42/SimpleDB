package simpledb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;
    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    private Op what;

    private Map<Field,Integer> groupMap;
    private Map<Field,Integer> countMap;
    private Map<Field, List<Integer>> avgMap;
    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
    	this.gbfield = gbfield;
    	this.gbfieldtype = gbfieldtype;
    	this.afield = afield;
    	this.what = what;
    	
    	this.groupMap = new HashMap<>();
    	this.avgMap = new HashMap<>();
    	this.countMap = new HashMap<>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
    	IntField aField = (IntField)tup.getField(this.afield);
    	Field gbField = this.gbfield == NO_GROUPING?null:tup.getField(this.gbfield);
    	int newValue = aField.getValue();
    	
    	if(gbField != null && gbField.getType()!=this.gbfieldtype) {
    		throw new IllegalArgumentException("Given tuple has wrong type");
    	}
    	
    	switch (this.what) {
		case MIN: {
			if(!this.groupMap.containsKey(gbField)) {
				this.groupMap.put(gbField, newValue);
			}else {
				this.groupMap.put(gbField,Math.min(this.groupMap.get(gbField),newValue));
			}
			break;
			
			
		}
		
		case MAX:{
			if(!this.groupMap.containsKey(gbField)) {
				this.groupMap.put(gbField, newValue);
			}else {
				this.groupMap.put(gbField,Math.max(this.groupMap.get(gbField),newValue));
			}
			break;
		}
		
		case SUM:{
			if(!this.groupMap.containsKey(gbField)) {
				this.groupMap.put(gbField, newValue);
			}else {
				this.groupMap.put(gbField,this.groupMap.get(gbField)+newValue);
			}
			break;
		}
		
		case COUNT:{
			if(!this.groupMap.containsKey(gbField)) {
				this.groupMap.put(gbField, 1);
			}else {
				this.groupMap.put(gbField,this.groupMap.get(gbField)+1);
			}
			break;
		}
		
		case SC_AVG:{
			// placeholder
			break;
		}
		
		case SUM_COUNT:{
			break;
		}
		
		case AVG:{
			if(!this.avgMap.containsKey(gbField)) {
				List<Integer> l = new ArrayList<>();
				l.add(newValue);
				this.avgMap.put(gbField,l);
			}else {
				List<Integer> l = this.avgMap.get(gbField);
				l.add(newValue);
			}
		}
		
		default:
			throw new IllegalArgumentException("Aggregate not supported!");
		}
    	
    }

    /**
     * Create a DbIterator over group aggregate results.
     * 
     * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        //placeholder
    	return null;
    }
    
    
    
    

}
