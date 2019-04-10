package com.coronation.collections.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Created by Toyin on 2/1/19.
 */
public class PageUtil {
    public static Pageable createPageRequest(int pageNumber, Sort sort) {
        return createPageRequest(pageNumber, 10, sort);
    }

    public static Pageable createPageRequest(int pageNumber, int size, Sort sort) {
        return PageRequest.of(pageNumber, size, sort);
    }
}
