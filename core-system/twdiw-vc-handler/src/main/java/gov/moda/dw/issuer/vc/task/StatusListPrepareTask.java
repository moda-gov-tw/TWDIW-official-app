package gov.moda.dw.issuer.vc.task;

import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jwt.FromJwtConverter;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import gov.moda.dw.issuer.vc.util.DateUtils;
import gov.moda.dw.issuer.vc.util.Tuple;
import gov.moda.dw.issuer.vc.util.ZipUtils;
import gov.moda.dw.issuer.vc.vo.Definition;
import gov.moda.dw.issuer.vc.vo.Definition.StatusListType;
import gov.moda.dw.issuer.vc.vo.Policy;
import gov.moda.dw.issuer.vc.vo.VcException;
import gov.moda.dw.issuer.vc.vo.VcStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * status list prepare task
 *
 * @version 20240902
 */
public class StatusListPrepareTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusListPrepareTask.class);

    private static final String NAME_STATUS_PURPOSE = "statusPurpose";
    private static final String NAME_ENCODED_LIST = "encodedList";

    private final String latestStatusList;
    private final VcStatus vcStatus;
    private final int statusListIndex;

    private final Policy policy;
    private final URI issuerId;
    private final URI statusListId;
    private final URI statusListSubjectId;
    private final URI contextUri;
    
    private final String statusListType;

    private StatusListPrepareTask(Builder builder) {

        this.latestStatusList = builder.latestStatusList;
        this.vcStatus = builder.vcStatus;
        this.statusListIndex = builder.statusListIndex;

        this.policy = builder.policy;
        this.issuerId = builder.issuerId;
        this.statusListId = builder.statusListId;
        this.statusListSubjectId = builder.statusListSubjectId;
        this.contextUri = builder.contextUri;
        
        this.statusListType = builder.statusListType;
    }

    public Tuple.Pair<VerifiableCredential, LocalDateTime> start() throws VcException {

        try {
            // prepare credential subject
            String encodedList = generateEncodedList(latestStatusList, statusListIndex, vcStatus);
            Map<String, Object> claims = new HashMap<>();
            StatusListType slType = StatusListType.getInstanceFromName(statusListType);
            if(slType == null) {
            	throw new VcException(VcException.ERR_SL_INPUT_STATUS_LIST_TYPE_ERROR, "invalid statusList type");
            }
            claims.put(NAME_STATUS_PURPOSE, slType.name());
            claims.put(NAME_ENCODED_LIST, encodedList);

            CredentialSubject credentialSubject = CredentialSubject.builder()
                .id(statusListSubjectId)
                .claims(claims)
                .build();

            // calculate issuance time and expiration time
            Policy.Duration duration = policy.getEffectiveDuration();
            LocalDateTime issuanceTime = LocalDateTime.now();
            LocalDateTime expirationTime = DateUtils.calculate(issuanceTime, duration.getUnit(), duration.getValue());

            // construct credential
            VerifiableCredential vc = VerifiableCredential.builder()
                    // additional contexts
                    .context(contextUri)
                    // credential type
                    .type(policy.getCredentialType())
                    // credential id, ex: https://digitalwallet.moda:8443/vc/api/status-list/VirtualCardCredential/r123
                    .id(statusListId)
                    // issuer id, ex: did:example:123456
                    .issuer(issuerId)
                    // TODO: use UTC time or local time (UTC+8) ?
                    // issuance date, ex: 2024-09-02T18:56:59Z
                    .issuanceDate(DateUtils.toDate(issuanceTime))
                    // expiration date, ex: 2029-09-02T18:56:59Z
                    .expirationDate(DateUtils.toDate(expirationTime))
                    // credential subject
                    .credentialSubject(credentialSubject)
                    .build();
            return Tuple.collect(vc, issuanceTime);

        } catch (VcException e) {
    		LOGGER.error(e.getMessage(), e);
    		throw e;
    	} catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VcException(VcException.ERR_SL_PREPARE_STATUS_LIST_ERROR, "fail to prepare status list");
        }
    }

    /**
     * generate encoded list
     *
     * @param latestStatusList latest status list
     * @param statusListIndex status list index
     * @param vcStatus vc status
     * @return encoded list
     */
    private String generateEncodedList(
        String latestStatusList,
        int statusListIndex,
        VcStatus vcStatus) {

        String encodedList = null;

        try {
            if (latestStatusList == null) {
                // generate 1st status list
                // prepare initial bit string with 0s (min. size = 16 KB)
                byte[] bytes = new byte[16 * 1024];
                Arrays.fill(bytes, (byte) 0x00);
                encodedList = ZipUtils.gzipCompressThenBase64(bytes);

            } else {
                // generate new status list based on latest one
                VerifiableCredential statusListCredential;
                JwtVerifiableCredential jwtVc = JwtVerifiableCredential.fromCompactSerialization(latestStatusList);
                statusListCredential = FromJwtConverter.fromJwtVerifiableCredential(jwtVc);

                Map<String, Object> claims = statusListCredential.getCredentialSubject().getClaims();
                encodedList = (String) claims.get(NAME_ENCODED_LIST);

                // only use for status list renew scheduled work
                if (statusListIndex == -1 && vcStatus == null) {
                    return encodedList;
                }

                // uncompress
                byte[] bitString = ZipUtils.gzipUncompress(encodedList);
                byte[] newBitString;
                if (statusListIndex > bitString.length * 8) {
                    // append new byte, then re-compress
                    newBitString = Arrays.copyOf(bitString, bitString.length + 1);
                } else {
                    // update existed byte, then re-compress
                    newBitString = Arrays.copyOf(bitString, bitString.length);
                    int byteIndex = statusListIndex / 8;
                    int bitIndex = statusListIndex % 8;
                    LOGGER.info("byteIndex = {}, bitIndex = {}", byteIndex, bitIndex);

                    byte[] activeMask = new byte[] {
                        (byte) 0x7F, (byte) 0xBF, (byte) 0xDF, (byte) 0xEF,
                        (byte) 0xF7, (byte) 0xFB, (byte) 0xFD, (byte) 0xFE
                    };
                    byte[] revokedMask = new byte[] {
                        (byte) 0x80, (byte) 0x40, (byte) 0x20, (byte) 0x10,
                        (byte) 0x08, (byte) 0x04, (byte) 0x02, (byte) 0x01
                    };

                    if (VcStatus.ACTIVE.equals(vcStatus)) {
                        newBitString[byteIndex] = (byte) (newBitString[byteIndex] & activeMask[bitIndex]);
                    } else {
                        newBitString[byteIndex] = (byte) (newBitString[byteIndex] | revokedMask[bitIndex]);
                    }
                }
                encodedList = ZipUtils.gzipCompressThenBase64(newBitString);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return encodedList;
    }

    public static class Builder {

        private String latestStatusList;
        private VcStatus vcStatus;
        private int statusListIndex;

        private Policy policy;
        private URI issuerId;
        private URI statusListId;
        private URI statusListSubjectId;
        private URI contextUri;
        
        private String statusListType;

        public Builder() {
        }

        public Builder setLatestStatusList(String latestStatusList) {
            this.latestStatusList = latestStatusList;
            return this;
        }

        public Builder setVcStatus(VcStatus vcStatus) {
            this.vcStatus = vcStatus;
            return this;
        }

        public Builder setStatusListIndex(int statusListIndex) {
            this.statusListIndex = statusListIndex;
            return this;
        }

        public Builder setPolicy(Policy policy) {
            this.policy = policy;
            return this;
        }

        public Builder setIssuerId(URI issuerId) {
            this.issuerId = issuerId;
            return this;
        }

        public Builder setStatusListId(URI statusListId) {
            this.statusListId = statusListId;
            return this;
        }

        public Builder setStatusListSubjectId(URI statusListSubjectId) {
            this.statusListSubjectId = statusListSubjectId;
            return this;
        }

        public Builder setContextUri(URI contextUri) {
            this.contextUri = contextUri;
            return this;
        }

        public Builder setStatusListType(String statusListType) {
			this.statusListType = statusListType;
			return this;
		}

		public StatusListPrepareTask build() {
            // TODO: check all required fields
            return new StatusListPrepareTask(this);
        }
    }
}
