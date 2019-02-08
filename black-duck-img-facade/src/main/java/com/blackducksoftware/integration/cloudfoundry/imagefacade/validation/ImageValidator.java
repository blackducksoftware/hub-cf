/*
 * Copyright (C) 2018 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.cloudfoundry.imagefacade.validation;

import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.blackducksoftware.integration.cloudfoundry.imagefacade.api.Image;

/**
 * @author fisherj
 *
 */
public final class ImageValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Image.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pullSpec", "errors.pullSpec.emptyOrWhitespace");

        Image image = (Image) target;

        if (!validSpecPattern(image.getPullSpec())) {
            errors.rejectValue("pullSpec", "errors.pullSpec.incorrectFormat");
        }
    }

    private boolean validSpecPattern(String spec) {
        Pattern pattern = Pattern.compile("([\\p{XDigit}-]++)" + Image.DELIMITER + "([\\p{XDigit}-]++)");
        return pattern.matcher(spec).matches();
    }
}
