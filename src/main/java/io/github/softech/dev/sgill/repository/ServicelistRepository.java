package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.Servicelist;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Servicelist entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServicelistRepository extends JpaRepository<Servicelist, Long>, JpaSpecificationExecutor<Servicelist> {
    Servicelist findServicelistByCustomer(Customer customer);
}
