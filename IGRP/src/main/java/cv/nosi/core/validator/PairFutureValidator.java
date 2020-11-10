package cv.nosi.core.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import cv.nosi.core.gui.components.IGRPSeparatorList.Pair;
import cv.nosi.core.validator.constraints.PairFuture;
import cv.nosi.core.webapp.util.Core;

/**
 * emerson
 * 31/07/2019
 */
public class PairFutureValidator implements ConstraintValidator<PairFuture, Pair>{

	private String currentDate;
	@Override
	public void initialize(PairFuture constraintAnnotation) {
		this.currentDate = Core.getCurrentDate();
	}
	
	
	@Override
	public boolean isValid(Pair pair, ConstraintValidatorContext context) {		
		if(pair!=null && Core.isNotNull(pair.getKey())) {
			return  Validation.validateFutureDate(this.currentDate,pair.getKey());
		}
		return true;
	}

}

