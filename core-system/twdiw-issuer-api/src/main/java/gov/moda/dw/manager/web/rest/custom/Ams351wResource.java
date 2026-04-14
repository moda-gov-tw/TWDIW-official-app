package gov.moda.dw.manager.web.rest.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gov.moda.dw.manager.domain.AccessToken;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.custom.Ams351wService;
import gov.moda.dw.manager.service.custom.MenuTreeService;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wAccessTokenResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wRelReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wResReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wResResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wSearchAllTokenResultDTO;
import gov.moda.dw.manager.service.dto.custom.MenuTreeDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.type.AuthorityAction;
import gov.moda.dw.manager.type.RelType;
import gov.moda.dw.manager.type.ResType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.DynamicDtoUtils;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing {@link AccessToken}.
 */
@RestController
@RequestMapping("/api")
public class Ams351wResource {

    private final DynamicDtoUtils dynamicDtoUtils;

    private final Logger log = LoggerFactory.getLogger(Ams351wResource.class);

    private final Ams351wService ams351wService;

    private final MenuTreeService menuTreeService;

    public Ams351wResource(DynamicDtoUtils dynamicDtoUtils, Ams351wService ams351wService, MenuTreeService menuTreeService) {
        this.dynamicDtoUtils = dynamicDtoUtils;
        this.ams351wService = ams351wService;
        this.menuTreeService = menuTreeService;
    }

