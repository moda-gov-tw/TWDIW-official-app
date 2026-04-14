package gov.moda.dw.manager.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * A Nonce.
 */
@Entity
@Table(name = "nonce")
public class Nonce implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "s_id")
    private String sId;

    @Column(name = "nonce_id")
    private String nonceId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "create_time")
    private LocalDate createTime;

    @Column(name = "captcha_code")
    private String captchaCode;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getsId() {
        return sId;
    }

    public Nonce sId(String sId) {
        this.sId = sId;
        return this;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getNonceId() {
        return nonceId;
    }

    public Nonce nonceId(String nonceId) {
        this.nonceId = nonceId;
        return this;
    }

    public void setNonceId(String nonceId) {
        this.nonceId = nonceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getCreateTime() {
        return this.createTime;
    }

    public Nonce createTime(LocalDate createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(LocalDate createTime) {
        this.createTime = createTime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    public String getCaptchaCode() {
        return this.captchaCode;
    }

    public Nonce captchaCode(String captchaCode) {
        this.setCaptchaCode(captchaCode);
        return this;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Nonce)) {
            return false;
        }
        return id != null && id.equals(((Nonce) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "Nonce{" +
                "id=" + getId() +
                ", sId='" + getsId() + "'" +
                ", nonceId='" + getNonceId() + "'" +
                ", userId='" + getUserId() + "'" +
                ", createTime='" + getCreateTime() + "'" +
                ", captchaCode='" + getCaptchaCode() + "'" +
            "}"
        );
    }
}
