package com.pwc.base.alerts;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class CompositeListAlertProducer implements AlertProducer{

	DefaultLoggingAlertProducer defaultLoggingAlertProducer;
	List<AlertProducer> alertProducers = new ArrayList<AlertProducer>();

	public CompositeListAlertProducer(DefaultLoggingAlertProducer defaultLoggingAlertProducer, List<AlertProducer> alertProducers) {
		Assert.notNull(alertProducers, "Alert producers cannot be null");
		Assert.notEmpty(alertProducers, "Alert producers cannot be empty");
		Assert.notNull(defaultLoggingAlertProducer, "DefaultLoggingAlertProducer cannot be null");
		this.defaultLoggingAlertProducer = defaultLoggingAlertProducer;
		this.alertProducers = alertProducers;
		Boolean hasDefault = alertProducers.stream()
				.anyMatch(alertProducer -> alertProducer.getClass().equals(DefaultLoggingAlertProducer.class));
		if (!hasDefault){
			this.alertProducers.add(defaultLoggingAlertProducer);
		}

	}

	@Override
	public Optional<Alert> sendAlert(final Supplier<Optional<Alert>> alertSupplier) {

		Optional<Alert> optionalAlert = alertSupplier.get();
		alertProducers.stream().forEach(alertProducer ->  sendProducerAlert(alertProducer, alertSupplier));
		return optionalAlert;
	}

	void sendProducerAlert(AlertProducer alertProducer, Supplier<Optional<Alert>> alertSupplier){
		try{
			alertProducer.sendAlert(alertSupplier);
		} catch (Exception e){
			final Alert alert = Alert.newBuilder()
					.setAlertName(alertProducer.getClass().getName())
					.setAlertLevel(AlertPriority.HIGH.getPriority())
					.setAlertPriority(AlertPriority.HIGH)
					.setMessage(String.format("Alert producer %s failed with message %s", alertProducer.getClass().getName(),
							e.getMessage()))
					.build();
			defaultLoggingAlertProducer.sendAlert(() -> Optional.of(alert));
		}
	}

}
