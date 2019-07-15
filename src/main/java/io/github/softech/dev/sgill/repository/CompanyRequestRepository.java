package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.CompanyRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the CompanyRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanyRequestRepository extends JpaRepository<CompanyRequest, Long>, JpaSpecificationExecutor<CompanyRequest> {

}
