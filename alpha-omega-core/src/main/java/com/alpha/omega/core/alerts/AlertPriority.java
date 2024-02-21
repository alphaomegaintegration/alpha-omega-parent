package com.pwc.base.alerts;

public enum AlertPriority {
	CRITICAL(1), HIGH(2), MEDIUM(3), LOW(4), UNKNOWN(5);

	int priority;
	AlertPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public static AlertPriority fromPriority(int priority){
		AlertPriority alertPriority = AlertPriority.UNKNOWN;
		if (priority == CRITICAL.getPriority()){
			alertPriority = CRITICAL;
		} else if (priority == HIGH.getPriority()){
			alertPriority = HIGH;
		} else if (priority == MEDIUM.getPriority()){
			alertPriority = MEDIUM;
		} else if (priority == LOW.getPriority()){
			alertPriority = LOW;
		}
		return alertPriority;
	}

}
