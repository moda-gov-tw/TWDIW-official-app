package gov.moda.dw.manager.type;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ModelType {
    MODEL_1("0", "請選擇"),
    MODEL_2("1", "靜態 QR Code 模式"),
    MODEL_3("2", "APP 出示憑證模式");
    
    @Getter
    private String value;
    
    @Getter
    private String label;
    
    public static ModelType toModelType(String value) {
        for (ModelType type : ModelType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        
        return null;
    }
    
    public static String getModelTypeLabel(String value) {
        if (StringUtils.isNotEmpty(value) || StringUtils.isNotBlank(value)) {
            for (ModelType type : ModelType.values()) {
                if (type.getValue().equals(value)) {
                    return type.getLabel();
                }
            }
        }
        
        return "";
    }
    
    public Boolean isStatic() {
        return this == MODEL_2;
    }
    
    public Boolean isOffline() {
        return this == MODEL_3;
    }
}
