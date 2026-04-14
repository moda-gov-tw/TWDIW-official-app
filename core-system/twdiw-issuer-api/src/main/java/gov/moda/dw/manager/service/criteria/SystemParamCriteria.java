package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.SystemParam} entity. This class is used
 * in {@link gov.moda.dw.manager.web.rest.SystemParamResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /system-params?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SystemParamCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter sysType;

    private StringFilter sysCode;

    private StringFilter sysName;

    private StringFilter sysEffect;

    private StringFilter col1;

    private StringFilter col2;

    private StringFilter col3;

    private StringFilter col4;

    private StringFilter col5;

    private StringFilter col6;

    private StringFilter col7;

    private StringFilter col8;

    private StringFilter col9;

    private StringFilter col10;

    private StringFilter status;

    private StringFilter createID;

    private InstantFilter modifyTime;

    private InstantFilter createTime;

    private Boolean distinct;

    public SystemParamCriteria() {}

    public SystemParamCriteria(SystemParamCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.sysType = other.optionalSysType().map(StringFilter::copy).orElse(null);
        this.sysCode = other.optionalSysCode().map(StringFilter::copy).orElse(null);
        this.sysName = other.optionalSysName().map(StringFilter::copy).orElse(null);
        this.sysEffect = other.optionalSysEffect().map(StringFilter::copy).orElse(null);
        this.col1 = other.optionalCol1().map(StringFilter::copy).orElse(null);
        this.col2 = other.optionalCol2().map(StringFilter::copy).orElse(null);
        this.col3 = other.optionalCol3().map(StringFilter::copy).orElse(null);
        this.col4 = other.optionalCol4().map(StringFilter::copy).orElse(null);
        this.col5 = other.optionalCol5().map(StringFilter::copy).orElse(null);
        this.col6 = other.optionalCol6().map(StringFilter::copy).orElse(null);
        this.col7 = other.optionalCol7().map(StringFilter::copy).orElse(null);
        this.col8 = other.optionalCol8().map(StringFilter::copy).orElse(null);
        this.col9 = other.optionalCol9().map(StringFilter::copy).orElse(null);
        this.col10 = other.optionalCol10().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.createID = other.optionalCreateID().map(StringFilter::copy).orElse(null);
        this.modifyTime = other.optionalModifyTime().map(InstantFilter::copy).orElse(null);
        this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SystemParamCriteria copy() {
        return new SystemParamCriteria(this);
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

    public StringFilter getSysType() {
        return sysType;
    }

    public Optional<StringFilter> optionalSysType() {
        return Optional.ofNullable(sysType);
    }

    public StringFilter sysType() {
        if (sysType == null) {
            setSysType(new StringFilter());
        }
        return sysType;
    }

    public void setSysType(StringFilter sysType) {
        this.sysType = sysType;
    }

    public StringFilter getSysCode() {
        return sysCode;
    }

    public Optional<StringFilter> optionalSysCode() {
        return Optional.ofNullable(sysCode);
    }

    public StringFilter sysCode() {
        if (sysCode == null) {
            setSysCode(new StringFilter());
        }
        return sysCode;
    }

    public void setSysCode(StringFilter sysCode) {
        this.sysCode = sysCode;
    }

    public StringFilter getSysName() {
        return sysName;
    }

    public Optional<StringFilter> optionalSysName() {
        return Optional.ofNullable(sysName);
    }

    public StringFilter sysName() {
        if (sysName == null) {
            setSysName(new StringFilter());
        }
        return sysName;
    }

    public void setSysName(StringFilter sysName) {
        this.sysName = sysName;
    }

    public StringFilter getSysEffect() {
        return sysEffect;
    }

    public Optional<StringFilter> optionalSysEffect() {
        return Optional.ofNullable(sysEffect);
    }

    public StringFilter sysEffect() {
        if (sysEffect == null) {
            setSysEffect(new StringFilter());
        }
        return sysEffect;
    }

    public void setSysEffect(StringFilter sysEffect) {
        this.sysEffect = sysEffect;
    }

    public StringFilter getCol1() {
        return col1;
    }

    public Optional<StringFilter> optionalCol1() {
        return Optional.ofNullable(col1);
    }

    public StringFilter col1() {
        if (col1 == null) {
            setCol1(new StringFilter());
        }
        return col1;
    }

    public void setCol1(StringFilter col1) {
        this.col1 = col1;
    }

    public StringFilter getCol2() {
        return col2;
    }

    public Optional<StringFilter> optionalCol2() {
        return Optional.ofNullable(col2);
    }

    public StringFilter col2() {
        if (col2 == null) {
            setCol2(new StringFilter());
        }
        return col2;
    }

    public void setCol2(StringFilter col2) {
        this.col2 = col2;
    }

    public StringFilter getCol3() {
        return col3;
    }

    public Optional<StringFilter> optionalCol3() {
        return Optional.ofNullable(col3);
    }

    public StringFilter col3() {
        if (col3 == null) {
            setCol3(new StringFilter());
        }
        return col3;
    }

    public void setCol3(StringFilter col3) {
        this.col3 = col3;
    }

    public StringFilter getCol4() {
        return col4;
    }

    public Optional<StringFilter> optionalCol4() {
        return Optional.ofNullable(col4);
    }

    public StringFilter col4() {
        if (col4 == null) {
            setCol4(new StringFilter());
        }
        return col4;
    }

    public void setCol4(StringFilter col4) {
        this.col4 = col4;
    }

    public StringFilter getCol5() {
        return col5;
    }

    public Optional<StringFilter> optionalCol5() {
        return Optional.ofNullable(col5);
    }

    public StringFilter col5() {
        if (col5 == null) {
            setCol5(new StringFilter());
        }
        return col5;
    }

    public void setCol5(StringFilter col5) {
        this.col5 = col5;
    }

    public StringFilter getCol6() {
        return col6;
    }

    public Optional<StringFilter> optionalCol6() {
        return Optional.ofNullable(col6);
    }

    public StringFilter col6() {
        if (col6 == null) {
            setCol6(new StringFilter());
        }
        return col6;
    }

    public void setCol6(StringFilter col6) {
        this.col6 = col6;
    }

    public StringFilter getCol7() {
        return col7;
    }

    public Optional<StringFilter> optionalCol7() {
        return Optional.ofNullable(col7);
    }

    public StringFilter col7() {
        if (col7 == null) {
            setCol7(new StringFilter());
        }
        return col7;
    }

    public void setCol7(StringFilter col7) {
        this.col7 = col7;
    }

    public StringFilter getCol8() {
        return col8;
    }

    public Optional<StringFilter> optionalCol8() {
        return Optional.ofNullable(col8);
    }

    public StringFilter col8() {
        if (col8 == null) {
            setCol8(new StringFilter());
        }
        return col8;
    }

    public void setCol8(StringFilter col8) {
        this.col8 = col8;
    }

    public StringFilter getCol9() {
        return col9;
    }

    public Optional<StringFilter> optionalCol9() {
        return Optional.ofNullable(col9);
    }

    public StringFilter col9() {
        if (col9 == null) {
            setCol9(new StringFilter());
        }
        return col9;
    }

    public void setCol9(StringFilter col9) {
        this.col9 = col9;
    }

    public StringFilter getCol10() {
        return col10;
    }

    public Optional<StringFilter> optionalCol10() {
        return Optional.ofNullable(col10);
    }

    public StringFilter col10() {
        if (col10 == null) {
            setCol10(new StringFilter());
        }
        return col10;
    }

    public void setCol10(StringFilter col10) {
        this.col10 = col10;
    }

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public StringFilter getCreateID() {
        return createID;
    }

    public Optional<StringFilter> optionalCreateID() {
        return Optional.ofNullable(createID);
    }

    public StringFilter createID() {
        if (createID == null) {
            setCreateID(new StringFilter());
        }
        return createID;
    }

    public void setCreateID(StringFilter createID) {
        this.createID = createID;
    }

    public InstantFilter getModifyTime() {
        return modifyTime;
    }

    public Optional<InstantFilter> optionalModifyTime() {
        return Optional.ofNullable(modifyTime);
    }

    public InstantFilter modifyTime() {
        if (modifyTime == null) {
            setModifyTime(new InstantFilter());
        }
        return modifyTime;
    }

    public void setModifyTime(InstantFilter modifyTime) {
        this.modifyTime = modifyTime;
    }

    public InstantFilter getCreateTime() {
        return createTime;
    }

    public Optional<InstantFilter> optionalCreateTime() {
        return Optional.ofNullable(createTime);
    }

    public InstantFilter createTime() {
        if (createTime == null) {
            setCreateTime(new InstantFilter());
        }
        return createTime;
    }

    public void setCreateTime(InstantFilter createTime) {
        this.createTime = createTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SystemParamCriteria that = (SystemParamCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sysType, that.sysType) &&
            Objects.equals(sysCode, that.sysCode) &&
            Objects.equals(sysName, that.sysName) &&
            Objects.equals(sysEffect, that.sysEffect) &&
            Objects.equals(col1, that.col1) &&
            Objects.equals(col2, that.col2) &&
            Objects.equals(col3, that.col3) &&
            Objects.equals(col4, that.col4) &&
            Objects.equals(col5, that.col5) &&
            Objects.equals(col6, that.col6) &&
            Objects.equals(col7, that.col7) &&
            Objects.equals(col8, that.col8) &&
            Objects.equals(col9, that.col9) &&
            Objects.equals(col10, that.col10) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createID, that.createID) &&
            Objects.equals(modifyTime, that.modifyTime) &&
            Objects.equals(createTime, that.createTime) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            sysType,
            sysCode,
            sysName,
            sysEffect,
            col1,
            col2,
            col3,
            col4,
            col5,
            col6,
            col7,
            col8,
            col9,
            col10,
            status,
            createID,
            modifyTime,
            createTime,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemParamCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSysType().map(f -> "sysType=" + f + ", ").orElse("") +
            optionalSysCode().map(f -> "sysCode=" + f + ", ").orElse("") +
            optionalSysName().map(f -> "sysName=" + f + ", ").orElse("") +
            optionalSysEffect().map(f -> "sysEffect=" + f + ", ").orElse("") +
            optionalCol1().map(f -> "col1=" + f + ", ").orElse("") +
            optionalCol2().map(f -> "col2=" + f + ", ").orElse("") +
            optionalCol3().map(f -> "col3=" + f + ", ").orElse("") +
            optionalCol4().map(f -> "col4=" + f + ", ").orElse("") +
            optionalCol5().map(f -> "col5=" + f + ", ").orElse("") +
            optionalCol6().map(f -> "col6=" + f + ", ").orElse("") +
            optionalCol7().map(f -> "col7=" + f + ", ").orElse("") +
            optionalCol8().map(f -> "col8=" + f + ", ").orElse("") +
            optionalCol9().map(f -> "col9=" + f + ", ").orElse("") +
            optionalCol10().map(f -> "col10=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCreateID().map(f -> "createID=" + f + ", ").orElse("") +
            optionalModifyTime().map(f -> "modifyTime=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
