package com.jchien.iou.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.TextBox;

public class Borrower implements Serializable, IsSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String nameString;
	protected String amountString;
	protected boolean paid;

	public Borrower() {

	}

	public Borrower(String name, String amount) {
		nameString = name;
		amountString = amount;
	}

	public Borrower(String name, String amount, boolean paid) {
		nameString = name;
		amountString = amount;
		this.paid = paid;
	}

	public String getNameString() {
		return nameString;
	}

	public void setNameString(String nameString) {
		this.nameString = nameString;
	}

	public String getAmountString() {
		return amountString;
	}

	public void setAmountString(String amountString) {
		this.amountString = amountString;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

}
