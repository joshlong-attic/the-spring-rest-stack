package com.jl.crm.web;


import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.jl.crm.services.UserProfilePhotoReadException;
import com.jl.crm.services.UserProfilePhotoWriteException;

@ControllerAdvice
class ErrorControllerAdvice {

	@ExceptionHandler
	HttpEntity<VndErrors> userProfilePhotoReadException(UserProfilePhotoReadException e) {
		return doHandleException(e, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler
	HttpEntity<VndErrors> userProfilePhotoWriteException(UserProfilePhotoWriteException e) {
		return doHandleException(e, HttpStatus.NOT_FOUND);
	}

    protected HttpEntity<VndErrors> doHandleException(Exception e, HttpStatus httpStatus) {
        VndErrors vndErrors = new VndErrors(new VndErrors.VndError(e.getClass().getName(), e.getMessage()));
        return new ResponseEntity<VndErrors>(vndErrors, httpStatus);
    }

}
