package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link gov.moda.dw.manager.domain.Org} entity. This class is used
 * in {@link gov.moda.dw.manager.web.rest.OrgResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /orgs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrgCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter orgId;

    private StringFilter orgTwName;

    private StringFilter orgEnName;

    private StringFilter metadata;

    private RangeFilter<LocalDateTime> createTime;

    private InstantFilter updateTime;

    private Boolean distinct;

    public OrgCriteria() {}

    public OrgCriteria(OrgCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.orgId = other.optionalOrgId().map(StringFilter::copy).orElse(null);
        this.orgTwName = other.optionalOrgTwName().map(StringFilter::copy).orElse(null);
        this.orgEnName = other.optionalOrgEnName().map(StringFilter::copy).orElse(null);
        this.metadata = other.optionalMetadata().map(StringFilter::copy).orElse(null);
        this.createTime = other.optionalCreateTime().map(RangeFilter::copy).orElse(null);
        this.updateTime = other.optionalUpdateTime().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OrgCriteria copy() {
        return new OrgCriteria(this);
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

    public StringFilter getOrgId() {
        return orgId;
    }

    public Optional<StringFilter> optionalOrgId() {
        return Optional.ofNullable(orgId);
    }

    public StringFilter orgId() {
        if (orgId == null) {
            setOrgId(new StringFilter());
        }
        return orgId;
    }

    public void setOrgId(StringFilter orgId) {
        this.orgId = orgId;
    }

    public StringFilter getOrgTwName() {
        return orgTwName;
    }

    public Optional<StringFilter> optionalOrgTwName() {
        return Optional.ofNullable(orgTwName);
    }

    public StringFilter orgTwName() {
        if (orgTwName == null) {
            setOrgTwName(new StringFilter());
        }
        return orgTwName;
    }

    public void setOrgTwName(StringFilter orgTwName) {
        this.orgTwName = orgTwName;
    }

    public StringFilter getOrgEnName() {
        return orgEnName;
    }

    public Optional<StringFilter> optionalOrgEnName() {
        return Optional.ofNullable(orgEnName);
    }

    public StringFilter orgEnName() {
        if (orgEnName == null) {
            setOrgEnName(new StringFilter());
        }
        return orgEnName;
    }

    public void setOrgEnName(StringFilter orgEnName) {
        this.orgEnName = orgEnName;
    }

    public StringFilter getMetadata() {
        return metadata;
    }

    public Optional<StringFilter> optionalMetadata() {
        return Optional.ofNullable(metadata);
    }

    public StringFilter metadata() {
        if (metadata == null) {
            setMetadata(new StringFilter());
        }
        return metadata;
    }

    public void setMetadata(StringFilter metadata) {
        this.metadata = metadata;
    }


    public RangeFilter getCreateTime() {
        return createTime;
    }

    public Optional<RangeFilter> optionalCreateTime() {
        return Optional.ofNullable(createTime);
    }

    public RangeFilter createTime() {
        if (createTime == null) {
            setCreateTime(new RangeFilter());
        }
        return createTime;
    }

    public void setCreateTime(RangeFilter<LocalDateTime> createTime) {
        this.createTime = createTime;
    }

    public InstantFilter getUpdateTime() {
        return updateTime;
    }

    public Optional<InstantFilter> optionalUpdateTime() {
        return Optional.ofNullable(updateTime);
    }

    public InstantFilter updateTime() {
        if (updateTime == null) {
            setUpdateTime(new InstantFilter());
        }
        return updateTime;
    }

    public void setUpdateTime(InstantFilter updateTime) {
        this.updateTime = updateTime;
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
        final OrgCriteria that = (OrgCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(orgId, that.orgId) &&
            Objects.equals(orgTwName, that.orgTwName) &&
            Objects.equals(orgEnName, that.orgEnName) &&
            Objects.equals(metadata, that.metadata) &&
            Objects.equals(createTime, that.createTime) &&
            Objects.equals(updateTime, that.updateTime) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orgId, orgTwName, orgEnName, metadata, createTime, updateTime, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return 
            "OrgCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOrgId().map(f -> "orgId=" + f + ", ").orElse("") +
            optionalOrgTwName().map(f -> "orgTwName=" + f + ", ").orElse("") +
            optionalOrgEnName().map(f -> "orgEnName=" + f + ", ").orElse("") +
            optionalMetadata().map(f -> "metadata=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalUpdateTime().map(f -> "updateTime=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") + "}";
    }
}
