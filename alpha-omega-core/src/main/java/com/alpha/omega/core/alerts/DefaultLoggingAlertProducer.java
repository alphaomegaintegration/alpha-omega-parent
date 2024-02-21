package com.alpha.omega.core.alerts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class DefaultLoggingAlertProducer implements AlertProducer {
    Logger logger = LogManager.getLogger(DefaultLoggingAlertProducer.class);

    public Optional<Alert> sendAlert(Supplier<Optional<Alert>> alertSupplier) {
        Optional<Alert> optionalAlert = alertSupplier.get();
        optionalAlert.ifPresent(alert -> {

            logger.error(alert);
        });
        return optionalAlert;
    }
}
