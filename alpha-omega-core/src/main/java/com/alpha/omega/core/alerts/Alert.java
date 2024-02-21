package com.pwc.base.alerts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pwc.base.log.PWCMessage;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;


@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"thrownException"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Alert extends PWCMessage {

	String alertName;
	String engagementId;
	String callingService;
	String destinationService;
	Integer alertLevel;
	AlertPriority alertPriority;
	Integer httpStatus;
	Exception thrownException;
	String principal;
	String potentialResolution;



	public static Builder newBuilder(){
		return new Builder();
	}

	public String getAlertName() {
		return alertName;
	}

	public String getEngagementId() {
		return engagementId;
	}

	public String getCallingService() {
		return callingService;
	}

	public String getDestinationService() {
		return destinationService;
	}

	public Integer getAlertLevel() {
		return alertLevel;
	}

	public AlertPriority getAlertPriority() {
		return alertPriority;
	}

	public Integer getHttpStatus() {
		return httpStatus;
	}

	public Exception getThrownException() {
		return thrownException;
	}

	public String getPrincipal() {
		return principal;
	}

	public String getPotentialResolution() {
		return potentialResolution;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Alert{");
		sb.append("alertName='").append(alertName).append('\'');
		sb.append(", engagementId='").append(engagementId).append('\'');
		sb.append(", callingService='").append(callingService).append('\'');
		sb.append(", destinationService='").append(destinationService).append('\'');
		sb.append(", alertLevel=").append(alertLevel);
		sb.append(", alertPriority=").append(alertPriority);
		sb.append(", httpStatus=").append(httpStatus);
		sb.append(", thrownException=").append(thrownException);
		sb.append(", principal='").append(principal).append('\'');
		sb.append(", potentialResolution='").append(potentialResolution).append('\'');
		sb.append(", timestamp=").append(timestamp);
		sb.append(", logLevel='").append(logLevel).append('\'');
		sb.append(", correlationId='").append(correlationId).append('\'');
		sb.append(", message='").append(message).append('\'');
		sb.append(", component='").append(component).append('\'');
		sb.append(", level='").append(level).append('\'');
		sb.append(", code='").append(code).append('\'');
		sb.append(", raiseAlarm='").append(raiseAlarm).append('\'');
		sb.append(", userId='").append(userId).append('\'');
		sb.append(", clientId='").append(clientId).append('\'');
		sb.append(", regionAndEnv='").append(regionAndEnv).append('\'');
		sb.append(", serverInfo='").append(serverInfo).append('\'');
		sb.append(", additionalInfo=").append(additionalInfo);
		sb.append(", stackTrace=").append(stackTrace);
		sb.append('}');
		return sb.toString();
	}

	public static final class Builder extends PWCMessage.Builder {
		String alertName;
		String engagementId;
		String callingService;
		String destinationService;
		Integer alertLevel;
		AlertPriority alertPriority;
		Integer httpStatus;
		Exception thrownException;
		String principal;
		String potentialResolution;
		LocalDateTime timestamp = LocalDateTime.now();
		String logLevel;
		String correlationId;
		String message;
		String component;
		String level;
		String code;
		String raiseAlarm;
		String userId;
		String clientId;
		String regionAndEnv;
		String serverInfo;
		Map<String, Object> additionalInfo;

		public Builder setAlertName(String alertName) {
			this.alertName = alertName;
			return this;
		}

		public Builder setEngagementId(String engagementId) {
			this.engagementId = engagementId;
			return this;
		}

		public Builder setCallingService(String callingService) {
			this.callingService = callingService;
			return this;
		}

		public Builder setDestinationService(String destinationService) {
			this.destinationService = destinationService;
			return this;
		}

		public Builder setAlertLevel(Integer alertLevel) {
			this.alertLevel = alertLevel;
			return this;
		}

		public Builder setAlertPriority(AlertPriority alertPriority) {
			this.alertPriority = alertPriority;
			return this;
		}

		public Builder setHttpStatus(Integer httpStatus) {
			this.httpStatus = httpStatus;
			return this;
		}

		public Builder setThrownException(Exception thrownException) {
			this.thrownException = thrownException;
			return this;
		}

		public Builder setPrincipal(String principal) {
			this.principal = principal;
			return this;
		}

		public Builder setPotentialResolution(String potentialResolution) {
			this.potentialResolution = potentialResolution;
			return this;
		}

		public Builder setTimestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder setLogLevel(String logLevel) {
			this.logLevel = logLevel;
			return this;
		}

		public Builder setCorrelationId(String correlationId) {
			this.correlationId = correlationId;
			return this;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setComponent(String component) {
			this.component = component;
			return this;
		}

		public Builder setLevel(String level) {
			this.level = level;
			return this;
		}

		public Builder setCode(String code) {
			this.code = code;
			return this;
		}

		public Builder setRaiseAlarm(String raiseAlarm) {
			this.raiseAlarm = raiseAlarm;
			return this;
		}

		public Builder setUserId(String userId) {
			this.userId = userId;
			return this;
		}

		public Builder setClientId(String clientId) {
			this.clientId = clientId;
			return this;
		}

		public Builder setRegionAndEnv(String regionAndEnv) {
			this.regionAndEnv = regionAndEnv;
			return this;
		}

		public Builder setServerInfo(String serverInfo) {
			this.serverInfo = serverInfo;
			return this;
		}

		public Builder setAdditionalInfo(Map<String, Object> additionalInfo) {
			this.additionalInfo = additionalInfo;
			return this;
		}

		public Alert build() {
			Alert alert = new Alert();
			alert.thrownException = this.thrownException;
			alert.engagementId = this.engagementId;
			alert.clientId = this.clientId;
			alert.alertName = this.alertName;
			alert.callingService = this.callingService;
			alert.raiseAlarm = this.raiseAlarm;
			alert.additionalInfo = this.additionalInfo;
			alert.serverInfo = this.serverInfo;
			alert.correlationId = this.correlationId;
			alert.timestamp = this.timestamp;
			alert.message = this.message;
			alert.code = this.code;
			alert.potentialResolution = this.potentialResolution;
			alert.alertLevel = this.alertLevel;
			alert.httpStatus = this.httpStatus;
			alert.userId = this.userId;
			alert.destinationService = this.destinationService;
			alert.level = this.level;
			alert.alertPriority = this.alertPriority;
			alert.principal = this.principal;
			alert.logLevel = this.logLevel;
			alert.component = this.component;
			alert.regionAndEnv = this.regionAndEnv;
			/*
			if (StringUtils.isBlank(raiseAlarm)){
				throw new IllegalArgumentException("RasieAlarm value should b set");
			}

			 */
			return alert;
		}
	}
}
