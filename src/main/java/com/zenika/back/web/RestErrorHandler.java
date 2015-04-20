package com.zenika.back.web;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ControllerAdvice
public class RestErrorHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestErrorHandler.class);

    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
	this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
	    MethodArgumentNotValidException ex, HttpHeaders headers,
	    HttpStatus status, WebRequest request) {
	
	ObjectMapper mapper = new ObjectMapper();
	
	try {
	    return new ResponseEntity(mapper.writeValueAsString(this.processValidationError(ex).getFieldErrors()), headers, status);
	} catch (JsonProcessingException e) {
	    logger.error(e.getMessage());
	}
	
	return new ResponseEntity("An error occured.", headers, HttpStatus.OK);
    }

    private ValidationError processValidationError(
	    MethodArgumentNotValidException ex) {
	BindingResult result = ex.getBindingResult();
	List<FieldError> fieldErrors = result.getFieldErrors();

	return processFieldErrors(fieldErrors);
    }

    private ValidationError processFieldErrors(List<FieldError> fieldErrors) {
	ValidationError dto = new ValidationError();

	for (FieldError fieldError : fieldErrors) {
	    String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
	    dto.addFieldError(fieldError.getField(), localizedErrorMessage);
	}

	return dto;
    }

    private String resolveLocalizedErrorMessage(FieldError fieldError) {
	Locale currentLocale = LocaleContextHolder.getLocale();
	String localizedErrorMessage = messageSource.getMessage(fieldError,
		currentLocale);

	// If the message was not found, return the most accurate field error
	// code instead.
	// You can remove this check if you prefer to get the default error
	// message.
	if (localizedErrorMessage.equals(fieldError.getDefaultMessage())) {
	    String[] fieldErrorCodes = fieldError.getCodes();
	    localizedErrorMessage = fieldErrorCodes[0];
	}

	return localizedErrorMessage;
    }

}
