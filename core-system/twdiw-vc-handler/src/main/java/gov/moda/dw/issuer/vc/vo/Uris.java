package gov.moda.dw.issuer.vc.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * URIs
 *
 * @version 20240902
 */
@Component
public class Uris {

    private static final Logger LOGGER = LoggerFactory.getLogger(Uris.class);

    private final PreloadSetting preloadSetting;

    private String basePath = "/";

    public Uris(PreloadSetting preloadSetting) {
        this.preloadSetting = preloadSetting;
        init();
    }

    private void init() {
        basePath = preloadSetting.getUrlVcBasePath();
        if (basePath != null) {
            // add slash ("/") to tail
            basePath = basePath.endsWith("/") ? basePath : basePath.concat("/");
        }
    }

    public URI generateCredentialId(String cid) {
        return URI.create(basePath + "api/credential/" + cid);
    }

    public URI generateStatusListId(String credentialType, String groupName) {
        return URI.create(basePath + "api/status-list/" + credentialType + "/" + groupName);
    }

    public URI generateStatusListSubjectId(URI statusListId) {
        return URI.create(statusListId.toString().concat("#list"));
    }

    public URI generateSchemaUri(String schemaName) {
        return URI.create(basePath + "api/schema/" + schemaName);
    }

    public URI generatePublicKeysUri() {
        return URI.create(basePath + "api/keys");
    }

    public static String extractCid(String credentialId) {

        if (credentialId == null || credentialId.trim().isEmpty()) {
            return "";
        }

        if (credentialId.endsWith("/")) {
            credentialId = credentialId.substring(0, credentialId.length() - 1);
        }

        return credentialId.substring(credentialId.lastIndexOf("/") + 1);
    }
}
