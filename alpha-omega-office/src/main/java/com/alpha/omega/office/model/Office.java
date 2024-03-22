package com.alpha.omega.office.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import reactor.util.function.Tuple2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.alpha.omega.office.OfficeConstants.OFFICE_KEY_PREFIX;


@RedisHash(OFFICE_KEY_PREFIX)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@SuperBuilder
public class Office implements Serializable {


    @JsonIgnore
    Long serialVersionUID = 719504915910873283L;

     /*
   0  Office,
   1  Address,
   2  City,
   3  State,
   4  Zip,
   5  Latitude,
   6  Longitude,
   7  LOCAL START TIME,
   8  LOCAL END TIME,
   9  UTC START TIME,
   10 UTC END TIME

     */

    @Id
    @ApiModelProperty(required = true, value = "")
    private String id;

    @Indexed
    @ApiModelProperty(required = true, value = "")
    String name;

    @ApiModelProperty(required = false, value = "")
    String address;

    @ApiModelProperty(required = false, value = "")
    String city;

    @ApiModelProperty(required = false, value = "")
    String state;

    @ApiModelProperty(required = false, value = "")
    String zip;

    @ApiModelProperty(required = true, value = "")
    BigDecimal latitude;

    @ApiModelProperty(required = true, value = "")
    BigDecimal longitude;

    @ApiModelProperty(required = false, value = "")
    LocalTime open;

    @ApiModelProperty(required = false, value = "")
    LocalTime close;

    @ApiModelProperty(required = false, value = "")
    Map<DayOfWeek, List<Tuple2<LocalTime,LocalTime>>> hoursOfOperation = new TreeMap<>();

}
