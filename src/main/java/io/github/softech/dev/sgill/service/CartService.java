package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.Cart;

import io.github.softech.dev.sgill.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Cart.
 */
public interface CartService {

    /**
     * Save a cart.
     *
     * @param cart the entity to save
     * @return the persisted entity
     */
    Cart save(Cart cart);

    /**
     * Get all the carts.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Cart> findAll(Pageable pageable);


    /**
     * Get the "id" cart.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Cart> findOne(Long id);

    /**
     * Delete the "id" cart.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the cart corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Cart> search(String query, Pageable pageable);

    Optional<List<Cart>> getCartsByCustomer(Customer customer);
}
