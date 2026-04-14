package gov.moda.dw.manager.config;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import gov.moda.dw.manager.repository.custom.CustomExtendedUserRepository;
import gov.moda.dw.manager.service.dto.custom.ExtendedUserResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitialApplication implements ApplicationRunner {

    private final CustomExtendedUserRepository customExtendedUserRepository;
    private final AttributeEncryptor attributeEncryptor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 系統啟動時執行 INIT_* 暱稱加密流程
        this.encryptedUserName();
    }

    /**
     * 加密預設資料 user_name (只處理 INIT_ 開頭的資料)
     *
     * 流程說明：
     * 1. 查詢所有 user_name LIKE 'INIT_%' 的使用者
     * 2. 解析 username，取出底線後的值進行加密
     * 3. 將加密後的結果批次更新至資料庫
     */
    private void encryptedUserName() {
        log.info("InitialApplication run encryptedUserName start");

        // 1. 取得所有 INIT_% 開頭的使用者
        List<ExtendedUserResDTO> extendedUserList = customExtendedUserRepository.findByUserName("INIT_%");

        // 若沒有任何符合格式的使用者，則不進行加密
        if (extendedUserList.isEmpty()) {
            log.info("No INIT_* users found. Skip encryption.");
            return;
        }

        log.info("Found {} users to encrypt.", extendedUserList.size());

        // 2. 將每一筆使用者資料轉換為 update 資料（包含 userId 與加密後的 username）
        //    若 username 格式不符合 INIT_xxx，則結果為 null，後續會被過濾掉
        List<UserNameUpdate> updates = extendedUserList.stream()
                .map(this::buildUpdateInfo)
                .filter(Objects::nonNull)
                .toList();

        // 若沒有任何合法的資料可更新，代表資料格式皆不符合 INIT_xxx
        if (updates.isEmpty()) {
            log.warn("No valid users to update (all username formats invalid).");
            return;
        }

        // 3. 執行批次更新
        updates.forEach(u -> customExtendedUserRepository.updateByUserId(u.encryptedUserName(), u.userId()));

        log.info("InitialApplication encryptedUserName finished. Updated {} users.", updates.size());
    }

    /**
     * 解析使用者的原始 user_name 並回傳更新所需資訊
     *
     * @param user ExtendedUserResDTO
     * @return UserNameUpdate（包含 userId 與加密後的 username），若格式不符則回傳 null
     */
    private UserNameUpdate buildUpdateInfo(ExtendedUserResDTO user) {
        String originalUserName = user.getUserName();

        // 使用底線分割，預期格式為 INIT_XXX
        String[] parts = StringUtils.split(originalUserName, "_");

        // 格式不正確 -> 無法取得要加密的部分 -> 不更新此筆
        if (parts.length < 2) {
            log.warn("Invalid INIT_* format. Skip userId={}, userName={}", user.getUserId(), originalUserName);
            return null;
        }

        // parts[1] 為真正需要加密的字串
        String encrypted = attributeEncryptor.convertToDatabaseColumn(parts[1]);

        return new UserNameUpdate(user.getUserId(), encrypted);
    }

    /**
     * 用於包裝批次更新資料
     *
     * @param userId            使用者 ID
     * @param encryptedUserName 加密後的 user_name
     */
    private record UserNameUpdate(String userId, String encryptedUserName) {
    }

}
