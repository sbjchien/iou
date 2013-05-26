package com.jchien.iou.server;

import com.jchien.iou.shared.User;

public interface AccountMapper {

	User getUser(String username);

	int createAccount(User user);
}
