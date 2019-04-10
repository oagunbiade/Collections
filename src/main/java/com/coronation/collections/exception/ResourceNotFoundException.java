package com.coronation.collections.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException {

	 private String resourceName;
	    private String fieldName;
	    private Object fieldValue;
	    
	    public ResourceNotFoundException( String resourceName, String fieldName, Object fieldValue) {
	        super();
	        this.resourceName = resourceName;
	        this.fieldName = fieldName;
	        this.fieldValue = fieldValue;
	    }

}
