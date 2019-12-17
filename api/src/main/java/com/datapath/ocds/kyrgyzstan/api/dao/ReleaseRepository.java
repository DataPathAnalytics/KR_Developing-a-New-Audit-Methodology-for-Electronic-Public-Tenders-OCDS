package com.datapath.ocds.kyrgyzstan.api.dao;

import com.datapath.ocds.kyrgyzstan.api.releases.Release;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReleaseRepository extends PagingAndSortingRepository<Release, String> {

    @Query(fields = "{ 'ocid' : 1, 'date' : 1}")
    Page<Release> findByDateAfterOrderByDate(String offset, Pageable pageable);

}
