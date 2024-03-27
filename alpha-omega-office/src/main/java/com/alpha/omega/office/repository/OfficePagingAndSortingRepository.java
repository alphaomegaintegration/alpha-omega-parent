package com.alpha.omega.office.repository;

import com.alpha.omega.office.model.Office;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OfficePagingAndSortingRepository extends PagingAndSortingRepository<Office, String> {
}
