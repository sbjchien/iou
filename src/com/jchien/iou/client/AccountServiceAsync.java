package com.jchien.iou.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.jchien.iou.shared.User;

public interface AccountServiceAsync {
	public void login(String username, String password,
			AsyncCallback<User> callback);

	public void createAccount(String username, String password, String name,
			AsyncCallback<User> callback);
}
