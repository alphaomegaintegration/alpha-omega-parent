package com.alpha.omega.office.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class OfficePage {

    String elapsed;
    String correlationId;
    Integer page;
    Integer pageSize;
    Integer total;
    @Builder.Default
    List<? extends Office> content = new ArrayList<>();


}
