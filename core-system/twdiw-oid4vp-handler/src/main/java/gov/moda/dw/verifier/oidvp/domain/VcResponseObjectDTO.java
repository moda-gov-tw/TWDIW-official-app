package gov.moda.dw.verifier.oidvp.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import gov.moda.dw.verifier.oidvp.presentationExchange.handler.PresentationDefinitionHandlerClient.ClaimInfo;
import gov.moda.dw.verifier.oidvp.presentationExchange.handler.PresentationDefinitionHandlerClient.PresentationDefinitionHandlerResult;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class VcResponseObjectDTO {

    private String credentialType;
    private JsonNode claims;

    public VcResponseObjectDTO() {
    }

    public VcResponseObjectDTO(ClaimInfo claimInfo) {
        credentialType = claimInfo.type().get(0);
        claims = claimInfo.claims();
    }

    public static List<VcResponseObjectDTO> toVcResponseObjectDTOs(PresentationDefinitionHandlerResult presentationDefinitionResult) {
        if (presentationDefinitionResult == null || presentationDefinitionResult.getClaimInfos() == null) {
            return null;
        }
        ArrayList<VcResponseObjectDTO> vcResponseObjectDTOs = new ArrayList<>();
        for (ClaimInfo claimInfo : presentationDefinitionResult.getClaimInfos()) {
            vcResponseObjectDTOs.add(new VcResponseObjectDTO(claimInfo));
        }
        return vcResponseObjectDTOs;
    }

    public void setClaims(JsonNode claims) {
        this.claims = claims;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    public JsonNode getClaims() {
        return claims;
    }

    public String getCredentialType() {
        return credentialType;
    }
}
