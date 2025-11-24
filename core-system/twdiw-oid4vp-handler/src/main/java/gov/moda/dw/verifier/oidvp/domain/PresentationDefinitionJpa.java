package gov.moda.dw.verifier.oidvp.domain;

import gov.moda.dw.verifier.oidvp.domain.PresentationDefinitionJpa.PresentationDefinitionJpaId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "presentation_definition")
@IdClass(PresentationDefinitionJpaId.class)
public class PresentationDefinitionJpa {

    @Id
    @Column(name = "serial_no")
    private String serialNo;

    @Id
    @Column(name = "business_id")
    private String businessId;


    @ColumnTransformer(write = "?::json")
    @Column(name = "presentation_definition")
    private String presentationDefinition;


    public String getBusinessId() {
        return businessId;
    }

    public PresentationDefinitionJpa setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    public String getPresentationDefinition() {
        return presentationDefinition;
    }

    public PresentationDefinitionJpa setPresentationDefinition(String presentationDefinition) {
        this.presentationDefinition = presentationDefinition;
        return this;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public PresentationDefinitionJpa setSerialNo(String serialNo) {
        this.serialNo = serialNo;
        return this;
    }


    public static class PresentationDefinitionJpaId implements Serializable {

        private String serialNo;

        private String businessId;

        public PresentationDefinitionJpaId() {
        }

        public PresentationDefinitionJpaId(String businessId, String serialNo) {
            this.businessId = businessId;
            this.serialNo = serialNo;
        }

        public String getBusinessId() {
            return businessId;
        }

        public void setBusinessId(String businessId) {
            this.businessId = businessId;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            PresentationDefinitionJpaId that = (PresentationDefinitionJpaId) object;
            return Objects.equals(serialNo, that.serialNo) && Objects.equals(businessId, that.businessId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serialNo, businessId);
        }
    }
}
