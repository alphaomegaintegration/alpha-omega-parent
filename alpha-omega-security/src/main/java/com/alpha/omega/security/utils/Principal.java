package com.alpha.omega.security.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class Principal implements Serializable {
    private static final long serialVersionUID = -7650357515685965164L;

    private String firstName, lastName, guid, email;

}
