package gov.moda.dw.issuer.oidvci.domain;


import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity //標示為實體類別
@Table(name = "oidvci_setting") //DB_TABLE_NAME
public class OidvciSettingEntity {

    @Id //標示何者為Primary Key
    @Column(name="setting_name")
    private String settingName;

    @Column(name="setting_value")
    private String settingValue;

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
}
