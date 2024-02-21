package com.alpha.omega.cache;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CacheResults {
	Boolean completed;
	String message;
	String tid;
}
