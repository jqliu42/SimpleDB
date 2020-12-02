package simpledb;

import java.util.HashMap;
import java.util.Map;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;
    
    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    private Op what;
    
    private Map<Field,Integer> groupMap;

    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
    	if(!what.equals(Op.COUNT)) {
    		throw new IllegalArgumentException("Only COUNT is supported for StringAggregator");
    	}
    	this.gbfield = gbfield;
    	this.gbfieldtype = gbfieldtype;
    	this.afield = afield;
    	this.what = what;
    	this.groupMap = new HashMap<>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
    	StringField afield = (StringField)tup.getField(this.afield);
    	Field gbField = this.gbfield==NO_GROUPING?null:tup.getField(this.gbfield);
    	String newValue = afield.getValue();
    	
    	if(gbField!=null&&gbField.getType()!=this.gbfieldtype) {
    		throw new IllegalArgumentException("Given tuple has wrong type");
    	}
    	
    	if(!this.groupMap.containsKey(gbField)) {
    		this.groupMap.put(gbField, 1);
    	}else {
    		this.groupMap.put(gbField,this.groupMap.get(gbField)+1);
    	}
    }

    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
    	return null;
    }

}
