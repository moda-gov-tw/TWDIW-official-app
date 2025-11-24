package gov.moda.dw.issuer.oidvci.service;

import gov.moda.dw.issuer.oidvci.domain.CredentialPolicyEntity;
import gov.moda.dw.issuer.oidvci.domain.OidvciSettingEntity;
import gov.moda.dw.issuer.oidvci.repository.CredentialPolicyRepository;
import gov.moda.dw.issuer.oidvci.repository.OidvciSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CredentialPolicyService {
    private static final Logger log = LoggerFactory.getLogger(OidvciSettingService.class);

    private final CredentialPolicyRepository credentialPolicyRepository;

    public CredentialPolicyService(CredentialPolicyRepository credentialPolicyRepository) {
        this.credentialPolicyRepository = credentialPolicyRepository;
    }

    public List<CredentialPolicyEntity> getCredentialPolicyByType(String credential_type) {
        return credentialPolicyRepository.findByCredentialType(credential_type);
    }

}
