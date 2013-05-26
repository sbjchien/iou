package com.jchien.iou.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.jchien.iou.shared.User;

@RemoteServiceRelativePath("account")
public interface AccountService extends RemoteService {
	public User login(String username, String password);

	public User createAccount(String username, String password, String name);

}
