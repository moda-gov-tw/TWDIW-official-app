package gov.moda.dw.manager.service.dto.custom.ext.api;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ams101iReqDTO {

    //平台代碼
    private String platformId;
    //客戶端活動編號
    private String customerActivityUUid;
    //客戶端活動日期
    private String customerActivityTime;
    //客戶端活動名稱
    private String customerActivityName;
    //聯絡人
    private String contact;
    //聯絡人信箱
    private String contactEmail;
    //聯絡人手機號碼
    private String contactPhone;
    //繳費平台
    private String payPlatform;
    //訂單類型
    private String orderType;
    //訂單結算所年月份
    private String orderSettlementDate;
    //正常或補正訂單
    private String correctionType;
    //商品清單
    private List<Item> item;
    //電子發票或收據開立類型
    private String invOrRecType;
    //公司統編
    private String taxIdNumber;
    //公司(學校/機構)名稱
    private String companyName;
    //公司(學校/機構)地址
    private String companyAddr;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {

        //商品名稱代碼
        private String itemNameCode;
        //商品明細數量
        private String itemDetailCount;
        //商品明細
        private List<ItemDetail> itemDetail;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ItemDetail {

            //購買人身分證號
            private String idNo;
            //驗證結果
            private String verifyResult;
            //支付類別
            private String paymentCategory;
            //購買/申請時間
            private String createTime;
        }
    }
}
