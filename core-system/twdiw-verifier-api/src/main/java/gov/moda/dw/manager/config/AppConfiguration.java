package gov.moda.dw.manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/**
 * 讀取 yml 配置
 */
@Getter
@Configuration
public class AppConfiguration {

    /**
     * Jwt 有效時間(秒)
     */
    @Value("${time.jwtTimeout}")
    private int jwtTimeout;

    /**
     * Totp 有效時間(秒)
     */
    @Value("${time.totpTimeout}")
    private int totpTimeout;

    /**
     * Totp 時間偏移量(秒)
     */
    @Value("${time.totpTimeOffset}")
    private int totpTimeOffset;

    /**
     * Jwt 驗簽開關
     */
    @Value("${switch.verify.jwt}")
    private boolean switchVerifyJwt;

}
