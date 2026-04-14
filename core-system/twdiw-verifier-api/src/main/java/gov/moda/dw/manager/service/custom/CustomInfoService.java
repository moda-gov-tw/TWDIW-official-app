package gov.moda.dw.manager.service.custom;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import gov.moda.dw.manager.service.dto.custom.CustomVersionInfoDetailResDTO;
import gov.moda.dw.manager.service.dto.custom.CustomVersionInfoResDTO;
import gov.moda.dw.manager.type.SystemCode;

@Service
public class CustomInfoService {

    // 記錄器，用於記錄系統日誌
    private static final Logger log = LoggerFactory.getLogger(CustomInfoService.class);

    // 遠端API服務
    private final RemoteApiService remoteApiService;

    private final InfoEndpoint infoEndpoint;

    // 取得當前環境
    @Value("${spring.profiles.active:}")
    private String env;

    // 取得 APP 更新日期
    @Value("${appDownloadDate}")
    private String appDownloadDate;

    /**
     * 建構子，注入所需的服務和資料庫存取介面
     */
    public CustomInfoService(RemoteApiService remoteApiService, InfoEndpoint infoEndpoint) {
        this.remoteApiService = remoteApiService;
        this.infoEndpoint = infoEndpoint;
    }

    /**
     * 取得各系統 management info 資料
     * 
     * @return
     */
    public CustomVersionInfoResDTO getVersionInfo() {
        CustomVersionInfoResDTO customVersionInfoResDTO = new CustomVersionInfoResDTO();

        // 取得 VERIFIERMGR 環境資訊
        CustomVersionInfoDetailResDTO verifiermgrVersionInfoDetailResDTO = getVersionInfoDetail(SystemCode.VERIFIERMGR);
        customVersionInfoResDTO.setVerifiermgr(verifiermgrVersionInfoDetailResDTO);

        // 取得 OID4P 環境資訊
        CustomVersionInfoDetailResDTO oid4pVersionInfoDetailResDTO = getVersionInfoDetail(SystemCode.OID4P);
        customVersionInfoResDTO.setOid4vp(oid4pVersionInfoDetailResDTO);

        // 取得 VP 環境資訊
        CustomVersionInfoDetailResDTO vpVersionInfoDetailResDTO = getVersionInfoDetail(SystemCode.VP);
        customVersionInfoResDTO.setVp(vpVersionInfoDetailResDTO);

        // 取得當前環境
        customVersionInfoResDTO.setEnv(env);

        // 取得 APP 更新日期
        customVersionInfoResDTO.setAppDownloadDate(appDownloadDate);

        return customVersionInfoResDTO;
    }
    
    /**
     * 取得操作手冊
     * 
     * @return
     */
    public byte[] getManualFileBytes() {
        byte[] fileBytes = remoteApiService.getManualFileBytes();
        log.info("取得操作手冊成功，檔案大小：{} bytes", fileBytes.length);

        return fileBytes;
    }

    /**
     * 取得各系統 management info 資料細項
     * 
     * @param systemCode
     * @return
     */
    private CustomVersionInfoDetailResDTO getVersionInfoDetail(SystemCode systemCode) {
        CustomVersionInfoDetailResDTO customVersionInfoDetailResDTO = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String versionInfo = "";

        try {
            if (systemCode == SystemCode.VERIFIERMGR) {
                Map<String,Object> info = infoEndpoint.info();
                objectMapper.registerModule(new JavaTimeModule());
                versionInfo = objectMapper.writeValueAsString(info);
            } else {
                versionInfo = remoteApiService.getVersionInfo(systemCode);
            }

            customVersionInfoDetailResDTO = new CustomVersionInfoDetailResDTO();
            if (StringUtils.isNotBlank(versionInfo)) {
                JsonNode node = objectMapper.readTree(versionInfo);
                JsonNode buildNode = node.path("build");
                customVersionInfoDetailResDTO.setVersion(buildNode.path("version").asText(null));
                customVersionInfoDetailResDTO.setApiVersion(buildNode.path("api-version").asText(null));
                customVersionInfoDetailResDTO.setAbbrev(node.path("git").path("commit").path("id").path("abbrev").asText(null));
                log.info("已取得 {} 版本資料", systemCode.getName());
            }
        } catch (Exception e) {
            log.warn("取得 {} 系統 management info 發生錯誤，原因為:{}", systemCode.getName(), ExceptionUtils.getStackTrace(e));
        }

        return customVersionInfoDetailResDTO;
    }
}
