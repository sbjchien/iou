package com.jchien.iou.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.jchien.iou.shared.User;

public class CreateAccountHandler implements ClickHandler, KeyPressHandler {
	private IOU iou;
	private AccountServiceAsync createAccountSvc = GWT
			.create(AccountService.class);

	public CreateAccountHandler(IOU iou) {
		this.iou = iou;
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if (event.getCharCode() != KeyCodes.KEY_ENTER) {
			return;
		}
		attemptAccountCreation();
	}

	@Override
	public void onClick(ClickEvent event) {
		attemptAccountCreation();
	}

	private void attemptAccountCreation() {
		String username = iou.createUsername.getText();
		String name = iou.createName.getText();
		String password = iou.createPassword.getText();
		String confirmPassword = iou.confirmPassword.getText();
		boolean notFilled = false;
		// Make sure all fields are filled
		if (username.equals("")) {
			iou.createUsername.addStyleName("errorField");
			notFilled = true;
		} else {
			iou.createUsername.removeStyleName("errorField");
		}
		if (name.equals("")) {
			iou.createName.addStyleName("errorField");
			notFilled = true;
		} else {
			iou.createName.removeStyleName("errorField");
		}
		if (password.equals("")) {
			iou.createPassword.addStyleName("errorField");
			notFilled = true;
		} else {
			iou.createPassword.removeStyleName("errorField");
		}
		if (confirmPassword.equals("")) {
			iou.confirmPassword.addStyleName("errorField");
			notFilled = true;
		} else {
			iou.confirmPassword.removeStyleName("errorField");
		}
		if (notFilled) {
			iou.usernameTaken.setVisible(false);
			iou.passwordTooShort.setVisible(false);
			iou.passwordsNoMatch.setVisible(false);
			iou.createEmptyError.setVisible(true);
			return;
		}
		// Check that password is more than 8 characters and matches the
		// confirmed password
		if (password.length() < 8) {
			iou.usernameTaken.setVisible(false);
			iou.passwordTooShort.setVisible(true);
			iou.passwordsNoMatch.setVisible(false);
			iou.createEmptyError.setVisible(false);
			iou.createPassword.addStyleName("errorField");
			return;
		} else if (!password.equals(confirmPassword)) {
			iou.usernameTaken.setVisible(false);
			iou.passwordTooShort.setVisible(false);
			iou.passwordsNoMatch.setVisible(true);
			iou.createEmptyError.setVisible(false);
			iou.createPassword.addStyleName("errorField");
			return;
		}

		iou.createPassword.removeStyleName("errorField");

		iou.usernameTaken.setVisible(false);
		iou.passwordTooShort.setVisible(false);
		iou.passwordsNoMatch.setVisible(false);
		iou.createEmptyError.setVisible(false);

		if (createAccountSvc == null) {
			createAccountSvc = GWT.create(AccountService.class);
		}
		AsyncCallback<User> accountCreateCallback = new AsyncCallback<User>() {
			public void onFailure(Throwable caught) {
				// TODO: Do something with errors.
			}

			public void onSuccess(User result) {
				// Username Already Taken
				if (result == null) {
					iou.usernameTaken.setVisible(true);
					iou.passwordTooShort.setVisible(false);
					iou.passwordsNoMatch.setVisible(false);
					iou.createEmptyError.setVisible(false);
				}
				// Account creation was successful
				else {
					iou.setUser(result);
					iou.switchView(true);
				}
			}
		};
		createAccountSvc.createAccount(username, password, name,
				accountCreateCallback);

	}
}