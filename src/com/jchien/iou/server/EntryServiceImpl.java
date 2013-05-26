package com.jchien.iou.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.jchien.iou.client.EntryService;
import com.jchien.iou.shared.Borrower;
import com.jchien.iou.shared.Entry;

@Service
public class EntryServiceImpl extends SpringBeanAutowiringSupport implements
		InitializingBean, DisposableBean, EntryService {

	@Autowired
	EntryMapper entryMapper;

	Connection con = null;
	Statement st = null;
	ResultSet rs = null;
	String url = "jdbc:postgresql://localhost:5432/iou";
	String user = "postgres";
	String password = "admin";

	private void connect() {
		try {
			try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
		} catch (SQLException ex) {
			System.out.println(ex);
		}
	}

	private boolean isConnected() {
		return con != null && st != null;
	}

	@Override
	public List<Entry> getAllEntries(int id) {
		if (!isConnected()) {
			connect();
		}
		List<Entry> result = new ArrayList<Entry>();
		String getQuery = "SELECT id, title, date, issuer, amountdue, amountpaid FROM entries WHERE issuer="
				+ id + " ORDER BY id ASC";

		try {
			ResultSet myResult = st.executeQuery(getQuery);
			// Generate a list of entries
			while (myResult.next()) {
				int entryId = myResult.getInt(1);
				String title = myResult.getString(2);
				String date = myResult.getString(3);
				int issuer = myResult.getInt(4);
				double amountdue = myResult.getFloat(5);
				double amountpaid = myResult.getFloat(6);
				Entry e = new Entry(title, date, amountpaid, amountdue,
						entryId, issuer);
				result.add(e);
			}
			// Associate the Borrowers with the entries
			// This gets all borrowers associated to entries posted by this user
			if (result.size() == 0) {
				return result;
			}
			String getAllBorrowers = "SELECT * FROM borrowers WHERE entryid in (";
			int index = 0;
			for (Entry e : result) {
				getAllBorrowers += e.getId();
				if (index != result.size() - 1) {
					getAllBorrowers += ",";
				}
				index++;
			}
			getAllBorrowers += ") ORDER BY entryid ASC";
			myResult = st.executeQuery(getAllBorrowers);
			int curEntryId = -1;
			int entryNum = 0;
			ArrayList<Borrower> borrowers = new ArrayList<Borrower>();
			while (myResult.next()) {
				int entryId = myResult.getInt(1);
				boolean paid = myResult.getInt(4) == 1;
				// This borrower is a part of another entry
				if (curEntryId != -1 && curEntryId != entryId) {
					copyBorrowerstoEntry(result, borrowers, entryNum);
					// clear the ArrayList of borrowers
					borrowers.clear();
					entryNum++;
				}
				// Part of the same entry or the first borrower associated with
				// an entry
				curEntryId = entryId;
				borrowers.add(new Borrower(myResult.getString(2), new Double(
						myResult.getDouble(3)).toString(), paid));
			}
			// Copy the last borrowers to the last entry
			copyBorrowerstoEntry(result, borrowers, entryNum);
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private void copyBorrowerstoEntry(List<Entry> entries,
			List<Borrower> borrowers, int index) {
		// Make a deep copy of the elements since you will be
		// clearng this ArrayList soon
		ArrayList<Borrower> tempBorrowers = new ArrayList<Borrower>();
		for (Borrower b : borrowers) {
			tempBorrowers.add(new Borrower(b.getNameString(), b
					.getAmountString(), b.isPaid()));
		}
		// associate the arraylist with the entry at index
		entries.get(index).setBorrowers(tempBorrowers);
	}

	/** Adds an entry. Query is implemented and called here **/
	@Override
	public Entry addEntry(String title, int id, double amountDue,
			List<Borrower> borrowers) {
		if (!isConnected()) {
			connect();
		}
		String entryQuery = "INSERT INTO entries (title, date, issuer, amountdue, numberborrowers, amountpaid) VALUES('"
				+ title
				+ "', NOW() ,"
				+ id
				+ ","
				+ amountDue
				+ ","
				+ borrowers.size() + ", 0) RETURNING id";

		try {
			// Get the ResultSet containing the id for the entry
			ResultSet result = st.executeQuery(entryQuery);
			// Position the cursor
			result.next();
			// Get the value of the id from the cursor
			int entryID = result.getInt(1);

			// Add the borrowers to the table using the entryID
			addBorrowers(entryID, borrowers);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			Entry resultingEntry = new Entry(title, dateFormat.format(date),
					0.0, amountDue, entryID, id,
					(ArrayList<Borrower>) borrowers);
			return resultingEntry;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	/** Updates an entry. Query is implemented and called here **/
	@Override
	public void updateEntry(Entry e, List<Borrower> borrowers) {
		if (!isConnected()) {
			connect();
		}
		String entryQuery = "UPDATE entries SET title= '" + e.getTitle()
				+ "', issuer=" + e.getIssuerID() + ", amountDue = "
				+ e.getAmountDue() + ", numberBorrowers = " + borrowers.size()
				+ ", amountpaid = " + e.getAmountPaid() + " WHERE id= "
				+ e.getId();
		String deleteOldBorrowers = "DELETE FROM borrowers WHERE entryid="
				+ e.getId();
		try {
			st.executeUpdate(entryQuery);
			st.executeUpdate(deleteOldBorrowers);
			addBorrowers(e.getId(), borrowers);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void addBorrowers(int entryID, List<Borrower> borrowers)
			throws SQLException {
		String borrowerQuery = "INSERT into borrowers VALUES";
		for (int i = 0; i < borrowers.size(); i++) {
			Borrower borrower = borrowers.get(i);
			int hasPaid = (borrower.isPaid()) ? 1 : 0;
			borrowerQuery += "(" + entryID + ",'"
					+ (String) borrower.getNameString() + "',"
					+ (String) borrower.getAmountString() + "," + hasPaid + ")";
			if (i < borrowers.size() - 1) {
				borrowerQuery += ",";
			}
		}
		st.executeUpdate(borrowerQuery);
	}

	@Override
	public void removeEntry(int id) {
		entryMapper.removeEntry(id);
	}

	@Override
	public void destroy() throws Exception {

	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

}
