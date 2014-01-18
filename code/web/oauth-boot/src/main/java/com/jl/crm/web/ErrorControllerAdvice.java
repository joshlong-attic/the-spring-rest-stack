package com.jl.crm.web;


import com.jl.crm.services.*;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
