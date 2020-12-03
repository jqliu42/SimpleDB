package simpledb;

import java.io.IOException;

/**
 * Inserts tuples read from the child operator into the tableid specified in the
 * constructor
 */
public class Insert extends Operator {

    private static final long serialVersionUID = 1L;
    
    private TransactionId tid;
    private DbIterator child;
    private int tableid;
    private final TupleDesc td;
    
    private int counter;
    private boolean called;

    /**
     * Constructor.
     * 
     * @param t
     *            The transaction running the insert.
     * @param child
     *            The child operator from which to read tuples to be inserted.
     * @param tableid
     *            The table in which to insert tuples.
     * @throws DbException
     *             if TupleDesc of child differs from table into which we are to
     *             insert.
     */
    public Insert(TransactionId t,DbIterator child, int tableid)
            throws DbException {
        // some code goes here
    	
    	if(!child.getTupleDesc().equals(Database.getCatalog().getTupleDesc(tableid))) {
    		throw new DbException("TupleDesc does not match");
    	}
    	
    	this.tid = t;
    	this.child = child;
    	this.tableid = tableid;
    	this.td = new TupleDesc(new Type[] {Type.INT_TYPE},new String[] {"number of inserted tuples"});
    	
    	this.counter = -1;
    	this.called = false;
    	
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return this.td;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
    	this.child.open();
    	super.open();
    	this.counter = 0;
    }

    public void close() {
        // some code goes here
    	super.close();
    	this.child.close();
    	this.counter = -1;
    	this.called = false;
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
    	this.child.rewind();
    	this.counter = 0;
    	this.called = false;
    }

    /**
     * Inserts tuples read from child into the tableid specified by the
     * constructor. It returns a one field tuple containing the number of
     * inserted records. Inserts should be passed through BufferPool. An
     * instances of BufferPool is available via Database.getBufferPool(). Note
     * that insert DOES NOT need check to see if a particular tuple is a
     * duplicate before inserting it.
     * 
     * @return A 1-field tuple containing the number of inserted records, or
     *         null if called more than once.
     * @see Database#getBufferPool
     * @see BufferPool#insertTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
    	
    	if(this.called) {
    		return null;
    	}
    	
    	this.called = true;
    	while(this.child.hasNext()) {
    		Tuple t = this.child.next();
    		try {
    			Database.getBufferPool().insertTuple(tid, tableid, t);
    			this.counter++;
    		}catch(IOException e) {
    			e.printStackTrace();
    			break;
    		}
    	}
    	
    	Tuple tu = new Tuple(this.td);
    	tu.setField(0, new IntField(this.counter));
    	
        return tu;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        return new DbIterator[] {this.child};
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
    	this.child = children[0];
    }
}
