package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.outside.vdr.category.CategoryVdrResp;
import gov.moda.dw.manager.domain.outside.vdr.category.CategoryVdrSimpleDto;
import gov.moda.dw.manager.domain.outside.vdr.category.SelectCategoryReq;
import gov.moda.dw.manager.repository.outside.VcManagerOrgRepository;
import gov.moda.dw.manager.type.OrgDataType;
import gov.moda.dw.manager.type.OrgType;
import lombok.Getter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class CustomVdrHelperService {

    @Getter
    @Value("${custom.inSandbox}")
    private Boolean inSandbox;

    private final RemoteApiService remoteApiService;

    private final VcManagerOrgRepository vcManagerOrgRepository;

    public CustomVdrHelperService(RemoteApiService remoteApiService, VcManagerOrgRepository vcManagerOrgRepository) {
        this.remoteApiService = remoteApiService;
        this.vcManagerOrgRepository = vcManagerOrgRepository;
    }

    public List<CategoryVdrSimpleDto> getCategoryList() {
        CategoryVdrResp category = remoteApiService.getCategory();
        List<CategoryVdrSimpleDto> result = null;
        if (inSandbox) {
            result = vcManagerOrgRepository.findAll().stream()
                    .filter(e -> !OrgType.DEFAULT_ORG.getCode().equals(e.getOrgId()))
                    .map(CategoryVdrSimpleDto::valueOf)
                    .toList();
        } else {
            result = category.getData().getDids().stream()
                    .filter(e -> (StringUtils.equals(OrgDataType.ISSUER.getType(), e.getOrgType())
                            && e.getStatus() == 1L
                            && (e.getOrg().getIssuerMetadataBaseURL() != null
                                    || e.getOrg().getServiceBaseURL() != null)))
                    .map(CategoryVdrSimpleDto::valueOf).toList();
        }

        return result;
    }

    public String getVdrUrlFactory(SelectCategoryReq req, CustomVCItemFieldsVdrService service) {
        return service.findVcItemsFromVdrInNormal(req.getTaxId());
    }

}
