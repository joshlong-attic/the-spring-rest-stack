package com.jl.crm.web;


import com.jl.crm.services.UserProfilePhotoReadException;
import com.jl.crm.services.UserProfilePhotoWriteException;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Component that sits in the middle of all the processing and handles custom errors.
 * <p/>
 * Things this *doesn't* do: - handle exceptions from Spring Data REST itself. - handle exceptions from Spring Data REST
 * validators.
 *
 * @author Josh Long
 */
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

    private HttpEntity<VndErrors> doHandleException(Exception e, HttpStatus httpStatus) {
        VndErrors vndErrors = new VndErrors(new VndErrors.VndError(e.getClass().getName(), e.getMessage()));
        return new ResponseEntity<VndErrors>(vndErrors, httpStatus);
    }

}
