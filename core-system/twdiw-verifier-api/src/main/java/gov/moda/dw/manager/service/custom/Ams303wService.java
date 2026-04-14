package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.BwdHistory;
import gov.moda.dw.manager.repository.custom.CustomBwdHistoryRepository;
import gov.moda.dw.manager.service.criteria.BwdParamCriteria;
import gov.moda.dw.manager.service.dto.BwdParamDTO;
import gov.moda.dw.manager.service.dto.custom.IsPasswordValidReqDTO;
import gov.moda.dw.manager.type.BwdRuleType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.BooleanFilterUtils;
import gov.moda.dw.manager.util.StringFilterUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class Ams303wService {

    @Autowired
    private CustomBwdParamQueryService customBwdParamQueryService;

    @Autowired
    private CustomBwdHistoryRepository customBwdHistoryRepository;

    /**
     * 確認使用者登入密碼是否還未過期
     */
    public StatusCode isPasswordValid(IsPasswordValidReqDTO reqDTO) {
        if (StringUtils.isBlank(reqDTO.getLogin())) {
            log.error(
                "[Ams303wService-isPasswordValid]，發生錯誤，錯誤原因:{}",
                StatusCode.LOGIN_IS_PASS_WORD_VALID_LOGIN_IS_NULL.getMsg()
            );
            return StatusCode.LOGIN_IS_PASS_WORD_VALID_LOGIN_IS_NULL;
        }

        try {
            // 取得密碼規則
            BwdParamCriteria criteria = new BwdParamCriteria();
            criteria.setState(BooleanFilterUtils.toEqualBooleanFilter(true));
            criteria.setRuleId(StringFilterUtils.toEqualStringFilter(BwdRuleType.BwdMaximumAge.getRuleId()));
            List<BwdParamDTO> bwdParamDTOList = this.customBwdParamQueryService.findByCriteria(criteria);
            if (bwdParamDTOList.isEmpty()) {
                log.warn("[Ams303wService-isPasswordValid]，{}", StatusCode.LOGIN_IS_PASS_WORD_VALID_RULE_NOT_FOUND);
                return StatusCode.LOGIN_IS_PASS_WORD_VALID_RULE_NOT_FOUND;
            }
            BwdParamDTO bwdParamDTO = bwdParamDTOList.get(0);

            // 取得帳號更改密碼歷史紀錄
            final List<BwdHistory> bwdHistoryList = this.customBwdHistoryRepository.findAllByUserIdOrderByCreateTimeDesc(reqDTO.getLogin());

            // 取得現在時間
            final Instant now = Instant.now();

            final String maxValidPeriod = bwdParamDTO.getParamValue();
            if (StringUtils.isNotBlank(maxValidPeriod)) {
                int maxValidMinutes = Integer.parseInt(maxValidPeriod);
                if (!bwdHistoryList.isEmpty()) {
                    Instant lastPasswordUpdateTime = bwdHistoryList.get(0).getCreateTime();
                    Instant passwordExpiryTime = lastPasswordUpdateTime.plusSeconds(60L * maxValidMinutes);
                    if (now.isAfter(passwordExpiryTime)) {
                        return StatusCode.LOGIN_IS_PASS_WORD_VALID_PASSWORD_EXPIRED;
                    } else {
                        return StatusCode.SUCCESS;
                    }
                } else {
                    log.info("[Ams303wService-isPasswordValid]，{}", StatusCode.LOGIN_IS_PASS_WORD_VALID_NO_HISTORY_RECORD.getMsg());
                    return StatusCode.LOGIN_IS_PASS_WORD_VALID_NO_HISTORY_RECORD;
                }
            } else {
                log.error(
                    "[Ams303wService-isPasswordValid]，發生錯誤，錯誤原因:{}",
                    StatusCode.LOGIN_IS_PASS_WORD_VALID_BWD_RULE_VALUE_ERROR.getMsg()
                );
                return StatusCode.LOGIN_IS_PASS_WORD_VALID_BWD_RULE_VALUE_ERROR;
            }
        } catch (Exception ex) {
            log.error("[Ams303wService-isPasswordValid]，發生錯誤，錯誤原因:{}", ExceptionUtils.getStackTrace(ex));
            return StatusCode.LOGIN_IS_PASS_WORD_VALID_EXCEPTION;
        }
    }
}
