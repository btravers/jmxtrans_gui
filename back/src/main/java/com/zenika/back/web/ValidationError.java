package com.zenika.back.web;

import java.util.ArrayList;
import java.util.List;

public class ValidationError {

    private List<FieldError> fieldErrors = new ArrayList<>();

    public void addFieldError(String path, String message) {
	FieldError error = new FieldError(path, message);
	fieldErrors.add(error);
    }

    public List<FieldError> getFieldErrors() {
	return fieldErrors;
    }

    public void setFieldErrors(List<FieldError> fieldErrors) {
	this.fieldErrors = fieldErrors;
    }

}
