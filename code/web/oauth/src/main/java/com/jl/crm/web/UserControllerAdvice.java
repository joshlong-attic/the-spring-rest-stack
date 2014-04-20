package com.jl.crm.web;

import com.jl.crm.services.exceptions.CustomerNotFoundException;
import com.jl.crm.services.exceptions.UserNotFoundException;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
class UserControllerAdvice {

      MediaType vndErrorMediaType = MediaType.parseMediaType("application/vnd.error");

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<VndErrors> userNotFoundException(CustomerNotFoundException e) {
        return error(e, HttpStatus.NOT_FOUND, e.getCustomerId() + "");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<VndErrors> userNotFoundException(UserNotFoundException e) {
        return error(e, HttpStatus.NOT_FOUND, e.getUserId() + "");
    }

    protected <E extends Exception> ResponseEntity<VndErrors> error(E e,
                                                                    HttpStatus httpStatus,
                                                                    String logref) {

        String msg = StringUtils.hasText(
                e.getMessage()) ? e.getMessage() :
                e.getClass().getSimpleName();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( this.vndErrorMediaType);
        return new ResponseEntity<VndErrors>(
                new VndErrors(logref, msg), httpHeaders, httpStatus);
    }


}