package com.alpha.omega.core.alerts;

import java.util.Optional;
import java.util.function.Supplier;

public interface AlertProducer {
    Optional<Alert> sendAlert(Supplier<Optional<Alert>> alertSupplier);
	default Optional<Alert> sendSuppliedAlert(Supplier<Alert> alertSupplier){
		return sendAlert(() -> Optional.ofNullable(alertSupplier.get()));
	}
}
