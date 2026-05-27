package gov.moda.dw.issuer.vc.web.rest;

import gov.moda.dw.issuer.vc.domain.ExtendedUser;
import gov.moda.dw.issuer.vc.repository.ExtendedUserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExtendedUserResource {

  @Autowired private ExtendedUserRepository extendedUserRepository;

  /**
   * query all credentials
   *
   * @return all credentials
   */
  @GetMapping(path = "/extendusers")
  public List<ExtendedUser> queryAll() {

    List<ExtendedUser> result = extendedUserRepository.findAll();
    return result;
  }
}
