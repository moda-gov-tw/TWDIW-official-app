package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.BwdHistory;
import gov.moda.dw.manager.domain.BwdParam;
import gov.moda.dw.manager.domain.LoginCount;
import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.repository.BwdHistoryRepository;
import gov.moda.dw.manager.repository.BwdParamRepository;
import gov.moda.dw.manager.repository.LoginCountRepository;
import gov.moda.dw.manager.repository.UserRepository;
import gov.moda.dw.manager.security.crypto.ModadwPasswordEncoder;
import gov.moda.dw.manager.type.BwdProfileType;
import gov.moda.dw.manager.type.BwdRuleType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class BwdParamCustomService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BwdParamRepository bwdParamRepository;

    @Autowired
    private BwdHistoryRepository bwdHistoryRepository;

    @Autowired
    private LoginCountRepository loginCountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModadwPasswordEncoder modadwPasswordEncoder;

    public List<BwdParam> getBwdRule(BwdProfileType bwdProfileType, boolean isForLogin) {
        BwdParam probe = new BwdParam().bwdProfileId(bwdProfileType.getCode()).state(true);
        if (isForLogin) {
            probe.setActionType("1");
        }

        Example<BwdParam> example = Example.of(probe);
        return this.bwdParamRepository.findAll(example);
    }

    /**
     * 密碼規則檢查
     * <pre>web進入:
     * 最短期效
     * </pre>
     * <pre>
     * N代不相同
     * </pre>
     *
     * @param userId      帳號
     * @param newBwd      新密碼
     * @param bwdParamMap 密碼規則
     * @param isUrl  是否從 url 進入的
     * @return
     */
    public String ruleCheck(String userId, String newBwd, Map<String, BwdParam> bwdParamMap, boolean isUrl, boolean isCreate) {
        final Instant now = Instant.now();
        final String cipher = this.passwordEncoder.encode(newBwd);
        final List<BwdHistory> bwdHistoryList = this.bwdHistoryRepository.findAllByUserIdOrderByCreateTimeDesc(userId);

        if (!isCreate && CollectionUtils.isNotEmpty(bwdHistoryList)) {
            // * N 代密碼不可重複。
            final BwdParam bwdMaxTimesParam = bwdParamMap.get(BwdRuleType.Remember.getRuleId());
            if (null != bwdMaxTimesParam) {
                int maxTimes = Integer.parseInt(bwdMaxTimesParam.getParamValue());
                int nowCount = bwdHistoryList.size();
                int index = 0;
                for (BwdHistory tmp : bwdHistoryList) {
                    if (nowCount > maxTimes && index >= maxTimes) {
                        break;
                    }
                    if (this.modadwPasswordEncoder.matchesNewBwd(newBwd, tmp.getBwdHash())) {
                        return this.toBwdGrammarCheckMessage(bwdMaxTimesParam);
                    }
                    index++;
                }
            }
        }

        // // 首次登入強迫更改密碼
        // // 密碼最長有效期 (分鐘)
        // // 密碼錯誤 N 次鎖定
        // // N 分鐘後自動解鎖
        // 帳號被管理者鎖定
        // 組織 IP 白名單過濾
        // 帳號 IP 白名單過濾
        // 平台 IP 白名單過濾
        // 密碼模式

        // * 密碼最短有效期 (分鐘)
        final BwdParam shortestLife = bwdParamMap.get(BwdRuleType.BwdMinimumAge.getRuleId());
        // ! 從web進入的(「非」從 重置url 進入的)
        if (!isUrl && null != shortestLife) {
            int value = Integer.parseInt(shortestLife.getParamValue());
            Instant deadline = bwdHistoryList.get(0).getCreateTime().plusSeconds(60 * value);
            if (now.isBefore(deadline)) {
                return "密碼於1天(24小時)內不可重複變更";
            }
        }

        // 密碼語法複雜度 (15~28)
        // 是否忽略大小寫
        // 密碼最少包含 N 個大寫字母
        // 密碼最多包含 N 個大寫字母
        // 密碼最少包含 N 個小寫字母
        // 密碼最多包含 N 個小寫字母
        // 允許密碼包含數字
        // 是否允許密碼以數字開頭
        // 是否允許密碼以數字結尾
        // 密碼最少包含 N 個數字
        // 密碼最多包含 N 個數字
        // 是否允許密碼包含特殊符號
        // 是否允許密碼以特殊符號開頭
        // 是否允許密碼以特殊符號結尾
        // 密碼最少包含 N 個符號
        // 密碼最多包含 N 個符號

        // * 密碼最少長度
        final BwdParam minLength = bwdParamMap.get(BwdRuleType.BwdGrammar_Symbol_MinLength.getRuleId());
        if (null != minLength) {
            int value = Integer.parseInt(minLength.getParamValue());
            boolean result = newBwd.length() < value;
            if (result) {
                return this.toBwdGrammarCheckMessage(minLength);
            }
        }

        // * 密碼最大長度
        final BwdParam maxLength = bwdParamMap.get(BwdRuleType.BwdGrammar_Symbol_MaxLength.getRuleId());
        if (null != maxLength) {
            int value = Integer.parseInt(maxLength.getParamValue());
            boolean result = newBwd.length() > value;
            if (result) {
                return this.toBwdGrammarCheckMessage(maxLength);
            }
        }

        // * 密碼最少包含 N 個大寫字母
        final BwdParam maxUpperCase = bwdParamMap.get(BwdRuleType.BwdGrammar_Letter_IncludeMimUpCase.getRuleId());
        if (null != maxUpperCase) {
            int value = Integer.parseInt(maxUpperCase.getParamValue());
            boolean result = countUpperCaseLetters(newBwd) >= value;
            if (!result) {
                return this.toBwdGrammarCheckMessage(maxUpperCase);
            }
        }

        // * 密碼最少包含 N 個小寫字母
        final BwdParam maxLowerCase = bwdParamMap.get(BwdRuleType.BwdGrammar_Letter_IncludeMimLowCase.getRuleId());
        if (null != maxLowerCase) {
            int value = Integer.parseInt(maxLowerCase.getParamValue());
            boolean result = countLowerCaseLetters(newBwd) >= value;
            if (!result) {
                return this.toBwdGrammarCheckMessage(maxLowerCase);
            }
        }

        // 密碼最少包含 N 個符號
        final BwdParam minSymbols = bwdParamMap.get(BwdRuleType.BwdGrammar_Symbol_IncludeMimSymbol.getRuleId());
        if (null != minSymbols) {
            int value = Integer.parseInt(minSymbols.getParamValue());
            boolean result = countSymbols(newBwd) >= value;
            if (!result) {
                return this.toBwdGrammarCheckMessage(minSymbols);
            }
        }

        // 弱密碼清單

        return null;
    }

    /**
     * 密碼規則檢查 (登入時)
     * <pre>
     * 密碼錯誤 N 次鎖定
     * N 分鐘後自動解鎖
     * </pre>
     *
     * @param userId      帳號
     * @param bwdParamMap 密碼規則
     * @return
     */
    public BwdRuleType preRuleCheckForLogin(String userId, Map<String, BwdParam> bwdParamMap) {
        final Instant now = Instant.now();

        final List<BwdHistory> bwdHistoryList = this.bwdHistoryRepository.findAllByUserIdOrderByCreateTimeDesc(userId);
        final Optional<LoginCount> loginCount = this.loginCountRepository.findOneByUserId(userId);
        // // N 代密碼不可重複。
        // // 首次登入強迫更改密碼
        // // 密碼最長有效期 (分鐘)

        // * 密碼錯誤 N 次鎖定
        final BwdParam loginFailCount = bwdParamMap.get(BwdRuleType.BwdLocked.getRuleId());

        if (null != loginFailCount) {
            int value = Math.max(1, Integer.parseInt(loginFailCount.getParamValue())); // ! 合理至少為 1 以上
            Integer failCount = loginCount.orElse(new LoginCount().failCount(0)).getFailCount();
            if (failCount >= value) {
                // * N 分鐘後自動解鎖
                final BwdParam unlock = bwdParamMap.get(BwdRuleType.BwdLocked_AutoUnlock.getRuleId());

                if (null != unlock) {
                    int sub_value = Integer.parseInt(unlock.getParamValue());
                    Instant deadline = loginCount.get().getUpdateTime().plusSeconds(60 * sub_value);
                    if (now.isBefore(deadline)) {
                        BwdRuleType ruleType = BwdRuleType.BwdLocked_AutoUnlock.setMsg(
                            this.toBwdGrammarCheckMessage(loginFailCount) + "，" + this.toBwdGrammarCheckMessage(unlock)
                        );
                        return ruleType;
                    }
                } else {
                    BwdRuleType ruleType = BwdRuleType.BwdLocked.setMsg(this.toBwdGrammarCheckMessage(loginFailCount));
                    return ruleType;
                }
            }
        }

        // // 帳號被管理者鎖定
        // // 組織 IP 白名單過濾
        // // 帳號 IP 白名單過濾
        // // 平台 IP 白名單過濾
        // // 密碼模式
        // // 密碼最短有效期 (分鐘)
        // // 密碼語法複雜度 (15~28)
        // // 是否忽略大小寫
        // // 密碼最少包含 N 個大寫字母
        // // 密碼最多包含 N 個大寫字母
        // // 密碼最少包含 N 個小寫字母
        // // 密碼最多包含 N 個小寫字母
        // // 允許密碼包含數字
        // // 是否允許密碼以數字開頭
        // // 是否允許密碼以數字結尾
        // // 密碼最少包含 N 個數字
        // // 密碼最多包含 N 個數字
        // // 是否允許密碼包含特殊符號
        // // 是否允許密碼以特殊符號開頭
        // // 是否允許密碼以特殊符號結尾
        // // 密碼最少包含 N 個符號
        // // 密碼最多包含 N 個符號
        // // 密碼最少長度
        // // 密碼最大長度
        // // 密碼最少包含 N 個大寫字母
        // // 密碼最少包含 N 個小寫字母
        // // 弱密碼清單

        return null;
    }

    /**
     * 密碼規則檢查 (登入時)
     * <pre>
     * 首次登入強迫更改密碼
     * 密碼最長有效期 (分鐘)
     * </pre>
     *
     * @param userId      帳號
     * @param bwdParamMap 密碼規則
     * @return
     */
    public BwdRuleType postRuleCheckForLogin(String userId, Map<String, BwdParam> bwdParamMap) {
        final Instant now = Instant.now();

        final List<BwdHistory> bwdHistoryList = this.bwdHistoryRepository.findAllByUserIdOrderByCreateTimeDesc(userId);
        final Optional<LoginCount> loginCount = this.loginCountRepository.findOneByUserId(userId);
        // // N 代密碼不可重複。

        // * 首次登入強迫更改密碼
        final BwdParam forceChange = bwdParamMap.get(BwdRuleType.ForceToChangeBwd.getRuleId());

        if (null != forceChange) {
            Object[] target = { null, -1 };
            if (ArrayUtils.contains(target, loginCount.orElse(new LoginCount()).getFailCount())) {
                BwdRuleType ruleType = BwdRuleType.ForceToChangeBwd.setMsg(this.toBwdGrammarCheckMessage(forceChange));
                return ruleType;
            }
        }

        // * 密碼最長有效期 (分鐘)
        final BwdParam longestLife = bwdParamMap.get(BwdRuleType.BwdMaximumAge.getRuleId());

        if (null != longestLife) {
            int value = Integer.parseInt(longestLife.getParamValue());
            Instant deadline;
            if (CollectionUtils.isNotEmpty(bwdHistoryList)) {
                deadline = bwdHistoryList.get(0).getCreateTime().plusSeconds(60 * value);
            } else {
                Optional<User> user = this.userRepository.findOneByLogin(userId);
                deadline = user.get().getCreatedDate().plusSeconds(60 * value);
            }
            if (now.isAfter(deadline)) {
                BwdRuleType ruleType = BwdRuleType.BwdMaximumAge.setMsg(this.toBwdGrammarCheckMessage(longestLife));
                return ruleType;
            }
        }

        // // 密碼錯誤 N 次鎖定
        // // N 分鐘後自動解鎖
        // // 帳號被管理者鎖定
        // // 組織 IP 白名單過濾
        // // 帳號 IP 白名單過濾
        // // 平台 IP 白名單過濾
        // // 密碼模式
        // // 密碼最短有效期 (分鐘)
        // // 密碼語法複雜度 (15~28)
        // // 是否忽略大小寫
        // // 密碼最少包含 N 個大寫字母
        // // 密碼最多包含 N 個大寫字母
        // // 密碼最少包含 N 個小寫字母
        // // 密碼最多包含 N 個小寫字母
        // // 允許密碼包含數字
        // // 是否允許密碼以數字開頭
        // // 是否允許密碼以數字結尾
        // // 密碼最少包含 N 個數字
        // // 密碼最多包含 N 個數字
        // // 是否允許密碼包含特殊符號
        // // 是否允許密碼以特殊符號開頭
        // // 是否允許密碼以特殊符號結尾
        // // 密碼最少包含 N 個符號
        // // 密碼最多包含 N 個符號
        // // 密碼最少長度
        // // 密碼最大長度
        // // 密碼最少包含 N 個大寫字母
        // // 密碼最少包含 N 個小寫字母
        // // 弱密碼清單

        return null;
    }

    /**
     * 依規則取得訊息
     *
     * @param bwdParam
     * @return
     */
    private String toBwdGrammarCheckMessage(BwdParam bwdParam) {
        final String value = bwdParam.getParamValue();
        // final String rule = bwdRuleType.getRuleName();
        final String rule = bwdParam.getRuleName();
        String message = rule;

        if (StringUtils.isNotBlank(value)) {
            if (StringUtils.contains(rule, "N")) {
                message = StringUtils.replaceOnce(rule, "N", value);
            } else if (StringUtils.equalsAnyIgnoreCase(value, "true", "false")) {
                message = (StringUtils.equalsIgnoreCase("false", value) ? "不" : "") + StringUtils.replaceOnce(rule, "是否", "");
            }
        }

        return message;
    }

    public void increaseFailCount(String userId) {
        Optional<LoginCount> res = this.loginCountRepository.findOneByUserId(userId);

        LoginCount result = res.orElse(new LoginCount().failCount(0));
        result.userId(userId).failCount(result.getFailCount() + 1).updateTime(Instant.now());

        this.loginCountRepository.save(result);
    }

    public void resetFailCount(String userId) {
        this.resetFailCount(userId, false);
    }

    public void resetFailCount(String userId, boolean isForce) {
        Optional<LoginCount> res = this.loginCountRepository.findOneByUserId(userId);

        LoginCount result = res.orElse(new LoginCount());
        result.userId(userId).failCount(isForce ? -1 : 0).updateTime(Instant.now());

        this.loginCountRepository.save(result);
    }

    public void keepBwd(String userId) {
        List<BwdHistory> bwdHistoryList = this.bwdHistoryRepository.findAllByUserIdOrderByCreateTimeDesc(userId);
        Optional<User> userO = this.userRepository.findOneByLogin(userId);
        if (CollectionUtils.isNotEmpty(bwdHistoryList) && userO.isPresent()) {
            BwdHistory record = bwdHistoryList.get(0);
            record.setCreateTime(Instant.now());

            User user = userO.get();
            user.setResetKey(null);
            user.setLastModifiedBy(userId);
            user.setLastModifiedDate(Instant.now());
            this.userRepository.save(user);
        } else {
            String msg =
                "使用者({userId})或Bwd歷史(size={size})找不到".replace("{userId}", userId).replace(
                        "{size}",
                        String.valueOf(bwdHistoryList.size())
                    );
            log.error("沿用密碼失敗: {}", msg);
            throw new RuntimeException("沿用密碼失敗");
        }
    }

    public static int countUpperCaseLetters(String str) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (Character.isUpperCase(c)) {
                count++;
            }
        }
        return count;
    }

    public static int countLowerCaseLetters(String str) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (Character.isLowerCase(c)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 計算字串中包含的符號數量
     * @param input 密碼字串
     * @return 符號數量
     */
    private int countSymbols(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        // 定義符號的集合
        String symbols = "!@#$%^&*()-_=+[]{}|;:'\"\\,.<>?/";
        int count = 0;

        for (char c : input.toCharArray()) {
            if (symbols.indexOf(c) != -1) {
                count++;
            }
        }

        return count;
    }
}
