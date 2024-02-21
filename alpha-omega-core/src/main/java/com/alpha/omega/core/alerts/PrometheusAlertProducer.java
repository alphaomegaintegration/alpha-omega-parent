package com.alpha.omega.core.alerts;

import java.util.Optional;
import java.util.function.Supplier;

public class PrometheusAlertProducer implements AlertProducer{

	//TODO add implentation for Prometheus
	@Override
	public Optional<Alert> sendAlert(Supplier<Optional<Alert>> alertSupplier) {
		return Optional.empty();
	}
}
