package com.jchien.iou.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.jchien.iou.shared.User;

public class LoginHandler implements ClickHandler, KeyPressHandler {
	private IOU iou;
	private AccountServiceAsync loginSvc = GWT.create(AccountService.class);

	public LoginHandler(IOU iou) {
		this.iou = iou;
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if (event.getCharCode() != KeyCodes.KEY_ENTER) {
			return;
		}
		attemptLogin();
	}

	@Override
	public void onClick(ClickEvent event) {
		attemptLogin();
	}

	private void attemptLogin() {
		String username = iou.loginUsername.getText();
		String password = iou.loginPassword.getText();
		boolean hasError = false;
		if (username.equals("")) {
			iou.loginUsername.addStyleName("errorField");
			hasError = true;
		} else {
			iou.loginUsername.removeStyleName("errorField");
		}
		if (password.equals("")) {
			iou.loginPassword.addStyleName("errorField");
			hasError = true;
		} else {
			iou.loginPassword.removeStyleName("errorField");
		}
		if (hasError) {
			iou.loginIncorrect.setVisible(false);
			iou.loginEmptyError.setVisible(true);
			return;
		}
		iou.loginIncorrect.setVisible(false);
		iou.loginEmptyError.setVisible(false);
		if (loginSvc == null) {
			loginSvc = GWT.create(AccountService.class);
		}
		AsyncCallback<User> loginCallback = new AsyncCallback<User>() {
			public void onFailure(Throwable caught) {
				// TODO: Do something with errors.
			}

			public void onSuccess(User result) {
				// Login Successful
				if (result != null) {
					iou.setUser(result);
					iou.switchView(true);
					iou.setUser(result);
				} else {
					// Login Unsuccessful
					iou.loginEmptyError.setVisible(false);
					iou.loginIncorrect.setVisible(true);
				}
			}
		};
		loginSvc.login(username, password, loginCallback);
	}

}
