package com.alpha.omega.security.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstantExpiry {

    @Builder.Default
    private Duration clockSkew = Duration.ofSeconds(60);
    @Builder.Default
    private Clock clock = Clock.systemUTC();

    public boolean hasInstantExpired(Instant instant) {
        return this.clock.instant().isAfter(instant.minus(this.clockSkew));
    }

    public void setClockSkew(Duration clockSkew) {
        Assert.notNull(clockSkew, "clockSkew cannot be null");
        Assert.isTrue(clockSkew.getSeconds() >= 0, "clockSkew must be >= 0");
        this.clockSkew = clockSkew;
    }

    public void setClock(Clock clock) {
        Assert.notNull(clock, "clock cannot be null");
        this.clock = clock;
    }
}
