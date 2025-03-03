package com.modsen.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * A generic interface for service layer operations.
 *
 * @param <K> the type of the entity's identifier
 * @param <Q> the type of the request object used for creating and updating entities
 * @param <S> the type of the response object returned by service methods
 */
public interface AbstractService<K, Q, S> {

  /**
   * Creates a new entity based on the provided request object.
   *
   * @param t the request object containing the details of the entity to be created
   * @return the response object representing the created entity
   */
  S create(Q t);

  /**
   * Retrieves all entities, paginated.
   *
   * @param pageable the pagination information
   * @return a page of response objects representing the entities
   */
  Page<S> getAll(Pageable pageable);

  /**
   * Retrieves an entity by its identifier.
   *
   * @param id the identifier of the entity
   * @return the response object representing the found entity
   */
  S getById(K id);

  /**
   * Updates an existing entity identified by its identifier.
   *
   * @param id the identifier of the entity to be updated
   * @param t the request object containing the new details of the entity
   * @return the response object representing the updated entity
   */
  S update(K id, Q t);

  /**
   * Deletes an entity by its identifier.
   *
   * @param id the identifier of the entity to be deleted
   */
  void delete(K id);
}
