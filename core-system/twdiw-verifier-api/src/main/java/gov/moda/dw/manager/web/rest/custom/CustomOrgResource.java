package gov.moda.dw.manager.web.rest.custom;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.moda.dw.manager.service.OrgQueryService;
import gov.moda.dw.manager.service.custom.Ams311wService;
import gov.moda.dw.manager.service.custom.CustomOrgService;
import gov.moda.dw.manager.service.dto.OrgDTO;
import gov.moda.dw.manager.service.dto.OrgTitleResDTO;
import gov.moda.dw.manager.service.dto.custom.OrgReqDTO;
import gov.moda.dw.manager.service.dto.custom.OrgResDTO;
import gov.moda.dw.manager.service.dto.custom.OrgUpdateResultDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.type.StatusCode;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.web.util.PaginationUtil;

@Slf4j
@RestController
@RequestMapping("/api")
public class CustomOrgResource {

    @Autowired
    private CustomOrgService customOrgService;

    @Autowired
    Ams311wService ams311wService;

    @Qualifier("orgQueryService")
    @Autowired
    private OrgQueryService orgQueryService;

    /**
     * 組織管理 - 查詢組織
     * 
     * @param pageable
     * @param orgId
     * @param orgTwName
     * @param orgEnName
     * @param createTimeFrom
     * @param createTimeTo
     * @return
     */
    @GetMapping("/modadworg/search")
    @PreAuthorize("hasAuthority('org_s')")
    public ResponseEntity<ResponseDTO<List<OrgResDTO>>> searchAllOrg(
        Pageable pageable,
        @RequestParam(required = false) String orgId,
        @RequestParam(required = false) String orgTwName,
        @RequestParam(required = false) String orgEnName,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createTimeFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createTimeTo) {
        log.debug("[查詢組織]/api/modadworg/search");
        ResponseDTO<List<OrgResDTO>> responseDTO = new ResponseDTO<>();
        try {
            OrgReqDTO request = new OrgReqDTO();
            request.setOrgId(orgId);
            request.setOrgTwName(orgTwName);
            request.setOrgEnName(orgEnName);

            request.setCreateTimeFrom(createTimeFrom);
            request.setCreateTimeTo(createTimeTo);
            //查詢組織
            Page<OrgDTO> page = customOrgService.getAllOrg(request, pageable);
            if (page != null) {
                // 回傳的查詢內容
                List<OrgDTO> orgDTOList = page.getContent();

                //整理要回傳的資料
                List<OrgResDTO> resultOrgList = new ArrayList<>();
                for (OrgDTO orgDTO : orgDTOList) {
                    OrgResDTO resultOrg = this.saveToResultOrgDTO(orgDTO);
                    resultOrgList.add(resultOrg);
                }

                responseDTO.setStatusCode(StatusCode.SUCCESS);
                responseDTO.setData(resultOrgList);

                // 將分頁訊息放在Headers
                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                return ResponseEntity.ok().headers(headers).body(responseDTO);
            } else {
                log.warn("CustomOrgResource-searchAllOrg 查詢組織列回傳 null");
                responseDTO.setStatusCode(StatusCode.ROLE_SEARCH_EXCEPTION);
                return ResponseEntity.ok().body(responseDTO);
            }
        } catch (Exception ex) {
            log.error("CustomOrgResource-searchAllOrg 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(responseDTO);
        }
    }

    /**
     * 組織管理 - 新增組織
     *
     * @param request
     * @return
     */
    @PostMapping("/modadworg/create")
    @PreAuthorize("hasAuthority('org_a')")
    public ResponseDTO createOrg(@RequestBody OrgReqDTO request) {
        log.debug("[新增組織]/api/modadworg/create");
        ResponseDTO responseDTO = new ResponseDTO<>();
        try {
            //檢查傳入的值
            if (request == null || request.getOrgId() == null || request.getOrgTwName() == null || request.getOrgEnName() == null) {
                responseDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                log.warn("CustomOrgResource-createOrg 缺少必要參數 responseDTO={}", responseDTO);
                return responseDTO;
            }

            if(
                !request.getOrgId().matches("^[0-9-]+$") ||
                !request.getOrgTwName().matches("^[\\u4E00-\\u9FFF\\u3400-\\u4DBFa-zA-Z0-9.-]+$") || request.getOrgTwName().length() > 30 ||
                !request.getOrgEnName().matches("^([a-zA-Z0-9.,&\\s-]+)$") || request.getOrgEnName().length() > 100
            ) {
                responseDTO.setStatusCode(StatusCode.ORG_PARAM_INVALID);
                log.warn("CustomOrgResource-createOrg 參數錯誤 responseDTO={}", responseDTO);
                return responseDTO;
            }

            //新增組織
            StatusCode statusCode = customOrgService.createOrg(request);
            responseDTO.setStatusCode(statusCode);

            return responseDTO;
        } catch (Exception ex) {
            log.error("CustomOrgResource-createOrg 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 組織管理 - 編輯組織
     *
     * @param request
     * @return
     */
    @PostMapping("/modadworg/update")
    @PreAuthorize("hasAuthority('org_c')")
    public ResponseDTO<OrgResDTO> updateOrg(@RequestBody OrgReqDTO request) {
        log.debug("[編輯組織]/api/modadworg/update");
        ResponseDTO<OrgResDTO> responseDTO = new ResponseDTO<>();
        try {
            //檢查傳入的值
            if (
                    request == null ||
                    request.getId() == null ||
                    request.getOrgId() == null ||
                    request.getOrgTwName() == null ||
                    request.getOrgEnName() == null
            ) {
                log.warn("CustomOrgResource-updateOrg 缺少必要參數");
                responseDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                return responseDTO;
            }

            if(
                !request.getOrgId().matches("^[0-9-]+$") ||
                !request.getOrgTwName().matches("^[\\u4E00-\\u9FFF\\u3400-\\u4DBFa-zA-Z0-9.-]+$") || request.getOrgTwName().length() > 30 ||
                !request.getOrgEnName().matches("^([a-zA-Z0-9.,&\\s-]+)$")
            ) {
                responseDTO.setStatusCode(StatusCode.ORG_PARAM_INVALID);
                log.warn("CustomOrgResource-updateOrg 參數錯誤 responseDTO={}", responseDTO);
                return responseDTO;
            }

            //更新組織
            OrgUpdateResultDTO orgResponseDTO = customOrgService.updateOrg(request);
            if (orgResponseDTO != null) {
                if (orgResponseDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(orgResponseDTO.getOrgResDTO());
                }
                responseDTO.setStatusCode(orgResponseDTO.getStatusCode());
            }

            return responseDTO;
        } catch (Exception ex) {
            log.error("CustomOrgResource-updateOrg 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 組織管理 - 刪除組織
     *
     * @return
     */
    @PostMapping("/modadworg/delete")
    @PreAuthorize("hasAuthority('org_d')")
    public ResponseDTO deleteOrg(@RequestBody OrgReqDTO request) {
        log.debug("[刪除組織]/api/modadworg/delete");
        ResponseDTO responseDTO = new ResponseDTO<>();
        try {
            //檢查必填欄位
            if (request == null || request.getOrgId() == null) {
                responseDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                log.warn("CustomOrgResource-deleteOrg 缺少必要參數 responseDTO={}", responseDTO);
                return responseDTO;
            }

            //刪除組織
            StatusCode statusCode = customOrgService.deleteOrg(request);
            responseDTO.setStatusCode(statusCode);

            return responseDTO;
        } catch (Exception ex) {
            log.error("CustomOrgResource-deleteOrg 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 依使用者是否為特權帳號，返回所有組織(多筆)或使用者自己的組織(一筆)
     */
    @GetMapping("/modadworg/list")
    public ResponseEntity<ResponseDTO<List<OrgTitleResDTO>>> getOrgList() {
        log.debug("[查詢組織]/api/modadworg/list");
        ResponseDTO<List<OrgTitleResDTO>> responseDTO = new ResponseDTO<>();

        try {
            List<OrgTitleResDTO> result = customOrgService.getOrgList();
            responseDTO.setStatusCode(StatusCode.SUCCESS);
            responseDTO.setData(result);

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception ex) {
            log.error("CustomOrgResource-getOrgList 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(responseDTO);
        }
    }

    private OrgResDTO saveToResultOrgDTO(OrgDTO orgDTO) {
        OrgResDTO orgSearchAllResDTO = new OrgResDTO();
        orgSearchAllResDTO.setId(orgDTO.getId());
        orgSearchAllResDTO.setOrgId(orgDTO.getOrgId());
        orgSearchAllResDTO.setOrgTwName(orgDTO.getOrgTwName());
        orgSearchAllResDTO.setOrgEnName(orgDTO.getOrgEnName());
        orgSearchAllResDTO.setCreateTime(orgDTO.getCreateTime());
        orgSearchAllResDTO.setUpdateTime(orgDTO.getUpdateTime());
        orgSearchAllResDTO.setIsDidOrg(orgDTO.getIsDidOrg());

        return orgSearchAllResDTO;
    }

}
