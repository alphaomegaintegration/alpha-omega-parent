package com.alpha.omega.office.service;

import com.alpha.omega.core.utils.CoreUtils;
import com.alpha.omega.office.OfficePageHelper;
import com.alpha.omega.office.model.Office;
import com.alpha.omega.office.model.OfficeDistance;
import com.alpha.omega.office.model.OfficeDistancePage;
import com.alpha.omega.office.model.OfficePage;
import com.alpha.omega.office.repository.OfficePagingAndSortingRepository;
import com.alpha.omega.office.repository.OfficeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.alpha.omega.core.utils.CoreUtils.calculateSkip;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisOfficeService implements OfficeService{

    private static final Logger logger = LoggerFactory.getLogger(RedisOfficeService.class);
    public static final String OFFICE_GEO_KEY = "office:geo";

    ReactiveStringRedisTemplate redisTemplate;
    OfficeRepository officeRepository;
    OfficePagingAndSortingRepository officeRepositoryPas;
    ObjectMapper objectMapper;
    Function<Office, Optional<String>> officeToStringFunction;
    Function<String, Optional<Office>> stringToOfficeFunction;
    Function<GeoResult<RedisGeoCommands.GeoLocation<String>>, Optional<OfficeDistance>> stringToOfficeDistanceFunction;

    @PostConstruct
    public void init(){
        officeToStringFunction = new OfficeToString(objectMapper);
        stringToOfficeFunction = new StringToOffice(objectMapper);
        stringToOfficeDistanceFunction = new StringToOfficeDistance(objectMapper);
    }


    @Override
    public Mono<OfficePage> getOfficesPas(PageRequest pageRequest) {
        return Mono.just(pageRequest)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(pageRequest1 -> logger.debug("getOfficesPas Got page request => {}", pageRequest1))
                .flatMap(request -> Mono.zip(Mono.just(officeRepository.count()),
                        Flux.fromIterable(officeRepositoryPas.findAll(Sort.by("name")))
                                .skip(calculateSkip.apply(pageRequest))
                                .take(request.getPageSize())
                                .collectList(),
                        (t1, t2) -> Tuples.of(t1, t2)))

                .elapsed()
                .map(tuplePage -> {
                    Tuple2<Long, List<Office>> tupleList = tuplePage.getT2();
                    List<Office> list = tupleList.getT2();
                    OfficePage contextPage = new OfficePage();
                    contextPage.setPage(pageRequest.getPageNumber());
                    contextPage.setTotal(tupleList.getT1().intValue());
                    contextPage.setPageSize(list.size());
                    contextPage.setContent(list);
                    contextPage.setElapsed(tuplePage.getT1().toString());
                    return contextPage;
                });
    }

    @Override
    public Mono<Office> createOffice(Office pOffice) {
        return Mono.just(pOffice)
                .publishOn(Schedulers.boundedElastic())
                .map(office -> Tuples.of(office,officeRepository.findByName(office.getName())))
                .map(tuple ->  tuple.getT2().isPresent() ? tuple.getT2().get() : officeRepository.save(tuple.getT1()))
                .flatMap(office -> redisTemplate.opsForGeo().add(OFFICE_GEO_KEY,
                        new Point(office.getLongitude().doubleValue(),office.getLatitude().doubleValue()),
                                officeToStringFunction.apply(office).get())
                        .map(val -> office));
    }


    public static class OfficeToString implements Function<Office, Optional<String>>{

        ObjectMapper objectMapper;

        public OfficeToString(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public Optional<String> apply(Office office) {
            Optional<String> optional = Optional.empty();
            try{
                optional = Optional.of(objectMapper.writeValueAsString(office));
            } catch (Exception e){
                logger.warn("Could not serialize office {}",office,e);
            }
            return optional;
        }
    }

    public static class StringToOffice implements Function<String, Optional<Office>>{

        ObjectMapper objectMapper;

        public StringToOffice(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public Optional<Office> apply(String source) {
            Optional<Office> optional = Optional.empty();
            try{
                optional = Optional.of(objectMapper.readValue(source, Office.class));
            } catch (Exception e){
                logger.warn("Could not deserialize office {}",source,e);
            }
            return optional;
        }
    }


    public static class StringToOfficeDistance implements Function<GeoResult<RedisGeoCommands.GeoLocation<String>>, Optional<OfficeDistance>>{

        ObjectMapper objectMapper;

        public StringToOfficeDistance(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public Optional<OfficeDistance> apply(GeoResult<RedisGeoCommands.GeoLocation<String>> source) {
            Optional<OfficeDistance> optional = Optional.empty();
            try{
                OfficeDistance officeDistance = objectMapper.readValue(source.getContent().getName(), OfficeDistance.class);
                officeDistance.setDistance(source.getDistance());
                optional = Optional.of(officeDistance);
            } catch (Exception e){
                logger.warn("Could not deserialize office {}",source,e);
            }
            return optional;
        }
    }


    public static final RedisGeoCommands.GeoRadiusCommandArgs distanceCommandArgs = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().sortAscending();


    @Override
    public Mono<OfficeDistancePage> getNearestOffices(OfficePageHelper officePageHelper) {

        if (officePageHelper.getOriginatingPoint() == null){
            throw new IllegalArgumentException("originatingPoint cannot be null!");
        }

        return Flux.just(officePageHelper)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(helper ->  findOfficesFrom(helper, helper.getOriginatingPoint().getX(),
                        helper.getOriginatingPoint().getY()))
                .collectList()
                .elapsed()
                .map(getTuple2OfficePageFunction(officePageHelper));
    }

    @Override
    public Mono<OfficeDistancePage> getNearestOfficesFrom(OfficePageHelper officePageHelper, Double latitude, Double longitude) {
        return findOfficesFrom(officePageHelper, latitude, longitude)
                .collectList()
                .elapsed()
                .map(getTuple2OfficePageFunction(officePageHelper));
    }

    @Override
    public Flux<OfficeDistance> findOfficesFrom(OfficePageHelper officePageHelper, Double latitude, Double longitude) {
        Point applicantPoint = new Point(longitude, latitude);
        Distance applicantDistance = new Distance(officePageHelper.getOfficeRadius(), Metrics.MILES);

        Circle applicantCircle =new Circle(applicantPoint, applicantDistance);
        PageRequest pageRequest = officePageHelper.toPageRequest();
        return Flux.just(applicantCircle)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(circle -> redisTemplate.opsForGeo().radius(OFFICE_GEO_KEY, applicantCircle, distanceCommandArgs))
                .skip(calculateSkip.apply(pageRequest))
                .take(pageRequest.getPageSize())
                .map(result -> stringToOfficeDistanceFunction.apply(result))
                .filter(Optional::isPresent)
                .map(Optional::get);


    }


    static Function<Tuple2<Long, List<OfficeDistance>>, OfficeDistancePage> getTuple2OfficePageFunction(OfficePageHelper officePageHelper) {
        return tuplePage -> {
            List<OfficeDistance> list = tuplePage.getT2();
            OfficeDistancePage contextPage = new OfficeDistancePage();
            contextPage.setPage(officePageHelper.toPageRequest().getPageNumber());
            contextPage.setTotal(tuplePage.getT1().intValue());
            contextPage.setPageSize(list.size());
            contextPage.setContent(list);
            contextPage.setElapsed(tuplePage.getT1().toString());
            return contextPage;
        };
    }


}
