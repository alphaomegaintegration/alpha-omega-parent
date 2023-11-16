package com.alpha.omega.security;

import reactor.core.publisher.Mono;

public interface UserContextPermissionsService {
    Mono<UserContextPermissions> getUserContextByUserIdAndContextId(UserContextRequest userContextRequest);
}
