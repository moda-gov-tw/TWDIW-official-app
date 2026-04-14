package gov.moda.dw.manager.service.custom;

// import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
        import gov.moda.dw.manager.service.ImgVerifyCodeQueryService;
import gov.moda.dw.manager.service.ImgVerifyCodeService;
import gov.moda.dw.manager.service.criteria.ImgVerifyCodeCriteria;
import gov.moda.dw.manager.service.dto.ImgVerifyCodeDTO;
        import gov.moda.dw.manager.util.PageUtils;
import gov.moda.dw.manager.util.StringFilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
        import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class Ams304wService {

  @Autowired
  private ImgVerifyCodeQueryService imgVerifyCodeQueryService;

  @Autowired
  private ImgVerifyCodeService imgVerifyCodeService;

  @Value("${etd.manage.pdf.url:xxx}")
  private String url;

  @Value("${etd.api.manager:xx}")
  private String managerToken;

  /**
   * 新增 或 更新 隨機碼紀錄
   * @param uuid
   * @param rand
   */
  public void newRandCode(String uuid, String rand) {
    ImgVerifyCodeDTO verifyCodeDTO = this.getRandCodeByRecordOrNew(uuid);
    verifyCodeDTO.setVerifyCode(rand);
    verifyCodeDTO.setCreateTime(Instant.now());
    verifyCodeDTO.setExpireTime(Instant.now().plusSeconds(10 * 60));
    imgVerifyCodeService.save(verifyCodeDTO);
  }

  // /**
  //  * 查驗驗證碼-取得驗證碼圖檔
  //  * @param reqDTO
  //  * @return
  //  */
  // public GetPdfResDTO getCodeImg(Etd3101wReqDTO reqDTO) {
  //     GetPdfResDTO result  = new GetPdfResDTO();
  //     reqDTO.setIp(this.getIp());
  //     RestTemplate restTemplate = RestTemplateUtils.getRestTemplate();
  //     HttpHeaders httpHeaders = new HttpHeaders();
  //     httpHeaders.add("Content-Type", "application/json");
  //     httpHeaders.add("Access-Token", managerToken);

  //     try{
  //         String reqString = JsonUtils.toJson(reqDTO);
  //         HttpEntity<String> req = new HttpEntity<>(reqString, httpHeaders);
  //         ResponseEntity<GetPdfResDTO> resEntity =
  //             restTemplate.exchange(url, HttpMethod.POST, req, GetPdfResDTO.class);
  //         result = resEntity.getBody();
  //         return result;
  //     } catch (Exception e) {
  //         log.error(StatusCode.ETD3101W_GET_BASE64_FAIL.getMsg() + "，帶入參數：{}，錯誤原因為:{}",reqDTO, ExceptionUtils.getStackTrace(e));
  //         result.setMessage(StatusCode.GETDATA_FAIL.getMsg()); // 取得檔案發生錯誤
  //         result.setCode(StatusCode.FAIL.getCode());
  //         return result;
  //     }
  // }

  // private String getIp() {
  //     ServletRequestAttributes servletRequestAttributes =
  //         (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
  //     HttpServletRequest req = null;
  //     if (servletRequestAttributes != null) {
  //         req = servletRequestAttributes.getRequest();
  //         String ipAddress = req.getHeader("X-FORWARDED-FOR");
  //         if (ipAddress == null) {
  //             ipAddress = req.getRemoteAddr();
  //         }
  //         return ipAddress;
  //     } else {
  //         return "";
  //     }
  // }

  // /**
  // * 檢核 圖形碼碼
  // * @param reqDTO
  // */
  // public boolean checkGraphVerifyCode(Etd3101wReqDTO reqDTO) {
  //     if (
  //         StringUtils.isNotBlank(reqDTO.getVerifyCode())
  //         && StringUtils.isNotBlank(reqDTO.getUuid())
  //     ) {
  //         ImgVerifyCodeCriteria criteria = new ImgVerifyCodeCriteria();
  //         criteria.setVerifyCode(StringFilterUtils.toEqualStringFilter(reqDTO.getVerifyCode().toUpperCase()));
  //         criteria.setVerifyUniId(StringFilterUtils.toEqualStringFilter(reqDTO.getUuid()));
  //         criteria.setExpireTime(new InstantFilter().setGreaterThanOrEqual(Instant.now()));
  //         List<ImgVerifyCodeDTO> codeDTOS = imgVerifyCodeQueryService.findByCriteria(criteria, PageUtils.MAX_CONTENT).getContent();

  //         if(codeDTOS.size() > 0) {
  //             imgVerifyCodeService.delete(codeDTOS.get(0).getId());

  //             return true;
  //         }
  //     }

  //     return false;
  // }

  public ImgVerifyCodeDTO getRandCodeByRecordOrNew(String uuid) {
    ImgVerifyCodeDTO result = new ImgVerifyCodeDTO();

    ImgVerifyCodeCriteria criteria = new ImgVerifyCodeCriteria();
    criteria.setVerifyUniId(StringFilterUtils.toEqualStringFilter(uuid));

    List<ImgVerifyCodeDTO> codeDTOS = imgVerifyCodeQueryService.findByCriteria(criteria, PageUtils.MAX_CONTENT).getContent();
    if (codeDTOS.size() > 0) {
      result = codeDTOS.get(0);
    } else {
      result.setVerifyUniId(uuid);
    }
    return result;
  }
  //    @Scheduled(fixedDelayString = "30000")
  //    public void removeExpireVerifyCode() {
  //        ImgVerifyCodeCriteria criteria = new ImgVerifyCodeCriteria();
  //        criteria.setExpireTime(new InstantFilter().setLessThan(Instant.now()));
  //        List<ImgVerifyCodeDTO> verifyCodeDTOS = imgVerifyCodeQueryService.findByCriteria(criteria, PageUtils.MAX_CONTENT).getContent();
  //        if(verifyCodeDTOS.size() > 0) {
  //            verifyCodeDTOS.forEach(x -> imgVerifyCodeService.delete(x.getId()));
  //        }
  //    }
}
