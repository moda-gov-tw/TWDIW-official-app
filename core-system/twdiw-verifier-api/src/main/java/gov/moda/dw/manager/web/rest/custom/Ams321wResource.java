package gov.moda.dw.manager.web.rest.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import gov.moda.dw.manager.service.criteria.RoleCriteria;
import gov.moda.dw.manager.service.custom.Ams321wService;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wRoleConferReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wRoleConferResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wRoleConferRultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wRoleResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wTreeNodeDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wUpdateResultDTO;
import gov.moda.dw.manager.service.dto.custom.MenuTreeDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.DynamicDtoUtils;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.web.util.PaginationUtil;

@Slf4j
@RestController
@RequestMapping("/api")
public class Ams321wResource {

    private static final String ENTITY_NAME = "Ams321wResource";

    private final Ams321wService ams321wService;
    private final DynamicDtoUtils dynamicDtoUtils;

    public Ams321wResource(Ams321wService ams321wService, DynamicDtoUtils dynamicDtoUtils) {
        this.ams321wService = ams321wService;
        this.dynamicDtoUtils = dynamicDtoUtils;
    }

    /**
     * 角色管理 - 查詢所有角色
     *
     * @param pageable
     * @param request
     * @return
     */
    @PostMapping("/modadw321w/search")
    @PreAuthorize("hasAuthority('ams321w_s')")
    public ResponseEntity<ResponseDTO<List<Ams321wRoleResDTO>>> searchAllRole(Pageable pageable, @RequestBody Ams321wReqDTO request) {
        log.info("Ams321wResource-searchAllRole 進入查詢功能 request={}", request);
        ResponseDTO<List<Ams321wRoleResDTO>> responseDTO = new ResponseDTO<>();
        try {
            //查詢角色
            Page<RoleDTO> page = ams321wService.getAllRole(request, pageable);
            if (page != null) {
                // 回傳的查詢內容
                List<RoleDTO> roleDTOList = page.getContent();

                //整理要回傳的資料
                List<Ams321wRoleResDTO> resultRoleList = new ArrayList<>();
                for (RoleDTO roleDTO : roleDTOList) {
                    Ams321wRoleResDTO resultRole = this.saveToResultRoleDTO(roleDTO);
                    resultRoleList.add(resultRole);
                }

                responseDTO.setStatusCode(StatusCode.SUCCESS);
                responseDTO.setData(resultRoleList);

                // 將分頁訊息放在Headers
                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                return ResponseEntity.ok().headers(headers).body(responseDTO);
            } else {
                log.warn("Ams321wResource-searchAllRole 查詢角色列回傳null request={}", request);
                responseDTO.setStatusCode(StatusCode.ROLE_SEARCH_EXCEPTION);
                return ResponseEntity.ok().body(responseDTO);
            }
        } catch (Exception ex) {
            log.error("Ams321wResource-searchAllRole 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(responseDTO);
        }
    }

    @GetMapping("/modadw321w/roles")
    @PreAuthorize("hasAuthority('ams321w_s')")
    public ResponseEntity<Page<Map<String, Object>>> getRoles(
        RoleCriteria criteria,
        Pageable pageable,
        @RequestParam(required = false) List<String> fields
    ) {
        log.info("[{}] 進入查詢功能 ", ENTITY_NAME);
        Page<RoleDTO> page = ams321wService.searchRoles(criteria, pageable);

        Page<Map<String, Object>> body = fields == null || fields.isEmpty()
            ? page.map(dynamicDtoUtils::dtoToMap)
            : page.map(receiptDTO -> dynamicDtoUtils.dtoToMap(receiptDTO, fields));

        return ResponseEntity.ok(body);
    }

    /**
     * 角色管理 - 更新角色啟停狀態
     *
     * @param request
     * @return
     */
    @PostMapping("/modadw321w/updateState")
    @PreAuthorize("hasAuthority('ams321w_upState')")
    public ResponseDTO<Ams321wRoleResDTO> setState(@RequestBody Ams321wReqDTO request) {
        log.info("Ams321wResource-setState 進入角色啟停狀態更改 request={}", request);
        ResponseDTO<Ams321wRoleResDTO> responseDTO = new ResponseDTO<>();
        try {
            //檢查傳入的值
            if (request == null || request.getId() == null) {
                log.info("Ams321wResource-setState 缺少必要參數 request={}", request);
                responseDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                return responseDTO;
            }

            //更新啟停狀態
            Ams321wUpdateResultDTO ams321WRoleResponseDTO = ams321wService.updateState(request);
            if (ams321WRoleResponseDTO != null) {
                if (ams321WRoleResponseDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams321WRoleResponseDTO.getAms321wRoleResDTO());
                }
                responseDTO.setStatusCode(ams321WRoleResponseDTO.getStatusCode());
            }

            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams321wResource-setState 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 角色管理 - 新增角色
     *
     * @param request
     * @return
     */
    @PostMapping("/modadw321w/create")
    @PreAuthorize("hasAuthority('ams321w_c')")
    public ResponseDTO createRole(@RequestBody Ams321wReqDTO request) {
        log.info("Ams321wResource-createRole 進入新增角色 request={}", request);
        ResponseDTO responseDTO = new ResponseDTO<>();
        try {
            //檢查傳入的值
            if (request == null || request.getCategoryId() == null || request.getCategoryName() == null || request.getState() == null) {
                responseDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                log.warn("Ams321wService-createRole 缺少必要參數 responseDTO={}", responseDTO);
                return responseDTO;
            }

            //新增角色
            StatusCode statusCode = ams321wService.createRole(request);
            responseDTO.setStatusCode(statusCode);

            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams321wResource-createRole 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 角色管理 - 編輯角色
     *
     * @param request
     * @return
     */
    @PostMapping("/modadw321w/update")
    @PreAuthorize("hasAuthority('ams321w_e')")
    public ResponseDTO<Ams321wRoleResDTO> updateRole(@RequestBody Ams321wReqDTO request) {
        log.info("Ams321wResource-updateRole 進入修改角色 request={}", request);
        ResponseDTO<Ams321wRoleResDTO> responseDTO = new ResponseDTO<>();
        try {
            //檢查傳入的值
            if (request == null || request.getId() == null || request.getCategoryName() == null || request.getState() == null) {
                log.warn("Ams321wService-updateRole 缺少必要參數 request={}", request);
                responseDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                return responseDTO;
            }

            //更新角色
            Ams321wUpdateResultDTO ams321wRoleResponseDTO = ams321wService.updateRole(request);
            if (ams321wRoleResponseDTO != null) {
                if (ams321wRoleResponseDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams321wRoleResponseDTO.getAms321wRoleResDTO());
                }
                responseDTO.setStatusCode(ams321wRoleResponseDTO.getStatusCode());
            }

            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams321wResource-updateRole 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 角色管理 - 刪除角色
     *
     * @return
     */
    @PostMapping("/modadw321w/delete")
    @PreAuthorize("hasAuthority('ams321w_d')")
    public ResponseDTO deleteRole(@RequestBody Ams321wReqDTO request) {
        log.info("Ams321wResource-deleteRole 進入刪除角色 request={}", request);
        ResponseDTO responseDTO = new ResponseDTO<>();
        try {
            //檢查必填欄位
            if (request == null || request.getId() == null) {
                responseDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                log.warn("Ams321wService-deleteRole 缺少必要參數 responseDTO={}", responseDTO);
                return responseDTO;
            }

            //刪除角色
            StatusCode statusCode = ams321wService.deleteRole(request);
            responseDTO.setStatusCode(statusCode);

            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams321wResource-deleteRole 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 角色管理 - 功能資訊
     * @return
     */
    @PostMapping("/modadw321w/searchConferRes")
    @PreAuthorize("hasAuthority('ams321w_hasRes')")
    public ResponseDTO<List<Ams321wRoleConferResDTO>> getRoleAllRes(@RequestBody Ams321wReqDTO request) {
        log.info("Ams321wResource-getRoleAllRes 載入角色所擁有的功能權限");
        ResponseDTO<List<Ams321wRoleConferResDTO>> responseDTO = new ResponseDTO<>();
        try {
            // 檢查必填欄位
            if (request == null || request.getId() == null) {
                responseDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                log.warn("Ams321wResource-getRoleAllRes 缺少必要參數 request={}", request);
                return responseDTO;
            }

            // 查詢角色已授權功能列表
            Ams321wRoleConferRultDTO ams321wRoleConferResDTO = ams321wService.searchRoleConferRes(request);
            if (ams321wRoleConferResDTO != null) {
                if (ams321wRoleConferResDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams321wRoleConferResDTO.getAms321wRoleResResponseDTOList());
                }
                responseDTO.setStatusCode(ams321wRoleConferResDTO.getStatusCode());
            }

            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams321wResource-getRoleAllRes 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 角色管理 - 授權功能
     * @param ams321wRelDTO
     * @return
     */
    @PostMapping("/modadw321w/saveConferRes")
    @PreAuthorize("hasAuthority('ams321w_conferRes')")
    public ResponseDTO<List<Ams321wRoleConferResDTO>> saveConferRes(@RequestBody Ams321wRoleConferReqDTO ams321wRelDTO) {
        log.info("Ams321wResource-saveConferRes 準備儲存角色授權的功能");
        ResponseDTO<List<Ams321wRoleConferResDTO>> responseDTO = new ResponseDTO<>();
        try {
            Ams321wRoleConferRultDTO ams321wRoleConferResDTO = ams321wService.saveConferRes(ams321wRelDTO);
            if (ams321wRoleConferResDTO != null) {
                if (ams321wRoleConferResDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams321wRoleConferResDTO.getAms321wRoleResResponseDTOList());
                }
                responseDTO.setStatusCode(ams321wRoleConferResDTO.getStatusCode());
            }

            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams321wResource-saveConferRes 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 角色管理 - 功能授權(列出web類型所有啟用功能的樹結構)
     * @return
     */
    @PostMapping("/modadw321w/searchResTree")
    @PreAuthorize("hasAuthority('ams321w_conferRes')")
    public ResponseDTO<MenuTreeDTO> searchResTree(@RequestBody Ams321wReqDTO request) {
        log.info("Ams321wResource-searchResTree 載入角色功能授權內容");
        ResponseDTO<MenuTreeDTO> responseDTO = new ResponseDTO<>();
        try {
            // 檢查必填欄位
            if (request == null || request.getId() == null) {
                responseDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                log.warn("Ams321wResource-searchResTree 缺少必要參數 request={}", request);
                return responseDTO;
            }

            // 產生樹結構內容
            Ams321wTreeNodeDTO ams321wTreeNodeDTO = ams321wService.getTree(request);
            if (ams321wTreeNodeDTO != null) {
                if (ams321wTreeNodeDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams321wTreeNodeDTO.getMenuTreeDTO());
                }
                responseDTO.setStatusCode(ams321wTreeNodeDTO.getStatusCode());
            }

            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams321wResource-searchResTree 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    private Ams321wRoleResDTO saveToResultRoleDTO(RoleDTO roleDTO) {
        Ams321wRoleResDTO ams321wSearchAllResDTO = new Ams321wRoleResDTO();
        ams321wSearchAllResDTO.setId(roleDTO.getId());
        ams321wSearchAllResDTO.setRoleId(roleDTO.getRoleId());
        ams321wSearchAllResDTO.setRoleName(roleDTO.getRoleName());
        ams321wSearchAllResDTO.setDescription(roleDTO.getDescription());
        ams321wSearchAllResDTO.setState(roleDTO.getState());
        ams321wSearchAllResDTO.setCreateTime(roleDTO.getCreateTime());

        return ams321wSearchAllResDTO;
    }
}
