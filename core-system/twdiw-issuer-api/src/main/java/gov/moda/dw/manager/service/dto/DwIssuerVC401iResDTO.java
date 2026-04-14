package gov.moda.dw.manager.service.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.List;

public class DwIssuerVC401iResDTO {

    public List<DwIssuerVC401iCredentialsResDTO> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<DwIssuerVC401iCredentialsResDTO> credentials) {
        this.credentials = credentials;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    private List<DwIssuerVC401iCredentialsResDTO> credentials;
    private Integer totalPages;
}
