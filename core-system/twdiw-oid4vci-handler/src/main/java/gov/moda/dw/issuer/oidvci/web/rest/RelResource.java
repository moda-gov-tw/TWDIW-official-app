package gov.moda.dw.issuer.oidvci.web.rest;

import gov.moda.dw.issuer.oidvci.domain.Rel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import gov.moda.dw.issuer.oidvci.repository.RelRepository;
import gov.moda.dw.issuer.oidvci.service.RelQueryService;
import gov.moda.dw.issuer.oidvci.service.RelService;
import gov.moda.dw.issuer.oidvci.service.criteria.RelCriteria;
import gov.moda.dw.issuer.oidvci.service.dto.RelDTO;
import gov.moda.dw.issuer.oidvci.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Rel}.
 */
@RestController
@RequestMapping("/api/rels")
public class RelResource {

  private final Logger log = LoggerFactory.getLogger(RelResource.class);

  private static final String ENTITY_NAME = "rel";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final RelService relService;

  private final RelRepository relRepository;

  private final RelQueryService relQueryService;

  public RelResource(RelService relService, RelRepository relRepository, RelQueryService relQueryService) {
    this.relService = relService;
    this.relRepository = relRepository;
    this.relQueryService = relQueryService;
  }

  /**
   * {@code POST  /rels} : Create a new rel.
   *
   * @param relDTO the relDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new relDTO, or with status {@code 400 (Bad Request)} if the rel has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
//  @PostMapping("")
//  public ResponseEntity<RelDTO> createRel(@Valid @RequestBody RelDTO relDTO) throws URISyntaxException {
//    log.debug("REST request to save Rel : {}", relDTO);
//    if (relDTO.getId() != null) {
//      throw new BadRequestAlertException("A new rel cannot already have an ID", ENTITY_NAME, "idexists");
//    }
//    relDTO = relService.save(relDTO);
//    return ResponseEntity.created(new URI("/api/rels/" + relDTO.getId()))
//      .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, relDTO.getId().toString()))
//      .body(relDTO);
//  }

  /**
   * {@code PUT  /rels/:id} : Updates an existing rel.
   *
   * @param id the id of the relDTO to save.
   * @param relDTO the relDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relDTO,
   * or with status {@code 400 (Bad Request)} if the relDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the relDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
//  @PutMapping("/{id}")
//  public ResponseEntity<RelDTO> updateRel(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody RelDTO relDTO)
//    throws URISyntaxException {
//    log.debug("REST request to update Rel : {}, {}", id, relDTO);
//    if (relDTO.getId() == null) {
//      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//    }
//    if (!Objects.equals(id, relDTO.getId())) {
//      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//    }
//
//    if (!relRepository.existsById(id)) {
//      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//    }
//
//    relDTO = relService.update(relDTO);
//    return ResponseEntity.ok()
//      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relDTO.getId().toString()))
//      .body(relDTO);
//  }

  /**
   * {@code PATCH  /rels/:id} : Partial updates given fields of an existing rel, field will ignore if it is null
   *
   * @param id the id of the relDTO to save.
   * @param relDTO the relDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relDTO,
   * or with status {@code 400 (Bad Request)} if the relDTO is not valid,
   * or with status {@code 404 (Not Found)} if the relDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the relDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
//  @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
//  public ResponseEntity<RelDTO> partialUpdateRel(
//    @PathVariable(value = "id", required = false) final Long id,
//    @NotNull @RequestBody RelDTO relDTO
//  ) throws URISyntaxException {
//    log.debug("REST request to partial update Rel partially : {}, {}", id, relDTO);
//    if (relDTO.getId() == null) {
//      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//    }
//    if (!Objects.equals(id, relDTO.getId())) {
//      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//    }
//
//    if (!relRepository.existsById(id)) {
//      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//    }
//
//    Optional<RelDTO> result = relService.partialUpdate(relDTO);
//
//    return ResponseUtil.wrapOrNotFound(
//      result,
//      HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relDTO.getId().toString())
//    );
//  }

  /**
   * {@code GET  /rels} : get all the rels.
   *
   * @param pageable the pagination information.
   * @param criteria the criteria which the requested entities should match.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rels in body.
   */
//  @GetMapping("")
//  public ResponseEntity<List<RelDTO>> getAllRels(RelCriteria criteria, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
//    log.debug("REST request to get Rels by criteria: {}", criteria);
//
//    Page<RelDTO> page = relQueryService.findByCriteria(criteria, pageable);
//    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//    return ResponseEntity.ok().headers(headers).body(page.getContent());
//  }

  /**
   * {@code GET  /rels/count} : count all the rels.
   *
   * @param criteria the criteria which the requested entities should match.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
   */
//  @GetMapping("/count")
//  public ResponseEntity<Long> countRels(RelCriteria criteria) {
//    log.debug("REST request to count Rels by criteria: {}", criteria);
//    return ResponseEntity.ok().body(relQueryService.countByCriteria(criteria));
//  }

  /**
   * {@code GET  /rels/:id} : get the "id" rel.
   *
   * @param id the id of the relDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the relDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/{id}")
  public ResponseEntity<RelDTO> getRel(@PathVariable("id") Long id) {
    log.debug("REST request to get Rel : {}", id);
    Optional<RelDTO> relDTO = relService.findOne(id);
    return ResponseUtil.wrapOrNotFound(relDTO);
  }

  /**
   * {@code DELETE  /rels/:id} : delete the "id" rel.
   *
   * @param id the id of the relDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRel(@PathVariable("id") Long id) {
    log.debug("REST request to delete Rel : {}", id);
    relService.delete(id);
    return ResponseEntity.noContent()
      .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
      .build();
  }
}
