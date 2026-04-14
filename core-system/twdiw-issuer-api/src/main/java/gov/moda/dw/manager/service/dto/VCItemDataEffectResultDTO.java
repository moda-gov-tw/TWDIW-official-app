package gov.moda.dw.manager.service.dto;

import java.util.List;

public class VCItemDataEffectResultDTO {

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<VCItemDataEffectDTO> getVcItemDataEffectDTOList() {
        return vcItemDataEffectDTOList;
    }

    public void setVcItemDataEffectDTOList(List<VCItemDataEffectDTO> vcItemDataEffectDTOList) {
        this.vcItemDataEffectDTOList = vcItemDataEffectDTOList;
    }

    Integer pageSize;
    Integer pageNumber;
    Integer totalPages;
    List<VCItemDataEffectDTO> vcItemDataEffectDTOList;
}
