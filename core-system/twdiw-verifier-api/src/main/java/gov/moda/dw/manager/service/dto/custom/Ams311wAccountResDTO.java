package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.service.dto.ExtendedUserDTO;
import gov.moda.dw.manager.service.dto.OrgDTO;
import gov.moda.dw.manager.type.UserTypeIdType;
import gov.moda.dw.manager.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ams311wAccountResDTO implements Serializable {

    // jhi_user。
    private Long id;

    private String login;

    private Boolean activated;

    private String createdBy;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private String activationKey;

    private String resetKey;

    // extended_user。
    private Long extendedId;

    private String orgId;

    private String orgTwName;

    private String userName;

    private String email;

    private String tel;

    private String employeeId;

    private String employeeTypeId;

    private String leftDate;

    private String onboardDate;

    private String userTypeId;

    private String userTypeName;

    private String state;

    private Instant createTime;

    public Ams311wAccountResDTO(User user, ExtendedUserDTO extendedUserDTO, OrgDTO orgDTO) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.activated = user.isActivated();
        this.createdBy = user.getCreatedBy();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.extendedId = extendedUserDTO.getId();
        this.orgId = extendedUserDTO.getOrgId();
        this.orgTwName = orgDTO.getOrgTwName();
        this.userName = extendedUserDTO.getUserName();
        this.email = extendedUserDTO.getEmail();
        this.tel = extendedUserDTO.getTel();
        this.employeeId = extendedUserDTO.getEmployeeId();
        this.employeeTypeId = extendedUserDTO.getEmployeeTypeId();
        this.leftDate = extendedUserDTO.getLeftDate() != null ? DateUtils.convertDate(extendedUserDTO.getLeftDate(), "yyyy-MM-dd") : null;
        this.onboardDate = extendedUserDTO.getOnboardDate() != null
            ? DateUtils.convertDate(extendedUserDTO.getOnboardDate(), "yyyy-MM-dd")
            : null;

        this.userTypeId = extendedUserDTO.getUserTypeId();
        UserTypeIdType userTypeIdType = null;
        if (StringUtils.isNotBlank(extendedUserDTO.getUserTypeId())) {
            userTypeIdType = UserTypeIdType.toUserTypeIdType(extendedUserDTO.getUserTypeId());
        }
        if (userTypeIdType != null) {
            this.userTypeName = userTypeIdType.getName();
        } else {
            this.userTypeName = extendedUserDTO.getUserTypeId();
        }
        this.state = extendedUserDTO.getState();
        this.createTime = extendedUserDTO.getCreateTime();
        this.activationKey = StringUtils.isNotBlank(user.getActivationKey()) ? "true" : "false";
        this.resetKey = StringUtils.isNotBlank(user.getResetKey()) ? "true" : "false";
    }

    public Ams311wAccountResDTO(User user, ExtendedUser extendedUser) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.activated = user.isActivated();
        this.createdBy = user.getCreatedBy();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.extendedId = extendedUser.getId();
        this.orgId = extendedUser.getOrgId();
        this.userName = extendedUser.getUserName();
        this.email = extendedUser.getEmail();
        this.tel = extendedUser.getTel();
        this.employeeId = extendedUser.getEmployeeId();
        this.employeeTypeId = extendedUser.getEmployeeTypeId();
        this.leftDate = extendedUser.getLeftDate() != null ? DateUtils.convertDate(extendedUser.getLeftDate(), "yyyy-MM-dd") : null;
        this.onboardDate = extendedUser.getOnboardDate() != null
            ? DateUtils.convertDate(extendedUser.getOnboardDate(), "yyyy-MM-dd")
            : null;

        this.userTypeId = extendedUser.getUserTypeId();
        UserTypeIdType userTypeIdType = null;
        if (StringUtils.isNotBlank(extendedUser.getUserTypeId())) {
            userTypeIdType = UserTypeIdType.toUserTypeIdType(extendedUser.getUserTypeId());
        }
        if (userTypeIdType != null) {
            this.userTypeName = userTypeIdType.getName();
        } else {
            this.userTypeName = extendedUser.getUserTypeId();
        }

        this.state = extendedUser.getState();
        this.createTime = extendedUser.getCreateTime();
        this.activationKey = StringUtils.isNotBlank(user.getActivationKey()) ? "true" : "false";
        this.resetKey = StringUtils.isNotBlank(user.getResetKey()) ? "true" : "false";
    }
}
