package gov.moda.dw.verifier.vc.type;

import java.time.ZoneId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ZoneType {
  GMT(ZoneId.of("GMT"), "國際標準時區"),

  TAIPEI(ZoneId.of("Asia/Taipei"), "台灣台北");

  @Getter
  private ZoneId zoneId;

  @Getter
  private String name;
}
