package com.alpha.omega.security;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UserContextRequest {
    @ApiModelProperty(required = false, value = "")
    String userId;
    @ApiModelProperty(required = false, value = "")
    String contextId;
    @ApiModelProperty(required = false, value = "")
    Boolean allRoles;
    @ApiModelProperty(required = false, value = "")
    String roles;
    @ApiModelProperty(required = false, value = "")
    String cacheControl;

}
