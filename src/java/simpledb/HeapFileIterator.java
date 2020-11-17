package simpledb;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class HeapFileIterator implements DbFileIterator {
	
	private final HeapFile heapFile;
	private final TransactionId tid;
	private Iterator<Tuple> it;
	private int whichPage;

	
	public HeapFileIterator(HeapFile file, TransactionId tid) {
		// TODO Auto-generated constructor stub
		this.heapFile = file;
		this.tid =tid;
	}

	
	private Iterator<Tuple> getPageTuples(int pageNumber) throws TransactionAbortedException, DbException{
		if(pageNumber >= 0 && pageNumber < heapFile.numPages()) {
			HeapPageId pid = new HeapPageId(heapFile.getId(), pageNumber);
			HeapPage page = (HeapPage)Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
			return page.iterator();
		}else {
			throw new DbException("");
		}
	}
	
	@Override
	public void open() throws DbException, TransactionAbortedException {
		// TODO Auto-generated method stub
		whichPage = 0;
		it = getPageTuples(whichPage);
	}

	@Override
	public boolean hasNext() throws DbException, TransactionAbortedException {
		// TODO Auto-generated method stub
		if(it == null) {
			return false;
		}
		
		if(!it.hasNext()) {
			if(whichPage < (heapFile.numPages()-1)) {
				whichPage++;
				it = getPageTuples(whichPage);
				return it.hasNext();
			}else {
				return false;
			}
		}else {
			return true;
		}
	}

	@Override
	public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
		// TODO Auto-generated method stub
		if(it == null || !it.hasNext()) {
			throw new NoSuchElementException();
		}
		return it.next();
	}

	@Override
	public void rewind() throws DbException, TransactionAbortedException {
		// TODO Auto-generated method stub
		close();
		open();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		it = null;

	}

}
