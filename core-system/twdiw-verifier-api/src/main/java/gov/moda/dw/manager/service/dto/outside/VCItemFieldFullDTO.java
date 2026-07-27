package gov.moda.dw.manager.service.dto.outside;

import gov.moda.dw.manager.domain.outside.VCItemField;
import gov.moda.dw.manager.domain.outside.VcManagerVCItem;

public class VCItemFieldFullDTO {

  VCItemField vcf;

  VcManagerVCItem vc;

  public VCItemFieldFullDTO(VCItemField vcf, VcManagerVCItem vc) {
    this.vcf = vcf;
    this.vc = vc;
  }

  public VCItemField getVcf() {
    return vcf;
  }

  public void setVcf(VCItemField vcf) {
    this.vcf = vcf;
  }

  public VcManagerVCItem getVc() {
    return vc;
  }

  public void setVc(VcManagerVCItem vc) {
    this.vc = vc;
  }
}
