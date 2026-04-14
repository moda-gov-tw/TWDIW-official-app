package gov.moda.dw.manager.service.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.moda.dw.manager.domain.VCItemField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 數位錢包憑證資料傳輸物件
 * 用於建立和傳輸數位錢包憑證資料
 */
public class CreateVCItemDataDTO {

    /**
     * 憑證項目識別碼
     */
    private Long vcId;

    /**
     * 憑證唯一識別碼
     */
    private String vcUid;

    /**
     * 交易驗證碼
     */
    private String txCode;
    
    /**
     * 卡片發行日(非必填)(YYYYMMDD)
     */
    private String issuanceDate;

    /**
     * 卡片到期日(非必填)(YYYYMMDD)
     */
    private String expiredDate;

    /**
     * 標記實際資料對應至 VC 卡片的資料標註
     */
    private String dataTag;

    /**
     * 憑證資料欄位列表
     */
    private List<CreateVCItemDataField> fields;

    /**
     * 欲廢止卡片的 id 清單
     */
    private List<String> cids;

    /**
     * 預設建構子
     * 初始化欄位列表
     */
    public CreateVCItemDataDTO() {
        this.fields = new ArrayList<>();
    }

    public String getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    /**
     * 取得憑證項目識別碼
     * @return 憑證項目識別碼
     */
    public Long getVcId() {
        return vcId;
    }

    /**
     * 設定憑證項目識別碼
     * @param vcId 憑證項目識別碼
     */
    public void setVcId(Long vcId) {
        this.vcId = vcId;
    }

    /**
     * 取得憑證唯一識別碼
     * @return 憑證唯一識別碼
     */
    public String getVcUid() {
        return vcUid;
    }

    /**
     * 設定憑證唯一識別碼
     * @param vcUid 憑證唯一識別碼
     */
    public void setVcUid(String vcUid) {
        this.vcUid = vcUid;
    }

    /**
     * 取得交易驗證碼
     * @return 交易驗證碼
     */
    public String getTxCode() {
        return txCode;
    }

    /**
     * 設定交易驗證碼
     * @param txCode 交易驗證碼
     */
    public void setTxCode(String txCode) {
        this.txCode = txCode;
    }

    /**
     * 取得標記實際資料對應至 VC 卡片的資料標註
     * 
     * @return 標記實際資料對應至 VC 卡片的資料標註
     */
    public String getDataTag() {
        return dataTag;
    }

    /**
     * 設定標記實際資料對應至 VC 卡片的資料標註
     * 
     * @param dataTag 標記實際資料對應至 VC 卡片的資料標註
     */
    public void setDataTag(String dataTag) {
        this.dataTag = dataTag;
    }

    /**
     * 取得憑證資料欄位列表
     * @return 憑證資料欄位列表
     */
    public List<CreateVCItemDataField> getFields() {
        return fields;
    }

    /**
     * 設定憑證資料欄位列表
     * @param fields 憑證資料欄位列表
     */
    public void setFields(List<CreateVCItemDataField> fields) {
        this.fields = fields;
    }

    /**
     * 取得欲廢止卡片的 id 清單
     * @return 欲廢止卡片的 id 清單
     */
    public List<String> getCids() {
        return cids;
    }

    /**
     * 設定欲廢止卡片的 id 清單
     * @param fields 欲廢止卡片的 id 清單
     */
    public void setCids(List<String> cids) {
        this.cids = cids;
    }

    /**
     * 將欄位列表轉換為 JSON 字串
     * @return JSON 格式的欄位資料字串，若轉換失敗則返回空陣列字串 "[]"
     */
    public String fieldsToJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(fields);
        } catch (Exception e) {
            return "[]";
        }
    }

    /**
     * 將欄位內容值轉換為逗號分隔的字串
     * @return 以逗號分隔的欄位內容值字串
     */
    public String fieldsValueToString() {
        return fields.stream().map(CreateVCItemDataField::getContent).collect(Collectors.joining(","));
    }

    /**
     * 將欄位資料轉換為 ObjectNode 物件
     * @return 包含欄位資料的 ObjectNode 物件
     */
    public ObjectNode fieldToJsonObject() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();

        for (CreateVCItemDataField field : fields) {
            rootNode.put(field.getEname(), field.getContent());
        }

        return rootNode;
    }

    /**
     * 將欄位資料轉換為 Map 物件
     * @return 包含欄位名稱和內容的 Map 物件
     */
    public Map<String, Object> fieldToMap() {
        return fields.stream().collect(Collectors.toMap(CreateVCItemDataField::getEname, CreateVCItemDataField::getContent));
    }

    /**
     * 新增欄位資料
     * @param vcItemField 憑證項目欄位定義
     * @param content 欄位內容
     */
    public void addField(VCItemField vcItemField, String content) {
        CreateVCItemDataField field = new CreateVCItemDataField();
        field.setType(vcItemField.getType());
        field.setEname(vcItemField.getEname());
        field.setCname(vcItemField.getCname());
        field.setContent(content);
        fields.add(field);
    }
}
