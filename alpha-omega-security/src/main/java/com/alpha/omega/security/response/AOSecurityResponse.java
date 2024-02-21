package com.alpha.omega.security.response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AOSecurityResponse<T> implements Serializable {
	private static final long serialVersionUID = 2211822157482471396L;

	private String correlationId;
	private T message;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime timestamp;
	private HttpStatus status;

	public String getCorrelationId() {
		return correlationId;
	}

	public T getMessage() {
		return message;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public static PwcSecurityResponseBuilder newBuilder() {
		return new PwcSecurityResponseBuilder();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("PwcSecurityResponse{");
		sb.append("correlationId='").append(correlationId).append('\'');
		sb.append(", message=").append(message);
		sb.append(", timestamp=").append(timestamp);
		sb.append(", status=").append(status);
		sb.append('}');
		return sb.toString();
	}

	@JsonPOJOBuilder(withPrefix = "set")
	public static final class PwcSecurityResponseBuilder<T> {
		private String correlationId;
		private T message;
		private LocalDateTime timestamp;
		private HttpStatus status;

		private PwcSecurityResponseBuilder() {
		}

		public static PwcSecurityResponseBuilder aPwcSecurityResponse() {
			return new PwcSecurityResponseBuilder();
		}

		public PwcSecurityResponseBuilder setCorrelationId(String correlationId) {
			this.correlationId = correlationId;
			return this;
		}

		public PwcSecurityResponseBuilder setMessage(T message) {
			this.message = message;
			return this;
		}

		public PwcSecurityResponseBuilder setTimestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public PwcSecurityResponseBuilder setStatus(HttpStatus status) {
			this.status = status;
			return this;
		}

		public AOSecurityResponse build() {
			AOSecurityResponse AOSecurityResponse = new AOSecurityResponse();
			AOSecurityResponse.status = this.status;
			AOSecurityResponse.correlationId = this.correlationId;
			AOSecurityResponse.message = this.message;
			AOSecurityResponse.timestamp = this.timestamp;
			return AOSecurityResponse;
		}
	}

}
