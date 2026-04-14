package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.VCItem} entity. This class is used
 * in {@link gov.moda.dw.manager.web.rest.VCItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /vc-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter serialNo;

    private StringFilter name;

    private LongFilter crUser;

    private InstantFilter crDatetime;

    private LongFilter categoryId;

    private StringFilter businessId;

    private StringFilter schemaId;

    private StringFilter unitTypeExpire;

    private IntegerFilter lengthExpire;

    private Boolean distinct;

    private BooleanFilter used;

    // private StringFilter apiType;

    private StringFilter httpMethod;

    private BooleanFilter expose;

    private StringFilter ial;

    private BooleanFilter isVerify;

    private StringFilter type;

    private StringFilter issuerServiceUrl;

    private BooleanFilter isTemp;

    private BooleanFilter activated;

    public VCItemCriteria() {}

    public VCItemCriteria(VCItemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.serialNo = other.optionalSerialNo().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.crUser = other.optionalCrUser().map(LongFilter::copy).orElse(null);
        this.crDatetime = other.optionalCrDatetime().map(InstantFilter::copy).orElse(null);
        this.categoryId = other.optionalCategoryId().map(LongFilter::copy).orElse(null);
        this.businessId = other.optionalBusinessId().map(StringFilter::copy).orElse(null);
        this.schemaId = other.optionalSchemaId().map(StringFilter::copy).orElse(null);
        this.unitTypeExpire = other.optionalUnitTypeExpire().map(StringFilter::copy).orElse(null);
        this.lengthExpire = other.optionalLengthExpire().map(IntegerFilter::copy).orElse(null);
        this.distinct = other.distinct;
        this.used = other.optionalUsed().map(BooleanFilter::copy).orElse(null);
        this.expose = other.optionalExpose().map(BooleanFilter::copy).orElse(null);
        this.ial = other.optionalIal().map(StringFilter::copy).orElse(null);
        this.isVerify = other.optionalIsVerify().map(BooleanFilter::copy).orElse(null);
        this.type = other.optionalType().map(StringFilter::copy).orElse(null);
        this.issuerServiceUrl = other.optionalIssuerServiceUrl().map(StringFilter::copy).orElse(null);
        this.isTemp = other.optionalIsTemp().map(BooleanFilter::copy).orElse(null);
        this.activated = other.optionalIsTemp().map(BooleanFilter::copy).orElse(null);
    }

    @Override
    public VCItemCriteria copy() {
        return new VCItemCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getSerialNo() {
        return serialNo;
    }

    public Optional<StringFilter> optionalSerialNo() {
        return Optional.ofNullable(serialNo);
    }

    public StringFilter serialNo() {
        if (serialNo == null) {
            setSerialNo(new StringFilter());
        }
        return serialNo;
    }

    public void setSerialNo(StringFilter serialNo) {
        this.serialNo = serialNo;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LongFilter getCrUser() {
        return crUser;
    }

    public Optional<LongFilter> optionalCrUser() {
        return Optional.ofNullable(crUser);
    }

    public LongFilter crUser() {
        if (crUser == null) {
            setCrUser(new LongFilter());
        }
        return crUser;
    }

    public void setCrUser(LongFilter crUser) {
        this.crUser = crUser;
    }

    public InstantFilter getCrDatetime() {
        return crDatetime;
    }

    public Optional<InstantFilter> optionalCrDatetime() {
        return Optional.ofNullable(crDatetime);
    }

    public InstantFilter crDatetime() {
        if (crDatetime == null) {
            setCrDatetime(new InstantFilter());
        }
        return crDatetime;
    }

    public void setCrDatetime(InstantFilter crDatetime) {
        this.crDatetime = crDatetime;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public Optional<LongFilter> optionalCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            setCategoryId(new LongFilter());
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public StringFilter getBusinessId() {
        return businessId;
    }

    public Optional<StringFilter> optionalBusinessId() {
        return Optional.ofNullable(businessId);
    }

    public StringFilter businessId() {
        if (businessId == null) {
            setBusinessId(new StringFilter());
        }
        return businessId;
    }

    public void setBusinessId(StringFilter businessId) {
        this.businessId = businessId;
    }

    public StringFilter getSchemaId() {
        return schemaId;
    }

    public Optional<StringFilter> optionalSchemaId() {
        return Optional.ofNullable(schemaId);
    }

    public StringFilter schemaId() {
        if (schemaId == null) {
            setSchemaId(new StringFilter());
        }
        return schemaId;
    }

    public void setSchemaId(StringFilter schemaId) {
        this.schemaId = schemaId;
    }

    public StringFilter getUnitTypeExpire() {
        return unitTypeExpire;
    }

    public Optional<StringFilter> optionalUnitTypeExpire() {
        return Optional.ofNullable(unitTypeExpire);
    }

    public StringFilter unitTypeExpire() {
        if (unitTypeExpire == null) {
            setUnitTypeExpire(new StringFilter());
        }
        return unitTypeExpire;
    }

    public void setUnitTypeExpire(StringFilter unitTypeExpire) {
        this.unitTypeExpire = unitTypeExpire;
    }

    public IntegerFilter getLengthExpire() {
        return lengthExpire;
    }

    public Optional<IntegerFilter> optionalLengthExpire() {
        return Optional.ofNullable(lengthExpire);
    }

    public IntegerFilter lengthExpire() {
        if (lengthExpire == null) {
            setLengthExpire(new IntegerFilter());
        }
        return lengthExpire;
    }

    public void setLengthExpire(IntegerFilter lengthExpire) {
        this.lengthExpire = lengthExpire;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    public Optional<BooleanFilter> optionalUsed() {
        return Optional.ofNullable(used);
    }

    public BooleanFilter used() {
        if (used == null) {
            setUsed(new BooleanFilter());
        }
        return used;
    }

    public void setUsed(BooleanFilter used) {
        this.used = used;
    }

    public Optional<BooleanFilter> optionalExpose() {
        return Optional.ofNullable(expose);
    }

    public BooleanFilter getExpose() {
        if (expose == null) {
            setExpose(new BooleanFilter());
        }
        return expose;
    }

    public void setExpose(BooleanFilter expose) {
        this.expose = expose;
    }

    // public StringFilter getApiType() {
    //     return apiType;
    // }
    //
    // public StringFilter apiType() {
    //     if (apiType == null) {
    //         apiType = new StringFilter();
    //     }
    //     return apiType;
    // }

    // public void setApiType(StringFilter apiType) {
    //     this.apiType = apiType;
    // }

    // public Optional<StringFilter> optionalApiType() {
    //     return Optional.ofNullable(this.apiType);
    // }

    public StringFilter getHttpMethod() {
        return httpMethod;
    }

    public StringFilter httpMethod() {
        if (httpMethod == null) {
            httpMethod = new StringFilter();
        }
        return httpMethod;
    }

    public void setHttpMethod(StringFilter httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Optional<StringFilter> optionalHttpMethod() {
        return Optional.ofNullable(this.httpMethod);
    }
    
    public StringFilter getIal() {
        return ial;
    }

    public Optional<StringFilter> optionalIal() {
        return Optional.ofNullable(ial);
    }

    public StringFilter ial() {
        if (ial == null) {
            setIal(new StringFilter());
        }
        return ial;
    }

    public void setIal(StringFilter ial) {
        this.ial = ial;
    }

    public BooleanFilter getIsVerify() {
        return isVerify;
    }

    public Optional<BooleanFilter> optionalIsVerify() {
        return Optional.ofNullable(isVerify);
    }

    public BooleanFilter isVerify() {
        if (isVerify == null) {
            setIsVerify(new BooleanFilter());
        }
        return isVerify;
    }

    public void setIsVerify(BooleanFilter isVerify) {
        this.isVerify = isVerify;
    }

    public StringFilter getType() {
        return type;
    }

    public Optional<StringFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public StringFilter type() {
        if (type == null) {
            setType(new StringFilter());
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public StringFilter getIssuerServiceUrl() {
        return issuerServiceUrl;
    }

    public Optional<StringFilter> optionalIssuerServiceUrl() {
        return Optional.ofNullable(issuerServiceUrl);
    }

    public StringFilter issuerServiceUrl() {
        if (issuerServiceUrl == null) {
            setIssuerServiceUrl(new StringFilter());
        }
        return issuerServiceUrl;
    }

    public void setIssuerServiceUrl(StringFilter issuerServiceUrl) {
        this.issuerServiceUrl = issuerServiceUrl;
    }

    public BooleanFilter getIsTemp() {
        return isTemp;
    }

    public Optional<BooleanFilter> optionalIsTemp() {
        return Optional.ofNullable(isTemp);
    }

    public BooleanFilter isTemp() {
        if (isTemp == null) {
            setIsTemp(new BooleanFilter());
        }
        return isTemp;
    }

    public void setIsTemp(BooleanFilter isTemp) {
        this.isTemp = isTemp;
    }

    public BooleanFilter getActivated() {
        return activated;
    }

    public Optional<BooleanFilter> optionalActivated() {
        return Optional.ofNullable(activated);
    }

    public BooleanFilter activated() {
        if (activated == null) {
            setActivated(new BooleanFilter());
        }
        return activated;
    }

    public void setActivated(BooleanFilter activated) {
        this.activated = activated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final VCItemCriteria that = (VCItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(serialNo, that.serialNo) &&
            Objects.equals(name, that.name) &&
            Objects.equals(crUser, that.crUser) &&
            Objects.equals(crDatetime, that.crDatetime) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(businessId, that.businessId) &&
            Objects.equals(schemaId, that.schemaId) &&
            Objects.equals(unitTypeExpire, that.unitTypeExpire) &&
            Objects.equals(lengthExpire, that.lengthExpire) &&
            Objects.equals(used, that.used) &&
            // Objects.equals(apiType, that.apiType) &&
            Objects.equals(httpMethod, that.httpMethod) &&
            Objects.equals(ial, that.ial) &&
            Objects.equals(isVerify, that.isVerify) &&
            Objects.equals(type, that.type) &&
            Objects.equals(issuerServiceUrl, that.issuerServiceUrl) &&
            Objects.equals(isTemp, that.isTemp) &&
            Objects.equals(activated, that.activated)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            serialNo,
            name,
            crUser,
            crDatetime,
            categoryId,
            businessId,
            schemaId,
            unitTypeExpire,
            lengthExpire,
            used,
            // apiType,
            httpMethod,
            ial,
            isVerify,
            type,
            issuerServiceUrl,
            isTemp,
            activated
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCItemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSerialNo().map(f -> "serialNo=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalCrUser().map(f -> "crUser=" + f + ", ").orElse("") +
            optionalCrDatetime().map(f -> "crDatetime=" + f + ", ").orElse("") +
            optionalCategoryId().map(f -> "categoryId=" + f + ", ").orElse("") +
            optionalBusinessId().map(f -> "businessId=" + f + ", ").orElse("") +
            optionalSchemaId().map(f -> "schemaId=" + f + ", ").orElse("") +
            optionalUnitTypeExpire().map(f -> "unitTypeExpire=" + f + ", ").orElse("") +
            optionalLengthExpire().map(f -> "lengthExpire=" + f + ", ").orElse("") +
            optionalIal().map(f -> "ial=" + f + ", ").orElse("") +
            optionalIsVerify().map(f -> "isVerify=" + f + ", ").orElse("") +
            optionalIsTemp().map(f -> "isTemp=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            optionalUsed().map(f -> "used=" + f).orElse("") +
            // optionalApiType().map(f -> "apiType=" + f).orElse("") +
            optionalHttpMethod().map(f -> "httpMethod=" + f).orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalIssuerServiceUrl().map(f -> "schemaId=" + f + ", ").orElse("") +
            optionalActivated().map(f -> "activated=" + f + ", ").orElse("") +
            "}";
    }
}
