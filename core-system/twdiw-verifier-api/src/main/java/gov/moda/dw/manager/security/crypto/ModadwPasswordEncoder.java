package gov.moda.dw.manager.security.crypto;

import lombok.extern.slf4j.Slf4j;
import gov.moda.dw.manager.util.EncryptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class ModadwPasswordEncoder implements PasswordEncoder {

  @Override
  public String encode(CharSequence charSequence) {
    return EncryptUtils.gainSHA512(charSequence.toString());
  }

  @Override
  public boolean matches(CharSequence charSequence, String s) {

      // 檢查 charSequence 是否已經是 SHA-512 加密後的值
      if(charSequence.toString().equals(s)){
          return true;
      }
      return charSequence.equals(EncryptUtils.gainSHA512(s));
  }

  public boolean matchesNewBwd(CharSequence charSequence, String s) {
    // 對明文密碼進行加密，然後與已存儲的哈希進行比較。
    return EncryptUtils.gainSHA512(charSequence.toString()).equals(s);
  }
}
