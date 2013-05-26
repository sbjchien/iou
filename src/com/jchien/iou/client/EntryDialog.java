package com.jchien.iou.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.jchien.iou.shared.Borrower;
import com.jchien.iou.shared.Entry;
import com.jchien.iou.shared.User;

public class EntryDialog extends DialogBox {
	VerticalPanel panel = new VerticalPanel();
	// ArrayList of Borrower Text Boxes (and check boxes for paid if updating)
	ArrayList<BorrowerTextBox> people = new ArrayList<BorrowerTextBox>();
	// Entry associated with this dialog
	Entry entry;
	// Table to hold a list of borrowers
	FlexTable peopleTable = new FlexTable();
	// The IOU object
	IOU parent;
	// Title Field
	final TextBox title = new TextBox();


	public EntryDialog(IOU iou, final boolean newEntry) {
		// Set the parent of this dialog
		parent = iou;
		// Set the heading dialog of this dialog
		setText("Add an IOU");
		// Enable animation for this dialog
		setAnimationEnabled(true);
		// Labels=====================================
		Label titleLabel = new Label("Title");
		Label amountLabel = new Label("Amount");

		// Buttons===================================
		Button addPerson = new Button("Add Person");
		Button save = new Button("Add IOU");
		if (!newEntry) {
			save = new Button("Save Changes");
		}
		Button close = new Button("Close");

		// Click Handlers==============================
		save.addClickHandler(new EntryHandler(this, newEntry));
		addPerson.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addBorrower(newEntry, "", "", false);
			}
		});
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EntryDialog.this.hide();
				reset(newEntry);

			}
		});
		// Set up table headers==========================
		peopleTable.setText(0, 0, "Name");
		peopleTable.setText(0, 1, "Amount");
		if (newEntry) {
			peopleTable.setText(0, 2, "Remove");
		} else {
			peopleTable.setText(0, 2, "Paid");
			peopleTable.setText(0, 3, "Remove");
		}

		// Horizontal Panel to add buttons================
		HorizontalPanel addFinished = new HorizontalPanel();
		addFinished.add(addPerson);
		addFinished.add(save);
		addFinished.add(close);

		// Adding stuff to vertical panel
		panel.add(titleLabel);
		panel.add(title);
		panel.add(amountLabel);
		panel.add(addFinished);
		panel.add(peopleTable);

		// Add a blank person if this is a new borrower
		if (newEntry) {
			addBorrower(newEntry, "", "", false);
		}

		// Set the widget to be the horizontal panel
		setWidget(panel);
	}

	/**
	 * Loads data for this dialog and sets the entry. Called by IOU
	 * 
	 * @param e
	 * @param origRow
	 */
	public void loadData(Entry e) {
		title.setText(e.getTitle());
		clearBorrowers();
		for (Borrower person : e.getBorrowers()) {
			addBorrower(false, (String) person.getNameString(),
					(String) person.getAmountString(), person.isPaid());
		}
		entry = e;
	}


	/**
	 * Returns the entry associated with this dialog
	 * 
	 * @return
	 */
	public Entry getEntry() {
		return entry;
	}

	/**
	 * This resets everything in this dialog
	 * 
	 * @param newEntry
	 */
	private void reset(boolean newEntry) {
		// Clears the title field
		title.setText("");
		clearBorrowers();
		// Add one new field for borrower if a new entry
		if (newEntry) {
			addBorrower(newEntry, "", "", false);
		}
	}

	private void clearBorrowers() {
		// Remove all borrowers from the dialog
		int numPeope = people.size();
		// Remove the first one as many times as there are people fields in the
		// ArrayList
		for (int i = 0; i < numPeope; i++) {
			// Remove the first text field
			peopleTable.removeRow(1);
			// Remove the first element from the ArrayList
			people.remove(0);
		}
	}

	/** Adds a new borrower both to the table and to the ArrayList **/
	private void addBorrower(boolean newEntry, String name, String amount,
			boolean hasPaid) {
		// Make the appropriate Text boxes and labels
		TextBox nameBox = new TextBox();
		TextBox amountBox = new TextBox();
		// Set the values of the text boxes
		nameBox.setText(name);
		amountBox.setText(amount);
		int row = peopleTable.getRowCount();
		peopleTable.setWidget(row, 0, nameBox);
		peopleTable.setWidget(row, 1, amountBox);
		CheckBox paid = null;
		// Add the remove button and attach the click handler to remove the row
		// from the list of text boxes and checkboxes if applicable
		Button remove = new Button("Remove");

		// New Entry, just have a remove button
		if (newEntry) {
			peopleTable.setWidget(row, 2, remove);
		}
		// Not a new entry, have bot ha checkbox and a remove button
		else {
			paid = new CheckBox();
			if (hasPaid) {
				paid.setValue(true);
			}
			peopleTable.setWidget(row, 2, paid);
			peopleTable.setWidget(row, 3, remove);
		}
		// Create the BorrowerTextBox object with the text boxes and the check
		// box if applicable
		final BorrowerTextBox borrower = new BorrowerTextBox(nameBox,
				amountBox, paid);
		// Add to the people arraylist
		people.add(borrower);

		// Click handler for removal from both the ArrayList and the table
		remove.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (people.size() == 1) {
					return;
				}
				int personIndex = people.indexOf(borrower);
				peopleTable.removeRow(personIndex + 1);
				people.remove(personIndex);
			}

		});

	}
}
