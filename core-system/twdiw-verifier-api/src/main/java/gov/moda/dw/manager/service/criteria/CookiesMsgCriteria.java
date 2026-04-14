package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CookiesMsgCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter cid;

    private StringFilter msg;

    private Boolean distinct;

    public CookiesMsgCriteria() {}

    public CookiesMsgCriteria(CookiesMsgCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.cid = other.optionalCid().map(StringFilter::copy).orElse(null);
        this.msg = other.optionalMsg().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CookiesMsgCriteria copy() {
        return new CookiesMsgCriteria(this);
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

    public StringFilter getCid() {
        return cid;
    }

    public Optional<StringFilter> optionalCid() {
        return Optional.ofNullable(cid);
    }

    public StringFilter cid() {
        if (cid == null) {
            setCid(new StringFilter());
        }
        return cid;
    }

    public void setCid(StringFilter cid) {
        this.cid = cid;
    }

    public StringFilter getMsg() {
        return msg;
    }

    public Optional<StringFilter> optionalMsg() {
        return Optional.ofNullable(msg);
    }

    public StringFilter msg() {
        if (msg == null) {
            setMsg(new StringFilter());
        }
        return msg;
    }

    public void setMsg(StringFilter msg) {
        this.msg = msg;
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
        final CookiesMsgCriteria that = (CookiesMsgCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(cid, that.cid) &&
            Objects.equals(msg, that.msg) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cid, msg, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CookiesMsgCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCid().map(f -> "cid=" + f + ", ").orElse("") +
            optionalMsg().map(f -> "msg=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
