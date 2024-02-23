package com.alpha.omega.security.context;

import reactor.core.publisher.Mono;

public interface UserContextPermissionsService {
    Mono<UserContextPermissions> getUserContextByUserIdAndContextId(UserContextRequest userContextRequest);
}
