package com.coronation.collections.repositories;

import com.coronation.collections.domain.AuthenticationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Toyin on 4/8/19.
 */
public interface AuthenticationDetailRepository extends JpaRepository<AuthenticationDetail, Long> {
}
