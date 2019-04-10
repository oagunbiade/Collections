package com.coronation.collections.forms;

import com.coronation.collections.domain.User;

public class UserForm {

	private User user; 
	
	private UserProfile profile;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserProfile getProfile() {
		return profile;
	}

	public void setProfile(UserProfile profile) {
		this.profile = profile;
	}
	
}
