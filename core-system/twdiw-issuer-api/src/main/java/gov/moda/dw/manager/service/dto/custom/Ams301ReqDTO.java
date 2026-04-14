package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.service.dto.NonceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ams301ReqDTO {

    String mail;
    String tel;
    NonceDTO nonce;
}
