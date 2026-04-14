package gov.moda.dw.manager.web.rest.vm;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * View Model object for storing the user's key and password.
 */
@Getter
@Setter
public class KeyAndPasswordVM {

    @JsonProperty("key")
    private String key;

    @JsonProperty("newPassword")
    private String newBwd;

}
