package com.alpha.omega.office.service;

import com.alpha.omega.office.OfficePageHelper;
import com.alpha.omega.office.model.Office;
import com.alpha.omega.office.model.OfficeDistance;
import com.alpha.omega.office.model.OfficeDistancePage;
import com.alpha.omega.office.model.OfficePage;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface OfficeService {

    Mono<OfficePage> getOfficesPas(PageRequest pageRequest);
    Mono<Office> createOffice(Office office);
    Mono<OfficeDistancePage> getNearestOffices(OfficePageHelper officePageHelper);
    Mono<OfficeDistancePage> getNearestOfficesFrom(OfficePageHelper officePageHelper, Double latitude, Double longitude);
    Flux<OfficeDistance> findOfficesFrom(OfficePageHelper officePageHelper, Double latitude, Double longitude);
}
