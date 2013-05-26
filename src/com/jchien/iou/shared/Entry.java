package com.jchien.iou.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Entry implements Serializable, IsSerializable {
	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	protected String title, date;
	protected double amountPaid, amountDue;
	protected int id, issuerID;
	protected ArrayList<Borrower> borrowers = new ArrayList<Borrower>();

	public Entry() {

	}

	public Entry(String title, String date, double amountPaid,
			double amountDue, int id, int issuerID) {
		this.title = title;
		this.date = date;
		this.amountPaid = amountPaid;
		this.amountDue = amountDue;
		this.id = id;
		this.issuerID = issuerID;
	}

	public Entry(String title, String date, double amountPaid,
			double amountDue, int id, int issuerID,
			ArrayList<Borrower> borrowers) {
		this.title = title;
		this.date = date;
		this.amountPaid = amountPaid;
		this.amountDue = amountDue;
		this.id = id;
		this.issuerID = issuerID;
		this.borrowers = borrowers;
	}

	public String toString() {
		String result = title + ", " + date + ", " + amountPaid + ", "
				+ amountDue + ", " + id + ", " + issuerID + ", \n Borrowers:";
		for (Borrower borrower : borrowers) {
			result += borrower.getNameString() + " => ";
		}
		return result;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public double getAmountDue() {
		return amountDue;
	}

	public void setAmountDue(double amountDue) {
		this.amountDue = amountDue;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIssuerID() {
		return issuerID;
	}

	public void setIssuerID(int issuerID) {
		this.issuerID = issuerID;
	}

	public ArrayList<Borrower> getBorrowers() {
		return borrowers;
	}

	public void setBorrowers(List<Borrower> borrowers) {
		this.borrowers = (ArrayList<Borrower>) borrowers;
	}

	public int getNumBorrowers() {
		return borrowers.size();
	}

}
