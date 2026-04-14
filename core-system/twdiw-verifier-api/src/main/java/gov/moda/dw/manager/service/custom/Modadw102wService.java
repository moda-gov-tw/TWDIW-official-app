package gov.moda.dw.manager.service.custom;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import gov.moda.dw.manager.domain.OidvpConfig;
import gov.moda.dw.manager.domain.Org;
import gov.moda.dw.manager.repository.custom.CustomOidvpConfigRepository;
import gov.moda.dw.manager.repository.custom.CustomOrgRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.OidvpConfigService;
import gov.moda.dw.manager.service.dto.OrgDTO;
import gov.moda.dw.manager.service.dto.custom.FindVerifierResDTO;
import gov.moda.dw.manager.service.dto.custom.Modadw102wDefaultLogoResDTO;
import gov.moda.dw.manager.service.dto.custom.OrgLogoReqDTO;
import gov.moda.dw.manager.service.dto.custom.OrgLogoResDTO;
import gov.moda.dw.manager.service.dto.custom.OrgLogoUploadResultDTO;
import gov.moda.dw.manager.service.mapper.OrgMapper;
import gov.moda.dw.manager.type.StatusCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class Modadw102wService {

    @Value("${verifierOid4vpUrl}")
    private String verifierOid4vpUrl;

    @Value("${dwfront.dwfront-301i}")
    private String findVerifierURL;

    @Value("${defaultLogoImgPath}")
    private String defaultLogoImgPath;

    private static final String BASE64PREFIX = "data:image/png;base64,";

    private static final String NOTO_SANS_TC_MEDIUM_FONT_PATH = "fonts/NotoSansTC-Medium.ttf";

    private final CustomOrgRepository customOrgRepository;

    private final OrgMapper orgMapper;

    private final OidvpConfigService oidvpConfigService;

    public Modadw102wService(CustomOrgRepository customOrgRepository,
            CustomOidvpConfigRepository customOidvpConfigRepository, OrgMapper orgMapper,
            OidvpConfigService oidvpConfigService) {
        this.customOrgRepository = customOrgRepository;
        this.orgMapper = orgMapper;
        this.oidvpConfigService = oidvpConfigService;
    }

    /**
     * 組織 DID、LOGO 查詢
     *
     * @param orgId
     * @return
     */
    public OrgLogoUploadResultDTO search() {
        log.info("Modadw102wService-orgLogoUpload-search 進入組織 LOGO 查詢");

        OrgLogoUploadResultDTO response = new OrgLogoUploadResultDTO();
        OrgLogoResDTO orgLogoResDTO = new OrgLogoResDTO();

        // 查 DID
        Optional<OidvpConfig> verifierDID = oidvpConfigService.findOne("verifier.did");
        if (!Objects.equals(verifierDID.get().getPropertyValue(), "")) {
            orgLogoResDTO.setVerifierDID(true);

            // 若verifier.did有值，則拿verifier.did的值呼叫 dwfront-301i 取得verifier相關資訊 -
            // 組織代碼、組織中文名、組織英文名
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String did = verifierDID.get().getPropertyValue();
            String url = findVerifierURL + "/" + did;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<FindVerifierResDTO> findVerifierResponse = restTemplate.exchange(url, HttpMethod.GET, entity,
                    FindVerifierResDTO.class);

            if (findVerifierResponse.getBody() == null || findVerifierResponse.getBody().getCode() != 0) {
                log.error("Modadw102wService-orgLogoUpload-search-front301i查詢DID資訊失敗，錯誤碼: {}, 錯誤訊息: {}",
                        findVerifierResponse.getBody().getCode(), findVerifierResponse.getBody().getMsg());
                response.setStatusCode(StatusCode.DID_INIT_FIND_ISSUER_ERROR);
                return response;
            }
            orgLogoResDTO.setOrgId(findVerifierResponse.getBody().getData().getOrg().getTaxId());
            orgLogoResDTO.setOrgTwName(findVerifierResponse.getBody().getData().getOrg().getName());
            orgLogoResDTO.setOrgEnName(findVerifierResponse.getBody().getData().getOrg().getName_en());

            // 查組織 - 拿 LOGO
            Optional<Org> org = customOrgRepository
                    .findByOrgId(findVerifierResponse.getBody().getData().getOrg().getTaxId());
            if (org.isPresent()) {
                orgLogoResDTO.setLogoSquare(org.get().getLogoSquare());
                orgLogoResDTO.setLogoRectangle(org.get().getLogoRectangle());
            }

        } else {
            orgLogoResDTO.setVerifierDID(false);
        }

        response.setOrgLogoResDTO(orgLogoResDTO);
        response.setStatusCode(StatusCode.SUCCESS);

        return response;
    }

    /**
     * 修改組織 Logo
     *
     * @param orgReqDTO
     * @return
     */
    public OrgLogoUploadResultDTO uploadOrgLogo(OrgLogoReqDTO orgLogoReqDTO) {
        log.info("Modadw102wService-orgLogoUpload 準備上傳組織 Logo, orgReqDTO={}", orgLogoReqDTO);
        // 初始化
        OrgLogoUploadResultDTO response = new OrgLogoUploadResultDTO();

        try {

            // [檢核] 是否已註冊DID
            Optional<OidvpConfig> verifierDID = oidvpConfigService.findOne("verifier.did");
            if (Objects.equals(verifierDID.get().getPropertyValue(), "")) {
                response.setStatusCode(StatusCode.DWVP_DID_NOT_REGISTERED);
                return response;
            }

            // 若verifier.did有值，則拿verifier.did的值呼叫 dwfront-301i 取得verifier相關資訊 -
            // 組織代碼、組織中文名、組織英文名
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String did = verifierDID.get().getPropertyValue();
            String url = findVerifierURL + "/" + did;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<FindVerifierResDTO> findVerifierResponse = restTemplate.exchange(url, HttpMethod.GET, entity,
                    FindVerifierResDTO.class);

            if (findVerifierResponse.getBody() == null || findVerifierResponse.getBody().getCode() != 0) {
                log.error("Modadw102wService-orgLogoUpload-search-front301i查詢DID資訊失敗，錯誤碼: {}, 錯誤訊息: {}",
                        findVerifierResponse.getBody().getCode(), findVerifierResponse.getBody().getMsg());
                response.setStatusCode(StatusCode.DID_INIT_FIND_ISSUER_ERROR);
                return response;
            }

            String didOrgId = findVerifierResponse.getBody().getData().getOrg().getTaxId();
            String loginUserOrgId = SecurityUtils.getJwtUserObject().get(0).getOrgId();

            // [檢核] 登入帳號組織與 DID 所屬組織應一致
            if (!Objects.equals(loginUserOrgId, didOrgId)) {
                response.setStatusCode(StatusCode.ORG_DID_ORG_MISMATCH);
                return response;
            }

            // [查詢] 組織
            Optional<Org> opOrg = customOrgRepository.findByOrgId(didOrgId);
            if (opOrg.isPresent()) {
                Org org = opOrg.get();

                // [更新] 組織 正方形logo、長方形logo、updateTime
                org.setLogoSquare(orgLogoReqDTO.getLogoSquare());
                org.setLogoRectangle(orgLogoReqDTO.getLogoRectangle());
                org.setUpdateTime(Instant.now());

                org = customOrgRepository.save(org);

                OrgDTO uploadResult = orgMapper.toDto(org);

                // 整理回傳內容
                OrgLogoResDTO orgLogoResDTO = new OrgLogoResDTO();
                orgLogoResDTO.setOrgId(uploadResult.getOrgId());
                orgLogoResDTO.setLogoSquare(uploadResult.getLogoSquare());
                orgLogoResDTO.setLogoRectangle(uploadResult.getLogoRectangle());

                // 整理回傳內容
                response.setOrgLogoResDTO(orgLogoResDTO);
                response.setStatusCode(StatusCode.SUCCESS);
                log.info("Modadw102wService-orgLogoUpload 組織上傳 Logo 成功 OrgDTO={}", uploadResult);

            } else {
                log.warn("Modadw102wService-orgLogoUpload 查不到該組織, 無法執行組織 Logo 上傳, org id= {}", didOrgId);
                response.setStatusCode(StatusCode.ORG_NOT_EXISTS);
            }

            return response;

        } catch (Exception ex) {
            log.error("Modadw102wService-orgLogoUpload 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 還原預設長方形 LOGO
     * 
     * @param orgName
     * @return
     */
    public Modadw102wDefaultLogoResDTO getDefaultLogo(String orgName) {
        Modadw102wDefaultLogoResDTO response = new Modadw102wDefaultLogoResDTO();

        if (StringUtils.isNotEmpty(orgName)) {
            response.setDefaultRectangleLogo(getLogoWithNameGenerator(orgName));
        }

        return response;
    }

    /**
     * 繪製長方形 LOGO
     * 
     * @param orgName
     * @return String(base64)
     */
    public String getLogoWithNameGenerator(String orgName) {
        try {
            BufferedImage bufferedImage = generateLogoImage(orgName);

            // 輸出 base64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();

            return BASE64PREFIX + Base64.getEncoder().encodeToString(imageBytes);

        } catch (IOException e) {
            log.error("getLogoWithNameGenerator # :{}", ExceptionUtils.getMessage(e));
            return null;
        } catch (FontFormatException e) {
            log.error("getLogoWithNameGenerator # :{}", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    /**
     * 圖片生成
     * 
     * @param orgName
     * @return
     * @throws IOException
     * @throws FontFormatException
     */
    private BufferedImage generateLogoImage(String orgName) throws FontFormatException, IOException {
        // 設定圖片大小
        int scale = 4;
        int width = 212 * scale;
        int height = 42 * scale;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2d = bufferedImage.createGraphics();
        // 開啟抗鋸齒、設定高品質縮放
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // 設定背景顏色
        graphics2d.setColor(Color.WHITE);
        graphics2d.fillRect(0, 0, width, height);

        // 讀取 logo 圖片
        ClassPathResource classPathResource = new ClassPathResource(defaultLogoImgPath);
        BufferedImage logoBufferedImage = ImageIO.read(classPathResource.getInputStream());
        // 繪製 logo 尺寸
        int logoWidth = (int) (width * 0.3);
        int logoHeight = (int) ((double) logoBufferedImage.getHeight() / logoBufferedImage.getWidth() * logoWidth) + 6;

        int logoX = -30;
        int logoY = (height - logoHeight) / 2;
        // 繪製 logo
        graphics2d.drawImage(logoBufferedImage, logoX, logoY, logoWidth, logoHeight, null);

        // 設定文字
        String name = orgName;
        // 字體顏色
        graphics2d.setColor(Color.BLACK);
        // 計算文字位置
        FontMetrics fontMetrics = graphics2d.getFontMetrics();
        int textX = logoWidth - 40;
        int textY = (height - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();

        if (name.length() > 16) {
            drawText(graphics2d, name, textX, textY, 60, 10, 70);
        } else if (name.length() > 8) {
            drawText(graphics2d, name, textX, textY, 72, 8, 80);
        } else {
            // name.length() 表示不分行
            drawText(graphics2d, name, textX, textY, 88, name.length(), 0);
        }

        graphics2d.dispose();

        // 繪製完後，縮小輸出
        Image scaledImage = bufferedImage.getScaledInstance(212, 42, Image.SCALE_AREA_AVERAGING);

        BufferedImage scaledDownImage = new BufferedImage(212, 42, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledDownImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return scaledDownImage;
    }

    /**
     * 文字配置
     * 
     * @param graphics2d
     * @param name        組織名稱
     * @param textX       字體x軸
     * @param textY       字體y軸
     * @param fontSize    字體大小
     * @param splitLength 分行
     * @param lineSpace   行距
     * @throws IOException
     * @throws FontFormatException
     */
    private void drawText(Graphics2D graphics2d, String name, int textX, int textY, int fontSize, int splitLength,
            int lineSpace) throws FontFormatException, IOException {
        // 載入內嵌中文字體
        try (InputStream fontStream = getClass().getClassLoader().getResourceAsStream(NOTO_SANS_TC_MEDIUM_FONT_PATH)) {
            if (null == fontStream) {
                log.warn("字體載入失敗！ path : {}", NOTO_SANS_TC_MEDIUM_FONT_PATH);
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.PLAIN, fontSize);
            graphics2d.setFont(font);

            // 判斷是否分行
            if (name.length() > splitLength) {
                String firstLine = name.substring(0, splitLength);
                String secondLine = name.substring(splitLength);

                // 調整 textY
                textY = textY - 10;

                // 繪製第一行
                graphics2d.drawString(firstLine, textX, textY);
                // 繪製第二行 (y 座標 + 行距)
                graphics2d.drawString(secondLine, textX, textY + lineSpace);

            } else {
                // 調整 textY
                textY = textY + 30;
                graphics2d.drawString(name, textX, textY);
            }
        }
    }
}
