package gov.moda.dw.manager.service.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.outside.vdr.category.CategoryVdrResp;
import gov.moda.dw.manager.domain.outside.vdr.category.CategoryVdrSimpleDto;
import gov.moda.dw.manager.domain.outside.vdr.category.Org;
import gov.moda.dw.manager.type.OrgDataType;

@Service
@Transactional
public class CustomCategoryService {

    @Value("${custom.inSandbox}")
    private Boolean inSandbox;

    private final RemoteApiService remoteApiService;

    private final CustomVdrHelperService customVdrHelperService;

    public CustomCategoryService(RemoteApiService remoteApiService, CustomVdrHelperService customVdrHelperService) {
        this.remoteApiService = remoteApiService;
        this.customVdrHelperService = customVdrHelperService;
    }

    public CategoryVdrResp getAllCategory() {
        return remoteApiService.getCategory();
    }

    public List<CategoryVdrSimpleDto> getFromVDRStatus1ToDto() {
        return customVdrHelperService.getCategoryList();
    }

    /**
     *
     * @return Map{ key: did , value : metaUrl }
     */
    public Map<String, String> getAllCategoryToDtoMap() {
        CategoryVdrResp categoryRespFromVDR = remoteApiService.getCategory();
        Map<String, String> didToMetaUrlMap = new HashMap<>();
        categoryRespFromVDR.getData().getDids().stream()
                .filter(issuer -> (StringUtils.equals(OrgDataType.ISSUER.getType(), issuer.getOrgType())
                        && issuer.getStatus() == 1L && (issuer.getOrg().getIssuerMetadataBaseURL() != null
                                || issuer.getOrg().getServiceBaseURL() != null)))
                .forEach(issuer -> {
                    String metaUrl = null;
                    Org org = issuer.getOrg();
                    if (null != org) {
                        if (StringUtils.isNotBlank(org.getIssuerMetadataBaseURL())) {
                            // 舊版本 metadata url
                            metaUrl = org.getIssuerMetadataBaseURL();
                        } else if (StringUtils.isNotBlank(org.getServiceBaseURL())) {
                            // 新版本 metadata url
                            metaUrl = org.getServiceBaseURL() + "/oid4vci";
                        }
                    }

                    didToMetaUrlMap.put(org.getTaxId(), metaUrl);
                });
        return didToMetaUrlMap;
    }
}
