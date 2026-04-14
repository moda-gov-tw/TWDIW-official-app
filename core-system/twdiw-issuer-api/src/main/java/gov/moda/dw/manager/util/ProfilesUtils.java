package gov.moda.dw.manager.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProfilesUtils {

    public static String serverStatic;

    /**
     * 靜態方式取得profiles參數。
     * @param server
     */
    @Value("${spring.profiles.active}")
    public void setServerStatic(String server) {
        ProfilesUtils.serverStatic = server;
    }
}
