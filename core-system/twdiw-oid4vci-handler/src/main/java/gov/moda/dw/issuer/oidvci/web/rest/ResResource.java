package gov.moda.dw.issuer.oidvci.web.rest;

import gov.moda.dw.issuer.oidvci.domain.Res;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import gov.moda.dw.issuer.oidvci.repository.ResRepository;
import gov.moda.dw.issuer.oidvci.service.ResQueryService;
import gov.moda.dw.issuer.oidvci.service.ResService;
import gov.moda.dw.issuer.oidvci.service.criteria.ResCriteria;
import gov.moda.dw.issuer.oidvci.service.dto.ResDTO;
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
 * REST controller for managing {@link Res}.
 */
@RestController
@RequestMapping("/api/res")
public class ResResource {

  private final Logger log = LoggerFactory.getLogger(ResResource.class);

  private static final String ENTITY_NAME = "res";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final ResService resService;

  private final ResRepository resRepository;

  private final ResQueryService resQueryService;

  public ResResource(ResService resService, ResRepository resRepository, ResQueryService resQueryService) {
    this.resService = resService;
    this.resRepository = resRepository;
    this.resQueryService = resQueryService;
  }

  /**
   * {@code POST  /res} : Create a new res.
   *
   * @param resDTO the resDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new resDTO, or with status {@code 400 (Bad Request)} if the res has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("")
  public ResponseEntity<ResDTO> createRes(@Valid @RequestBody ResDTO resDTO) throws URISyntaxException {
    log.debug("REST request to save Res : {}", resDTO);
    if (resDTO.getId() != null) {
      throw new BadRequestAlertException("A new res cannot already have an ID", ENTITY_NAME, "idexists");
    }
    resDTO = resService.save(resDTO);
    return ResponseEntity.created(new URI("/api/res/" + resDTO.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, resDTO.getId().toString()))
      .body(resDTO);
  }

  /**
   * {@code PUT  /res/:id} : Updates an existing res.
   *
   * @param id the id of the resDTO to save.
   * @param resDTO the resDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resDTO,
   * or with status {@code 400 (Bad Request)} if the resDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the resDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/{id}")
  public ResponseEntity<ResDTO> updateRes(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody ResDTO resDTO)
    throws URISyntaxException {
    log.debug("REST request to update Res : {}, {}", id, resDTO);
    if (resDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, resDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!resRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    resDTO = resService.update(resDTO);
    return ResponseEntity.ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, resDTO.getId().toString()))
      .body(resDTO);
  }

  /**
   * {@code PATCH  /res/:id} : Partial updates given fields of an existing res, field will ignore if it is null
   *
   * @param id the id of the resDTO to save.
   * @param resDTO the resDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resDTO,
   * or with status {@code 400 (Bad Request)} if the resDTO is not valid,
   * or with status {@code 404 (Not Found)} if the resDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the resDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<ResDTO> partialUpdateRes(
    @PathVariable(value = "id", required = false) final Long id,
    @NotNull @RequestBody ResDTO resDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update Res partially : {}, {}", id, resDTO);
    if (resDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, resDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!resRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<ResDTO> result = resService.partialUpdate(resDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, resDTO.getId().toString())
    );
  }

  /**
   * {@code GET  /res} : get all the res.
   *
   * @param pageable the pagination information.
   * @param criteria the criteria which the requested entities should match.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of res in body.
   */
  @GetMapping("")
  public ResponseEntity<List<ResDTO>> getAllRes(ResCriteria criteria, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get Res by criteria: {}", criteria);

    Page<ResDTO> page = resQueryService.findByCriteria(criteria, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /res/count} : count all the res.
   *
   * @param criteria the criteria which the requested entities should match.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
   */
  @GetMapping("/count")
  public ResponseEntity<Long> countRes(ResCriteria criteria) {
    log.debug("REST request to count Res by criteria: {}", criteria);
    return ResponseEntity.ok().body(resQueryService.countByCriteria(criteria));
  }

  /**
   * {@code GET  /res/:id} : get the "id" res.
   *
   * @param id the id of the resDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the resDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ResDTO> getRes(@PathVariable("id") Long id) {
    log.debug("REST request to get Res : {}", id);
    Optional<ResDTO> resDTO = resService.findOne(id);
    return ResponseUtil.wrapOrNotFound(resDTO);
  }

  /**
   * {@code DELETE  /res/:id} : delete the "id" res.
   *
   * @param id the id of the resDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRes(@PathVariable("id") Long id) {
    log.debug("REST request to delete Res : {}", id);
    resService.delete(id);
    return ResponseEntity.noContent()
      .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
      .build();
  }
}
