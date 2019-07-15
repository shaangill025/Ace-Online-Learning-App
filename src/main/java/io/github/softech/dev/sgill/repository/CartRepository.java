package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Cart;
import io.github.softech.dev.sgill.domain.Customer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Max;
import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Cart entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartRepository extends JpaRepository<Cart, Long>, JpaSpecificationExecutor<Cart> {
    Optional<List<Cart>> findCartsByCustomerIdAndCheckout(Long id, Boolean flag);
    Optional<List<Cart>> getCartsByCustomer(Customer customer);
    List<Cart> getCartByCustomerId(Long id);
    Optional<List<Cart>> findCartsByCustomer(Customer customer);
}
