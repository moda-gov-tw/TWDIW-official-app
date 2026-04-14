package gov.moda.dw.manager.domain.outside.vdr.category;


import gov.moda.dw.manager.domain.outside.VcManagerOrg;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryVdrSimpleDto {

    String name;

    String nameEn;

    String info;

    String taxId;

    public static CategoryVdrSimpleDto valueOf(DataDid dataDid) {
        return CategoryVdrSimpleDto.builder()
                .name(dataDid.getOrg().getName())
                .nameEn(dataDid.getOrg().getNameEn())
                .info(dataDid.getOrg().getInfo())
                .taxId(dataDid.getOrg().getTaxId())
                .build();
    }

    public static CategoryVdrSimpleDto valueOf(VcManagerOrg vcManagerOrg) {
        return CategoryVdrSimpleDto.builder()
                .name(vcManagerOrg.getOrgTwName())
                .nameEn(vcManagerOrg.getOrgEnName())
                .taxId(vcManagerOrg.getOrgId())
                .build();
    }

}
