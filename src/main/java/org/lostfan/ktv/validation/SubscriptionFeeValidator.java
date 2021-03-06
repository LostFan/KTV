package org.lostfan.ktv.validation;

import org.lostfan.ktv.domain.RenderedService;

public class SubscriptionFeeValidator implements Validator<RenderedService> {

    @Override
    public ValidationResult validate(RenderedService entity, ValidationResult result) {
        if (entity.getDate() == null) {
            result.addError("errors.empty", "renderedService.date");
        }

        if (entity.getPrice() == null) {
            result.addError("errors.empty", "renderedService.price");
        }

        if (entity.getSubscriberAccount() == null) {
            result.addError("errors.empty", "subscriber");
        }

        return result;
    }
}
