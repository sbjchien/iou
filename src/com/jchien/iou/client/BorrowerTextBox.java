package com.jchien.iou.client;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;

public class BorrowerTextBox {

	TextBox name;
	TextBox amount;

	CheckBox paid;

	public BorrowerTextBox(TextBox name, TextBox amount, CheckBox paid) {
		this.name = name;
		this.amount = amount;
		this.paid = paid;
	}

	public TextBox getName() {
		return name;
	}

	public void setName(TextBox name) {
		this.name = name;
	}

	public TextBox getAmount() {
		return amount;
	}

	public void setAmount(TextBox amount) {
		this.amount = amount;
	}

	public CheckBox getPaid() {
		return paid;
	}

	public void setPaid(CheckBox paid) {
		this.paid = paid;
	}

}
