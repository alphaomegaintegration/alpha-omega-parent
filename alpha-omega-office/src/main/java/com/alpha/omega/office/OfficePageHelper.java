package com.alpha.omega.office;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class OfficePageHelper {

    @Builder.Default
    Integer page = 0;
    @Builder.Default
    Integer pageSize = 25;
    String direction;
    Sort sort;
    Point originatingPoint;
    Point officePoint;
    @Builder.Default
    Double officeRadius = Double.valueOf(50.0);

    public PageRequest toPageRequest(){

        if (page < 0){
            page = 0;
        }

        if (pageSize < 1){
            pageSize = 1;
        }
        Sort.Direction sortdDirection = null;
        try{
            sortdDirection = Sort.Direction.fromString(direction);
        } catch (Exception e){
            sortdDirection = Sort.Direction.ASC;
        }
        sort = Sort.by("name");
        return PageRequest.of(page,pageSize,sort);
    }
}
