package com.coronation.collections.util;

import com.coronation.collections.domain.User;
import com.coronation.collections.security.ProfileDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserUtils {

	public static User getLoggedInUser(){
		Authentication authentication =	SecurityContextHolder.getContext().getAuthentication();
		return ((ProfileDetails)authentication.getPrincipal()).toUser();
	}
	
}
