package gov.moda.dw.manager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** JHipster UserAuthority Entity (CHT 補) */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthorityId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Size(max = 50)
    @Column(name = "authority_name", length = 50)
    private String authorityName;
}
