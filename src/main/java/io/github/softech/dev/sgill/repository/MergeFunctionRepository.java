package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.MergeFunction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the MergeFunction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MergeFunctionRepository extends JpaRepository<MergeFunction, Long> {
    Optional<MergeFunction> findMergeFunctionByTobeRemovedAndReplacement(Customer tobe, Customer newRepl);
}
