package com.alpha.omega.core.alerts;

import io.micrometer.common.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class DefaultLoggingAlertProducer implements AlertProducer {
    Logger logger = LogManager.getLogger(DefaultLoggingAlertProducer.class);

	private static ObjectMapper objectMapper = new DefaultObjectMapperFactory().createObjectMapper(ObjectMapperFactory.Scope.PROTOTYPE);

    public Optional<Alert> sendAlert(Supplier<Optional<Alert>> alertSupplier) {
        Optional<Alert> optionalAlert = alertSupplier.get();
        optionalAlert.ifPresent(alert -> {
            if (StringUtils.isNotBlank(alert.getCorrelationId())) {
                PrincipalInfo principalInfo = PrincipalContext.getThreadLocal().get();
                alert.setCorrelationId(principalInfo.getCorrelationId());
            }
            logger.error(alert);
        });
        return optionalAlert;
    }
}
