package com.jchien.iou.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.jchien.iou.shared.Borrower;
import com.jchien.iou.shared.Entry;

public class EntryHandler implements ClickHandler {
	private EntryDialog dialog;
	private boolean isNew;
	private EntryServiceAsync manageEntrySvc = GWT.create(EntryService.class);

	public EntryHandler(EntryDialog e, boolean newEntry) {
		dialog = e;
		isNew = newEntry;
	}

	@Override
	public void onClick(ClickEvent event) {
		// Get the entry from the dialog
		final Entry entry = dialog.getEntry();
		// Determine whether this is an open or closed entry
		final boolean origOpen = (isNew) ? false
				: entry.getAmountPaid() < entry.getAmountDue();
		// Get the title of the dialog
		String titleText = dialog.title.getText();
		// Initialize the number of borrowers at 0
		int numBorrowers = 0;
		// This is an ArrayList of actual borrower objects that will be
		// populated as you go through the rows of borrower people
		final ArrayList<Borrower> borrowers = new ArrayList<Borrower>();
		// This boolean will become true if there are any problems with the
		// input such as blank fields and non numerical inputs for the amount
		// field
		boolean hasProblems = false;
		// Initialize the total amount due at 0
		double totalAmount = 0.00;

		// Check that the title field is true
		if (titleText.equals("")) {
			hasProblems = true;
			dialog.title.addStyleName("errorField");
		} else {
			dialog.title.removeStyleName("errorField");
		}

		// Reset the total value to 0 if updating. You will add up the new total
		// amount that is paid based on the check marks
		if (!isNew) {
			entry.setAmountPaid(0);
		}
		// Go through the rows of borrower people
		for (BorrowerTextBox b : dialog.people) {
			// Name
			TextBox nameField = (TextBox) b.getName(), amountField = (TextBox) b
					.getAmount();
			String nameText = nameField.getText();
			// Amount String initialized as empty string because you have to
			// parse to a double
			String amountText = "";

			// Make sure all fields are filled================================
			if (nameField.getText().equals("")) {
				nameField.addStyleName("errorField");
				hasProblems = true;
			} else {
				nameField.removeStyleName("errorField");
				if (entry != null) {
					// Set the title to the entry
					entry.setTitle(titleText);
				}
			}
			if (amountField.getText().equals("")) {
				amountField.addStyleName("errorField");
				hasProblems = true;
			} else {
				amountField.removeStyleName("errorField");
			}

			// Make sure the amount is a valid number
			try {
				// Parse the string to a double
				double amountNum = Double.parseDouble(((TextBox) b.getAmount())
						.getText());
				// Round to 2 decimal places
				amountText = NumberFormat.getFormat("##0.00").format(amountNum);
				// Incremenet the total amount counter
				totalAmount += Double.parseDouble(amountText);
			} catch (NumberFormatException e) {
				hasProblems = true;
				amountField.addStyleName("errorField");
			}
			if (!hasProblems) {
				// Entry that was loaded from the database
				if (!isNew) {
					
					// Create the borrower object with the checkbox factored in
					// to determine whether that person has paid yet
					borrowers.add(new Borrower(nameText, amountText, b
							.getPaid().getValue()));
					// If this person has paid, add the value to the total
					// amount paid so far for the entry
					if (b.getPaid().getValue()) {
						double newVal = entry.getAmountPaid()
								+ Double.parseDouble(amountText);
						String numAmount = NumberFormat.getFormat("##0.00")
								.format(newVal);
						newVal = Double.parseDouble(numAmount);						
						entry.setAmountPaid(newVal);
					}
				}
				// New Entry, don't have to worry about if the person has paid
				// or not
				else {
					borrowers.add(new Borrower(nameText, amountText));
				}
				numBorrowers++;
			}
		}
		// Not enough borrowers (needs at least one)
		if (numBorrowers == 0) {
			return;
		}
		// Set the total amount paid to the entry
		if (entry != null) {
			entry.setAmountDue(totalAmount);
		}

		// If there are any problems, you have no need for the borrowers
		// ArrayList
		if (hasProblems) {
			borrowers.clear();
		}
		// No problems
		else {
			// New Entry. Adds this new entry to the database as well as to the
			// table as an open entry.
			if (isNew) {
				if (manageEntrySvc == null) {
					manageEntrySvc = GWT.create(EntryService.class);
				}
				AsyncCallback<Entry> entryActionCallback = new AsyncCallback<Entry>() {
					public void onFailure(Throwable caught) {
						System.out.println(caught);
					}

					public void onSuccess(Entry result) {
						dialog.parent.addEntry(result, true);
					}
				};
				manageEntrySvc.addEntry(titleText, dialog.parent.getUser()
						.getId(), totalAmount, borrowers, entryActionCallback);
			}
			// Updating an entry
			else {
				if (manageEntrySvc == null) {
					manageEntrySvc = GWT.create(EntryService.class);
				}
				AsyncCallback entryActionCallback = new AsyncCallback() {
					public void onFailure(Throwable caught) {
						System.out.println(caught);
					}

					public void copyBorrowerstoEntry(Entry entry,
							List<Borrower> borrowers) {
						// Make a deep copy of the elements since you will be
						// clearng this ArrayList soon
						ArrayList<Borrower> tempBorrowers = new ArrayList<Borrower>();
						for (Borrower b : borrowers) {
							tempBorrowers.add(new Borrower(b.getNameString(), b
									.getAmountString(), b.isPaid()));
						}
						// associate the arraylist with the entry at index
						entry.setBorrowers(tempBorrowers);
					}

					@Override
					public void onSuccess(Object result) {
						// Copy the new borrowers over to the entry
						copyBorrowerstoEntry(entry, borrowers);
						// Determine whether this is an open or closed entry
						boolean open = entry.getAmountPaid() < entry
								.getAmountDue();
						FlexTable table = (open) ? dialog.parent.openIOUsTable
								: dialog.parent.closedIOUsTable;
						ArrayList<Entry> entries = (origOpen) ? dialog.parent.openEntries
								: dialog.parent.closedEntries;
						int row = entries.indexOf(entry);
						int tableRow = row + 1;
						// Only change if it changes table from open <=> closed
						if (origOpen != open) {
							dialog.parent.removeEntry(origOpen, tableRow);
							dialog.parent.addEntry(entry, open);
						}
						// Update the values
						else {
							table.setText(tableRow, 0, entry.getTitle());
							table.setText(tableRow, 2, entry.getAmountPaid()
									+ "/" + entry.getAmountDue());
							table.setText(tableRow, 3, entry.getBorrowers()
									.size() + " people");
						}
						dialog.hide();
					}
				};
				manageEntrySvc.updateEntry(entry, borrowers,
						entryActionCallback);
			}
		}

	}

}
