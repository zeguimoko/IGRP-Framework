package cv.nosi.core.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import cv.nosi.core.validator.constraints.Future;
import cv.nosi.core.webapp.util.Core;

/**
 * emerson
 * 29/07/2019
 */
public class FutureValidator implements ConstraintValidator<Future, String>{

	private String currentDate;
	@Override
	public void initialize(Future constraintAnnotation) {
		this.currentDate = Core.getCurrentDate();
	}
	
	
	@Override
	public boolean isValid(String dateValue, ConstraintValidatorContext context) {	
		if(Core.isNotNull(dateValue)) {
			return Validation.validateFutureDate(this.currentDate,dateValue);
		}
		return true;
	}

}
