package com.jchien.iou.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.jchien.iou.shared.Entry;
import com.jchien.iou.shared.User;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IOU implements EntryPoint {

	User user;

	// Signed Out
	VerticalPanel signedOut = new VerticalPanel(), login = new VerticalPanel(),
			createaccount = new VerticalPanel();
	Button loginButton = new Button("Log In"),
			createAccountButton = new Button("Create Account");
	TextBox loginUsername = new TextBox(), createUsername = new TextBox(),
			createName = new TextBox();
	PasswordTextBox loginPassword = new PasswordTextBox(),
			createPassword = new PasswordTextBox(),
			confirmPassword = new PasswordTextBox();

	LoginHandler loginHandler = new LoginHandler(this);
	CreateAccountHandler createAccountHandler = new CreateAccountHandler(this);

	// Signed In
	VerticalPanel signedIn = new VerticalPanel();
	HorizontalPanel tables = new HorizontalPanel();
	VerticalPanel openIOUs = new VerticalPanel(),
			closedIOUs = new VerticalPanel();

	FlexTable openIOUsTable = new FlexTable(),
			closedIOUsTable = new FlexTable();
	Button logout = new Button("Log Out"), addEntry = new Button("Add IOU");
	Label open = new Label("Open IOUs"), closed = new Label("Closed IOUs");
	Label loginEmptyError = new Label("Please make sure all fields are filled"),
			loginIncorrect = new Label(
					"Login unsuccessful. Username and password don't match."),
			createEmptyError = new Label(
					"Please make sure all fields are filled"),

			usernameTaken = new Label("Username is taken"),
			passwordTooShort = new Label(
					"Password must be at least 8 characters long"),
			passwordsNoMatch = new Label("Passwords do not match");
	EntryDialog addDialog = new EntryDialog(this, true);
	EntryDialog editDialog = new EntryDialog(this, false);
	ArrayList<Entry> openEntries = new ArrayList<Entry>();
	ArrayList<Entry> closedEntries = new ArrayList<Entry>();
	Label name = new Label();

	EntryServiceAsync entrySvc = GWT.create(EntryService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		loginEmptyError.addStyleName("errorMessage");
		loginIncorrect.addStyleName("errorMessage");
		createEmptyError.addStyleName("errorMessage");
		usernameTaken.addStyleName("errorMessage");
		passwordTooShort.addStyleName("errorMessage");
		passwordsNoMatch.addStyleName("errorMessage");

		loginEmptyError.setVisible(false);
		loginIncorrect.setVisible(false);
		createEmptyError.setVisible(false);
		usernameTaken.setVisible(false);
		passwordTooShort.setVisible(false);
		passwordsNoMatch.setVisible(false);

		loginButton.addClickHandler(loginHandler);
		loginButton.addKeyPressHandler(loginHandler);

		createAccountButton.addClickHandler(createAccountHandler);
		createAccountButton.addKeyPressHandler(createAccountHandler);

		/** Signed Out **/
		login.add(new Label("Username"));
		login.add(loginUsername);
		login.add(new Label("Password"));
		login.add(loginPassword);
		login.add(loginEmptyError);
		login.add(loginIncorrect);
		login.add(loginButton);

		createaccount.add(new Label("Username"));
		createaccount.add(createUsername);
		createaccount.add(new Label("Name"));
		createaccount.add(createName);
		createaccount.add(new Label("Password"));
		createaccount.add(createPassword);
		createaccount.add(new Label("Confirm Password"));
		createaccount.add(confirmPassword);
		createaccount.add(createEmptyError);
		createaccount.add(usernameTaken);
		createaccount.add(passwordTooShort);
		createaccount.add(passwordsNoMatch);
		createaccount.add(createAccountButton);

		signedOut.add(login);
		signedOut.add(createaccount);
		signedOut.setVisible(true);
		RootPanel.get("signedOut").add(signedOut);

		/** Signed In **/
		resetTables();
		openIOUs.add(open);
		openIOUs.add(openIOUsTable);
		closedIOUs.add(closed);
		closedIOUs.add(closedIOUsTable);

		// Add these tables to the tables HorizontalPanel
		tables.add(openIOUs);
		tables.add(closedIOUs);

		// Add listener for adding entries to show the dialog popup
		addEntry.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				addDialog.center();

			}

		});

		// Add listener for logging out
		logout.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setUser(null);
				switchView(false);
			}
		});
		HorizontalPanel commands = new HorizontalPanel();
		signedIn.add(name);
		commands.add(addEntry);
		commands.add(logout);
		signedIn.add(commands);
		signedIn.add(tables);
		signedIn.setVisible(false);
		RootPanel.get("signedIn").add(signedIn);
	}

	private void resetTables() {
		openIOUsTable.removeAllRows();
		closedIOUsTable.removeAllRows();
		// Set the column titles of the openIOUs table
		openIOUsTable.setText(0, 0, "Title");
		openIOUsTable.setText(0, 1, "Date Issued");
		openIOUsTable.setText(0, 2, "Amount Paid/Total Amount Due");
		openIOUsTable.setText(0, 3, "Number of People in Debt");
		openIOUsTable.setText(0, 4, "Edit");
		openIOUsTable.setText(0, 5, "Remove");
		openIOUsTable.setCellPadding(6);
		openIOUsTable.addStyleName("iouTable");
		openIOUsTable.getRowFormatter().addStyleName(0, "tableHeader");

		// Set the column titles of the closedIOUs table
		closedIOUsTable.setText(0, 0, "Title");
		closedIOUsTable.setText(0, 1, "Date Issued");
		closedIOUsTable.setText(0, 2, "Amount Paid/Total Amount Due");
		closedIOUsTable.setText(0, 3, "Number of People in Debt");
		closedIOUsTable.setText(0, 4, "Edit");
		closedIOUsTable.setText(0, 5, "Remove");
		closedIOUsTable.setCellPadding(6);
		closedIOUsTable.addStyleName("iouTable");
		closedIOUsTable.getRowFormatter().addStyleName(0, "tableHeader");
	}

	private void loadEntries() {
		if (entrySvc == null) {
			entrySvc = GWT.create(EntryService.class);
		}
		AsyncCallback<List<Entry>> loadCallback = new AsyncCallback<List<Entry>>() {

			public void onFailure(Throwable caught) {
				System.out.println(caught);
			}

			@Override
			public void onSuccess(List<Entry> result) {
				for (Entry e : result) {
					boolean open = e.getAmountPaid() < e.getAmountDue();
					addEntry(e, open);
				}
			}
		};
		entrySvc.getAllEntries(user.getId(), loadCallback);
	}

	public void addEntry(final Entry e, boolean open) {
		// Determine which table and array list, open or closed to add this
		// entry to
		final FlexTable table = (open) ? openIOUsTable : closedIOUsTable;
		final ArrayList<Entry> entryList = (open) ? openEntries : closedEntries;
		final int row = table.getRowCount();
		table.setText(row, 0, e.getTitle());
		table.setText(row, 1, e.getDate());
		table.setText(row, 2, e.getAmountPaid() + "/" + e.getAmountDue());
		table.setText(row, 3, e.getNumBorrowers() + " people");
		Button edit = new Button("Edit"), remove = new Button("Remove");
		edit.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// Populate the dialog
				editDialog.loadData(e);
				// Show the dialog
				editDialog.center();
			}

		});
		remove.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (entrySvc == null) {
					entrySvc = GWT.create(EntryService.class);
				}
				AsyncCallback removeCallback = new AsyncCallback() {

					public void onFailure(Throwable caught) {
						System.out.println(caught);
					}

					@Override
					public void onSuccess(Object result) {
						int index = entryList.indexOf(e);
						entryList.remove(index);
						table.removeRow(index + 1);
					}
				};

				int entryID = e.getId();
				entrySvc.removeEntry(entryID, removeCallback);

			}

		});
		table.setWidget(row, 4, edit);
		table.setWidget(row, 5, remove);
		entryList.add(e);
	}

	/** Remove an entry from the table and ArrayList **/
	public void removeEntry(boolean open, int row) {
		// Determine which table to remove this from
		FlexTable table = (open) ? openIOUsTable : closedIOUsTable;
		// Determine which ArrayList to remove this from
		ArrayList<Entry> entryList = (open) ? openEntries : closedEntries;
		// Remove from table
		table.removeRow(row);
		// Remove from ArrayList
		entryList.remove(row - 1);
	}

	public void setUser(User u) {
		user = u;
		if (u != null) {
			name.setText(u.getName());
		} else {
			name.setText("");
		}
	}

	public User getUser() {
		return user;
	}

	public void clearFields() {
		loginUsername.setText("");
		createUsername.setText("");
		createName.setText("");
		loginPassword.setText("");
		createPassword.setText("");
		confirmPassword.setText("");
	}

	public void switchView(boolean loggedIn) {

		if (loggedIn) {
			signedOut.setVisible(false);
			signedIn.setVisible(true);
			loadEntries();
			clearFields();
		} else {
			resetTables();
			signedIn.setVisible(false);
			signedOut.setVisible(true);
		}
	}
}
