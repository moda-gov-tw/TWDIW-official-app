package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** JHipster UserAuthority Entity (CHT 補) */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jhi_user_authority")
public class UserAuthority implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private UserAuthorityId id;
}
