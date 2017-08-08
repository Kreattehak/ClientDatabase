package com.company.util;

import com.company.model.Address;
import com.company.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;

@Component
@Profile("!test")
public class SpringAndHibernateValidator implements Validator {

    private javax.validation.Validator beanValidator;

    private Set<Validator> springValidators;

    @Autowired
    public SpringAndHibernateValidator(javax.validation.Validator beanValidator) {
        this.springValidators = new HashSet<>();
        this.beanValidator = beanValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Address.class.isAssignableFrom(clazz) || Client.class.isAssignableFrom(clazz);
    }


    @Override
    public void validate(Object o, Errors errors) {
        Set<ConstraintViolation<Object>> constraintViolations =
                beanValidator.validate(o);

        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
            errors.rejectValue(constraintViolation.getPropertyPath().toString(), "error",
                    constraintViolation.getMessage());
        }

        for (Validator validator : springValidators) {
            validator.validate(o, errors);
        }
    }

    public void setSpringValidators(Set<Validator> springValidators) {
        this.springValidators = springValidators;
    }
}
