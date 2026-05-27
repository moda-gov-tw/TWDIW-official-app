package gov.moda.dw.issuer.oidvci.service;

import gov.moda.dw.issuer.oidvci.domain.CredentialEntity;
import gov.moda.dw.issuer.oidvci.repository.CredentialRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CredentialService {

  private final CredentialRepository credentialRepository;

  public CredentialService(CredentialRepository credentialRepository) {
    this.credentialRepository = credentialRepository;
  }

  public List<CredentialEntity> getCredentialCID(String cid) {
    return credentialRepository.findByCID(cid);
  }
}
