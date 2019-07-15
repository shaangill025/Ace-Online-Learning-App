package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Company;
import io.github.softech.dev.sgill.domain.Course;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Customer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    Customer findCustomersByUser(User user);
    Customer findCustomerByUserId(Long id);
    List<Customer> findCustomersByAreaserviced(String area);
    List<Customer> findCustomersByCity(String city);
    List<Customer> findCustomersByCompany(Company company);
    Optional<List<Customer>> findCustomersByHidden(String hidden);
    Long countCustomersByHidden(String hidden);
}
