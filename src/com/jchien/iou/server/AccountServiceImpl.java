package com.jchien.iou.server;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.jchien.iou.client.AccountService;
import com.jchien.iou.shared.User;

@Service
public class AccountServiceImpl extends SpringBeanAutowiringSupport implements
		InitializingBean, DisposableBean, AccountService {

	@Autowired
	AccountMapper accountMapper;

	public User login(String username, String password) {
		User account = accountMapper.getUser(username);
		if (!password.equals(account.getPassword())) {
			return null;
		}
		return account;
	}

	@Override
	public User createAccount(String username, String password, String name) {
		User result = accountMapper.getUser(username);
		if (result != null) {
			return null;
		}
		User user = new User(username, password, name);
		accountMapper.createAccount(user);
		user.setId(accountMapper.getUser(username).getId());
		return user;
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

}
