package com.alpha.omega.office.repository;

import com.alpha.omega.office.model.Office;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface OfficeRepository extends CrudRepository<Office, String> {

    Optional<Office> findByName(String name);
    List<Office> findByNameIn(List<String> names);
}
