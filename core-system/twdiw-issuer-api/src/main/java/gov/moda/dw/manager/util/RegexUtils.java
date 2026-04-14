package gov.moda.dw.manager.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegexUtils {

    private static final int BASE_TIMEOUT_MS = 200;
    private static final int PER_CHAR_TIMEOUT_MS = 2;
    private static final int MAX_TIMEOUT_MS = 3000;
    private static final int MAX_INPUT_LENGTH = 200;

    /**
     * 驗證輸入是否匹配正則表達式（具超時保護）
     * 
     * @param pattern 正則表達式
     * @param input   待驗證的輸入
     * @return 匹配則返回 Matcher，否則返回 null
     */
    public static Matcher matchWithTimeout(Pattern pattern, String input) {
        // 檢核輸入
        if (StringUtils.isBlank(input)) {
            return null;
        }

        // 根據輸入長度動態計算超時時間
        int timeout = Math.min(BASE_TIMEOUT_MS + (input.length() * PER_CHAR_TIMEOUT_MS), MAX_TIMEOUT_MS);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = null;

        try {
            Matcher matcher = pattern.matcher(input);
            future = executor.submit(matcher::matches);

            boolean isMatch = future.get(timeout, TimeUnit.MILLISECONDS);
            return isMatch ? matcher : null;
        } catch (TimeoutException e) {
            log.warn("Regex matching timeout for input length: {}", input.length());
            if (future != null) {
                future.cancel(true);
            }
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢復中斷狀態
            log.warn("Regex matching interrupted", e);
            if (future != null) {
                future.cancel(true);
            }
            return null;
        } catch (ExecutionException e) {
            log.warn("Regex matching execution error", e);
            return null;
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * 驗證輸入是否匹配正則表達式（具超時保護）
     * 
     * @param pattern 正則表達式
     * @param input   待驗證的輸入
     * @return 返回匹配結果 boolean
     */
    public static boolean isMatchWithTimeout(Pattern pattern, String input) {
        // 檢核輸入
        if (StringUtils.isBlank(input)) {
            return false;
        }

        // 根據輸入長度動態計算超時時間
        int timeout = Math.min(BASE_TIMEOUT_MS + (input.length() * PER_CHAR_TIMEOUT_MS), MAX_TIMEOUT_MS);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = null;

        try {
            Matcher matcher = pattern.matcher(input);
            future = executor.submit(matcher::matches);

            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            log.warn("Regex matching timeout for input length: {}", input.length());
            if (future != null) {
                future.cancel(true);
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢復中斷狀態
            log.warn("Regex matching interrupted", e);
            if (future != null) {
                future.cancel(true);
            }
            return false;
        } catch (ExecutionException e) {
            log.warn("Regex matching execution error", e);
            return false;
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * 編譯正則表達式（具輸入長度限制及超時保護）
     *
     * @param regex 正則表達式字串
     * @return 若編譯成功則回傳 Pattern，若超時或錯誤則回傳 null
     */
    public static Pattern matchWithTimeout(String regex) {
        if (StringUtils.isBlank(regex) || regex.length() > MAX_INPUT_LENGTH) {
            return null;
        }

        // 設定超時時間：根據 regex 長度計算，但不超過上限
        int timeout = Math.min(BASE_TIMEOUT_MS + regex.length() * 5, MAX_TIMEOUT_MS);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Pattern> future = null;

        try {
            // 嘗試在獨立執行緒中編譯 regex
            future = executor.submit(() -> Pattern.compile(regex));
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            log.warn("Regex compilation timeout for pattern length: {}", regex.length());
            if (future != null) {
                future.cancel(true);
            }
            return null;
        } catch (PatternSyntaxException e) {
            log.warn("Invalid regex pattern: {}", regex, e);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢復中斷狀態
            log.warn("Regex compilation interrupted: {}", regex, e);
            return null;
        } catch (ExecutionException e) {
            log.warn("Regex compilation execution error: {}", regex, e);
            return null;
        } finally {
            executor.shutdownNow();
        }
    }

}
