package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.service.dto.CreateVCItemDataDTO;
import gov.moda.dw.manager.service.dto.VCItemDataDTO;
import org.springframework.stereotype.Service;

@Service
public class DwSandBoxVC301WService {

  private final Dwvc100iService dwvc100iService;

  public DwSandBoxVC301WService(Dwvc100iService dwvc100iService) {
    this.dwvc100iService = dwvc100iService;
  }

  /**
   * Save a vCItemData.
   *
   * @param vCItemDataDTO the entity to save.
   * @return the persisted entity.
   */
  public VCItemDataDTO save(CreateVCItemDataDTO vCItemDataDTO) {
    return this.dwvc100iService.createVCHasDataForWeb(vCItemDataDTO);
  }
}
