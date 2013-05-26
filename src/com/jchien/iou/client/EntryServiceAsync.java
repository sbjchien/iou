package com.jchien.iou.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.jchien.iou.shared.Borrower;
import com.jchien.iou.shared.Entry;

public interface EntryServiceAsync {

	public void getAllEntries(int id, AsyncCallback<List<Entry>> callback);

	public void addEntry(String title, int id, double amountDue,
			List<Borrower> borrowers, AsyncCallback<Entry> callback);

	public void updateEntry(Entry e, List<Borrower> borrowers,
			AsyncCallback callback);

	public void removeEntry(int id, AsyncCallback callback);
}
