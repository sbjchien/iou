package com.jchien.iou.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.jchien.iou.shared.Borrower;
import com.jchien.iou.shared.Entry;

@RemoteServiceRelativePath("entry")
public interface EntryService extends RemoteService {

	List<Entry> getAllEntries(int id);

	Entry addEntry(String title, int id, double amountDue,
			List<Borrower> borrowers);

	void updateEntry(Entry e, List<Borrower> borrowers);

	void removeEntry(int id);
}
