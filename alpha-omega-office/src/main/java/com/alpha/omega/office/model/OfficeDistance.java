package com.alpha.omega.office.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.geo.Distance;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@SuperBuilder
public class OfficeDistance extends Office{
    Distance distance;
}
