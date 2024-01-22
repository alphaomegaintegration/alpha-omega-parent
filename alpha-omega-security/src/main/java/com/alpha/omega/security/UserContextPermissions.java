package com.alpha.omega.security;
/*
 * user-context-service
 * Data Platform User Context Service
 *
 * The version of the OpenAPI document: v1
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * UserContextPermissions
 */
@JsonPropertyOrder({
        UserContextPermissions.JSON_PROPERTY_USER_ID,
        UserContextPermissions.JSON_PROPERTY_CONTEXT_ID,
        UserContextPermissions.JSON_PROPERTY_ROLE_ID,
        UserContextPermissions.JSON_PROPERTY_PERMISSIONS,
        UserContextPermissions.JSON_PROPERTY_ENABLED
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-01-11T09:51:33.037400-05:00[America/New_York]")
public class UserContextPermissions {
    public static final String JSON_PROPERTY_USER_ID = "userId";
    private String userId;

    public static final String JSON_PROPERTY_CONTEXT_ID = "contextId";
    private String contextId;

    public static final String JSON_PROPERTY_ROLE_ID = "roleId";
    private String roleId;

    public static final String JSON_PROPERTY_PERMISSIONS = "permissions";
    private List<String> permissions = new ArrayList<>();

    public static final String JSON_PROPERTY_ENABLED = "enabled";
    private Boolean enabled;

    public UserContextPermissions() {
    }

    public UserContextPermissions userId(String userId) {

        this.userId = userId;
        return this;
    }

    /**
     * Get userId
     * @return userId
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")
    @JsonProperty(JSON_PROPERTY_USER_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)

    public String getUserId() {
        return userId;
    }


    @JsonProperty(JSON_PROPERTY_USER_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setUserId(String userId) {
        this.userId = userId;
    }


    public UserContextPermissions contextId(String contextId) {

        this.contextId = contextId;
        return this;
    }

    /**
     * Get contextId
     * @return contextId
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")
    @JsonProperty(JSON_PROPERTY_CONTEXT_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)

    public String getContextId() {
        return contextId;
    }


    @JsonProperty(JSON_PROPERTY_CONTEXT_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setContextId(String contextId) {
        this.contextId = contextId;
    }


    public UserContextPermissions roleId(String roleId) {

        this.roleId = roleId;
        return this;
    }

    /**
     * Get roleId
     * @return roleId
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")
    @JsonProperty(JSON_PROPERTY_ROLE_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

    public String getRoleId() {
        return roleId;
    }


    @JsonProperty(JSON_PROPERTY_ROLE_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }


    public UserContextPermissions permissions(List<String> permissions) {

        this.permissions = permissions;
        return this;
    }

    public UserContextPermissions addPermissionsItem(String permissionsItem) {
        this.permissions.add(permissionsItem);
        return this;
    }

    /**
     * Get permissions
     * @return permissions
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")
    @JsonProperty(JSON_PROPERTY_PERMISSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)

    public List<String> getPermissions() {
        return permissions;
    }


    @JsonProperty(JSON_PROPERTY_PERMISSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }


    public UserContextPermissions enabled(Boolean enabled) {

        this.enabled = enabled;
        return this;
    }

    /**
     * Get enabled
     * @return enabled
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")
    @JsonProperty(JSON_PROPERTY_ENABLED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

    public Boolean getEnabled() {
        return enabled;
    }


    @JsonProperty(JSON_PROPERTY_ENABLED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserContextPermissions userContextPermissions = (UserContextPermissions) o;
        return Objects.equals(this.userId, userContextPermissions.userId) &&
                Objects.equals(this.contextId, userContextPermissions.contextId) &&
                Objects.equals(this.roleId, userContextPermissions.roleId) &&
                Objects.equals(this.permissions, userContextPermissions.permissions) &&
                Objects.equals(this.enabled, userContextPermissions.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, contextId, roleId, permissions, enabled);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UserContextPermissions {\n");
        sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
        sb.append("    contextId: ").append(toIndentedString(contextId)).append("\n");
        sb.append("    roleId: ").append(toIndentedString(roleId)).append("\n");
        sb.append("    permissions: ").append(toIndentedString(permissions)).append("\n");
        sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
