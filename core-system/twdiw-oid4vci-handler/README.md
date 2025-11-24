## moda-digitalwallet-issuer-oid4vci-service [發行端OID4VCI系統服務]

部署於卡片發行機關的環境，OID4VCI（OpenID for Verifiable Credential Issuance）服務元件負責卡片發行過程，處理卡片的發放與驗證的流程。

### 編譯方式

* Maven > 3.5

#### standalone jar
```bash
mvn clean package 
```
#### docker image 
```bash
mvn spring-boot:build-image
```

### 執行方式

```
java -jar -Dspring.profiles.active=dev moda-digitalwallet-issuer-oid4vci-service-0.0.1-SNAPSHOT.jar
```

### 範例程式

#### [dwissuer-oidvci-101i] 發行端產生卡片申請QRcode
```
curl --location 'http://issuer-oidvci.service.modadw.dev.webe.hinet.services/api/issuer/qr-code' \
--header 'Content-Type: application/json' \
--data '{
 "authenticated": true,
 "id_token": "<ID_TOKEN>"
 }'
```

### runtime 環境

* OpenJDK 17 (Eclipse Temurin)
* Jhipster > 8.6
* Spring-boot 3.3.4
