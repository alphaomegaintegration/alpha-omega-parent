package com.alpha.omega.office.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OfficeDistancePage extends OfficePage{

}