    /**
     * 取得所有AccessToken清單by搜尋條件。
     */
    @PostMapping("/modadw351w")
    @PreAuthorize("hasAuthority('ams351w_s')")
    public ResponseEntity<ResponseDTO<List<Map<String, Object>>>> findAccessTokenListByConditions(
        Pageable pageable,
        @RequestParam(required = false) List<String> fields,
        @RequestBody Ams351wReqDTO ams351WReqDTO
    ) {
        log.debug("[查詢AccessToken]/api/modadw351w");
        try {
            // 查詢所有accessTokenBy條件。
            Ams351wSearchAllTokenResultDTO resultDTO = this.ams351wService.getAllAccessToken(ams351WReqDTO, pageable);
            if (resultDTO.getStatusCode().equals(StatusCode.SUCCESS)) {
                Page<Ams351wAccessTokenResDTO> page = resultDTO.getAms351wAccessTokenResDTOPage();
                List<Ams351wAccessTokenResDTO> resDTOList = page.getContent();

                List<Map<String, Object>> dtoMaps;
                //將DTO 根據 Req 指定的欄位轉換成 Map
                if (fields == null || fields.size() < 2) {
                    dtoMaps = dynamicDtoUtils.dtoToMapList(resDTOList);
                } else {
                    fields.add("dataRole1");
                    dtoMaps = dynamicDtoUtils.dtoToMapList(resDTOList, fields);
                }

                // 將分頁訊息放在Headers。
                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                return ResponseEntity.ok().headers(headers).body(new ResponseDTO<>(dtoMaps));
            } else {
                return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getStatusCode()));
            }
        } catch (Exception ex) {
            log.error("Ams351wResource-findAccessTokenListByConditions-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_SEARCH_DB_EXCEPTION));
    }

    /**
     * {@code POST /modadw351w} : Create a new accessToken.
     * 建立AccessToken。
     */
    @PostMapping("/modadw351w/create")
    @PreAuthorize("hasAuthority('ams351w_c')")
    public ResponseEntity<ResponseDTO> createAccessToken(@RequestBody Ams351wReqDTO ams351wReqDTO) {
        log.debug("[建立AccessToken]/api/modadw351w/create");
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams351wResource-createAccessToken-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_ADMIN_USER_ERROR));
        }
        try {
            Ams351wResultDTO resultDTO = this.ams351wService.upsertAccessToken(ams351wReqDTO, AuthorityAction.ACCESS_TOKEN_CREATE);
            if (resultDTO != null) {
                return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getStatusCode()));
            }
        } catch (Exception ex) {
            log.error("Ams351wResource-createAccessToken-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_CREATE_EXCEPTION));
    }

    /**
     * 更新AccessToken。
     */
    @PostMapping("/modadw351w/update")
    @PreAuthorize("hasAuthority('ams351w_e')")
    public ResponseEntity<ResponseDTO<Ams351wAccessTokenResDTO>> updateAccessToken(@RequestBody Ams351wReqDTO ams351wReqDTO) {
        log.debug("[更新AccessToken]/api/modadw351w/update");
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams351wResource-updateAccessToken-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_ADMIN_USER_ERROR));
        }
        try {
            Ams351wResultDTO resultDTO = this.ams351wService.upsertAccessToken(ams351wReqDTO, AuthorityAction.ACCESS_TOKEN_UPDATE_RES);
            if (resultDTO != null) {
                if (StatusCode.SUCCESS.getCode().equals(resultDTO.getStatusCode().getCode())) {
                    return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getAms351wAccessTokenResDTO()));
                }
                return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getStatusCode()));
            }
        } catch (Exception ex) {
            log.error("Ams351wResource-updateAccessToken-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_CREATE_EXCEPTION));
    }

    /**
     * 變更AccessToken啟停狀態。
     */
    @PostMapping("/modadw351w/changeState")
    @PreAuthorize("hasAuthority('ams351w_upState')")
    public ResponseEntity<ResponseDTO<Ams351wAccessTokenResDTO>> changeAccessTokenState(@RequestBody Ams351wReqDTO ams351wReqDTO) {
        log.debug("[變更AccessToken啟停狀態]/api/modadw351w/changeState");
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams351wResource-updateAccessTokenState-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_ADMIN_USER_ERROR));
        }
        try {
            Ams351wResultDTO resultDTO =
                this.ams351wService.upsertAccessToken(ams351wReqDTO, AuthorityAction.ACCESS_TOKEN_CHANGE_ACTIVATED);
            if (resultDTO != null) {
                if (StatusCode.SUCCESS.getCode().equals(resultDTO.getStatusCode().getCode())) {
                    return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getAms351wAccessTokenResDTO()));
                }
                return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getStatusCode()));
            }
        } catch (Exception ex) {
            log.error("Ams351wResource-updateAccessTokenState-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_CREATE_EXCEPTION));
    }

    /**
     * 取得功能授權左右樹狀圖顯示內容。
     */
    @PostMapping("/modadw351w/searchResTree")
    @PreAuthorize("hasAuthority('ams351w_conferRes')")
    public ResponseEntity<ResponseDTO<MenuTreeDTO>> searchResTree(@RequestBody Ams351wReqDTO request) {
        log.debug("[取得功能授權左右樹狀圖顯示內容]/api/modadw351w/searchResTree");
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams351wResource-searchResTree-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_ADMIN_USER_ERROR));
        }
        try {
            //檢查必填欄位
            if (request.getId() == null) {
                return ResponseEntity.ok().body((new ResponseDTO<>(StatusCode.TOKEN_INVALID_ID)));
            }
            MenuTreeDTO treeDTO = this.menuTreeService.readTree(request.getId(), RelType.ACCESSTOKENTORES, ResType.API);
            return ResponseEntity.ok().body(new ResponseDTO<>(treeDTO));
        } catch (Exception ex) {
            log.error("Ams351wResource-searchResTree-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_SEARCH_DB_EXCEPTION));
        }
    }

    /**
     * AccessToken更新授權功能。
     */
    @PostMapping("/modadw351w/saveRel")
    @PreAuthorize("hasAuthority('ams351w_conferRes')")
    public ResponseEntity<ResponseDTO> upsertRel(@RequestBody Ams351wRelReqDTO ams351wRelReqDTO) {
        log.debug("[AccessToken更新授權功能]/api/modadw351w/saveRel");
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams351wResource-upsertRel-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_ADMIN_USER_ERROR));
        }
        try {
            StatusCode result = ams351wService.updateRel(ams351wRelReqDTO, AuthorityAction.ACCESS_TOKEN_UPDATE_RES);
            if (result != null) {
                return ResponseEntity.ok().body(new ResponseDTO<>(result));
            }
        } catch (Exception ex) {
            log.error("Ams351wResource-upsertRel-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_SEARCH_DB_EXCEPTION));
    }

    /**
     * 取得AccessToken所擁有的功能權限清單。
     */
    @PostMapping("/modadw351w/getDetail/res")
    @PreAuthorize("hasAuthority('ams351w_hasRes')")
    public ResponseEntity<ResponseDTO<List<ResDTO>>> getAccessTokenResList(@RequestBody Ams351wResReqDTO request) {
        log.debug("[取得AccessToken所擁有的功能權限清單]/api/modadw351w/getDetail/res");
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams351wResource-getAccessTokenAllRes-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_ADMIN_USER_ERROR));
        }
        try {
            if (request.getId() == null) {
                log.error("Ams351wResource-getAccessTokenAllRes-發生錯誤，AccessTokenID不得為空");
                return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_ID_NOT_EXISTS));
            }

            Ams351wResResultDTO resultDTO = this.ams351wService.getAccessTokenAllRes(request);
            if (resultDTO.getStatusCode().equals(StatusCode.SUCCESS)) {
                return ResponseEntity.ok()
                    .body(new ResponseDTO<>(resultDTO.getResDTOList() == null ? new ArrayList<>() : resultDTO.getResDTOList()));
            } else {
                return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getStatusCode()));
            }
        } catch (Exception ex) {
            log.error("Ams351wResource-getAccessTokenAllRes-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_SEARCH_DB_EXCEPTION));
        }
    }

    /**
     * 刪除AccessToken。
     */
    @DeleteMapping("/modadw351w")
    @PreAuthorize("hasAuthority('ams351w_d')")
    public ResponseEntity<ResponseDTO> deleteAccessTokens(@RequestBody List<Long> listId) {
        log.debug("刪除AccessToken]/api/modadw351w");
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams351wResource-deleteAccessTokens-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_ADMIN_USER_ERROR));
        }
        try {
            StatusCode result = this.ams351wService.delete(listId);
            return ResponseEntity.ok().body(new ResponseDTO<>(result));
        } catch (Exception ex) {
            log.error("Ams351wResource-deleteAccessTokens-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.TOKEN_DELETE_EXCEPTION));
        }
    }
}
