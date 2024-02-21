package com.alpha.omega.core.alerts;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class Alert {

	String alertName;
	String callingService;
	String destinationService;
	Integer alertLevel;
	AlertPriority alertPriority;
	Integer httpStatus;
	Exception thrownException;
	String principal;
	String potentialResolution;
	String message;



}
