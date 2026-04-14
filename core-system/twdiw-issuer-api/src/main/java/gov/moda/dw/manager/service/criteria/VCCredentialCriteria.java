package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCCredentialCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private StringFilter credentialType;

    private InstantFilter issuanceDate;

    private StringFilter credentialStatus;

    public VCCredentialCriteria() {}

    public VCCredentialCriteria(VCCredentialCriteria other) {
        this.credentialType = other.optionalCredentialType().map(StringFilter::copy).orElse(null);
        this.issuanceDate = other.optionalIssuanceDate().map(InstantFilter::copy).orElse(null);
        this.credentialStatus = other.optionalCredentialStatus().map(StringFilter::copy).orElse(null);
    }

    public StringFilter getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(StringFilter credentialType) {
        this.credentialType = credentialType;
    }

    public Optional<StringFilter> optionalCredentialType() {
        return Optional.ofNullable(credentialType);
    }

    public StringFilter credentialType() {
        if (credentialType == null) {
            setCredentialType(new StringFilter());
        }
        return credentialType;
    }

    public InstantFilter getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(InstantFilter issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public Optional<InstantFilter> optionalIssuanceDate() {
        return Optional.ofNullable(issuanceDate);
    }

    public InstantFilter issuanceDate() {
        if (issuanceDate == null) {
            setIssuanceDate(new InstantFilter());
        }
        return issuanceDate;
    }

    public StringFilter getCredentialStatus() {
        return credentialStatus;
    }

    public void setCredentialStatus(StringFilter credentialStatus) {
        this.credentialStatus = credentialStatus;
    }

    public Optional<StringFilter> optionalCredentialStatus() {
        return Optional.ofNullable(credentialStatus);
    }

    public StringFilter credentialStatus() {
        if (credentialStatus == null) {
            setCredentialStatus(new StringFilter());
        }
        return credentialStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final VCCredentialCriteria that = (VCCredentialCriteria) o;
        return (Objects.equals(credentialType, that.credentialStatus) && Objects.equals(credentialStatus, that.credentialStatus));
    }

    @Override
    public VCCredentialCriteria copy() {
        return new VCCredentialCriteria(this);
    }
}
