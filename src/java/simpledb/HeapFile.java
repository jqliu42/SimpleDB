package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {
	
	private final File f;
	private final TupleDesc td;

    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
    	this.f = f;
    	this.td = td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return f;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
       return f.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        int tableId = pid.getTableId();
        int pgNo = pid.pageNumber();
        
        RandomAccessFile rf = null;
        
        try {
			rf = new RandomAccessFile(this.f, "r");
			
			byte[] bytes = new byte[BufferPool.getPageSize()];
			rf.seek(pgNo * BufferPool.getPageSize());
			
			int read = rf.read(bytes,0,BufferPool.getPageSize());
			
			HeapPageId id = new HeapPageId(tableId, pgNo);
			rf.close();
			return new HeapPage(id,bytes);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
       
        throw new IllegalArgumentException();
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    	int pgNo = page.getId().pageNumber();
    	if(pgNo > numPages()) {
    		throw new IllegalArgumentException();
    	}
    	int pgSize = BufferPool.getPageSize();
    	RandomAccessFile f = new RandomAccessFile(this.f, "rw");
    	f.seek(pgNo*pgSize);
    	
    	byte[] data = page.getPageData();
    	f.write(data);
    	f.close();
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
    	
        int num = (int)Math.floor(this.f.length()*1.0/BufferPool.getPageSize());
        return num;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
    	
    	ArrayList<Page> pageList = new ArrayList<Page>();
    	for(int i=0;i<numPages();++i) {
    		HeapPage p = (HeapPage)Database.getBufferPool().getPage(tid, new HeapPageId(this.getId(),i), Permissions.READ_ONLY);
    		if(p.getNumEmptySlots()==0) {
    			continue;
    		}
    		p.insertTuple(t);
    		pageList.add(p);
    		return pageList;
    	}
    	
    	BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(f));
    	byte[] emptyData = HeapPage.createEmptyPageData();
    	bw.write(emptyData);
    	bw.close();
    	
    	HeapPage p = (HeapPage)Database.getBufferPool().getPage(tid, new HeapPageId(this.getId(),numPages()-1), Permissions.READ_WRITE);
    	p.insertTuple(t);
    	pageList.add(p);
    	
    	return pageList;
    	
   
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        ArrayList<Page> pageList = new ArrayList<Page>();
        HeapPage p = (HeapPage)Database.getBufferPool().getPage(tid, t.getRecordId().getPageId(), Permissions.READ_WRITE);
        p.deleteTuple(t);
        pageList.add(p);
        return pageList;
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
    	
        return new HeapFileIterator(this,tid);
    }

}

