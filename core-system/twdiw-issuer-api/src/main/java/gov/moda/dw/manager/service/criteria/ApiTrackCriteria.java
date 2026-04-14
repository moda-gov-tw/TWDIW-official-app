package gov.moda.dw.manager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApiTrackCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter uuid;

    private InstantFilter timestamp;

    private StringFilter source;

    private StringFilter serviceId;

    private StringFilter uri;

    private StringFilter url;

    private StringFilter statusCode;

    private StringFilter rtt;

    private StringFilter requestMethod;

    private StringFilter accessToken1;

    private StringFilter accessToken2;

    private StringFilter jhiFrom;

    private StringFilter jhiTo;

    private StringFilter cost;

    private StringFilter charged;

    private StringFilter synced;

    private Boolean distinct;

    public ApiTrackCriteria() {}

    public ApiTrackCriteria(ApiTrackCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.uuid = other.optionalUuid().map(StringFilter::copy).orElse(null);
        this.timestamp = other.optionalTimestamp().map(InstantFilter::copy).orElse(null);
        this.source = other.optionalSource().map(StringFilter::copy).orElse(null);
        this.serviceId = other.optionalServiceId().map(StringFilter::copy).orElse(null);
        this.uri = other.optionalUri().map(StringFilter::copy).orElse(null);
        this.url = other.optionalUrl().map(StringFilter::copy).orElse(null);
        this.statusCode = other.optionalStatusCode().map(StringFilter::copy).orElse(null);
        this.rtt = other.optionalRtt().map(StringFilter::copy).orElse(null);
        this.requestMethod = other.optionalRequestMethod().map(StringFilter::copy).orElse(null);
        this.accessToken1 = other.optionalAccessToken1().map(StringFilter::copy).orElse(null);
        this.accessToken2 = other.optionalAccessToken2().map(StringFilter::copy).orElse(null);
        this.jhiFrom = other.optionalJhiFrom().map(StringFilter::copy).orElse(null);
        this.jhiTo = other.optionalJhiTo().map(StringFilter::copy).orElse(null);
        this.cost = other.optionalCost().map(StringFilter::copy).orElse(null);
        this.charged = other.optionalCharged().map(StringFilter::copy).orElse(null);
        this.synced = other.optionalSynced().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ApiTrackCriteria copy() {
        return new ApiTrackCriteria(this);
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

    public StringFilter getUuid() {
        return uuid;
    }

    public Optional<StringFilter> optionalUuid() {
        return Optional.ofNullable(uuid);
    }

    public StringFilter uuid() {
        if (uuid == null) {
            setUuid(new StringFilter());
        }
        return uuid;
    }

    public void setUuid(StringFilter uuid) {
        this.uuid = uuid;
    }

    public InstantFilter getTimestamp() {
        return timestamp;
    }

    public Optional<InstantFilter> optionalTimestamp() {
        return Optional.ofNullable(timestamp);
    }

    public InstantFilter timestamp() {
        if (timestamp == null) {
            setTimestamp(new InstantFilter());
        }
        return timestamp;
    }

    public void setTimestamp(InstantFilter timestamp) {
        this.timestamp = timestamp;
    }

    public StringFilter getSource() {
        return source;
    }

    public Optional<StringFilter> optionalSource() {
        return Optional.ofNullable(source);
    }

    public StringFilter source() {
        if (source == null) {
            setSource(new StringFilter());
        }
        return source;
    }

    public void setSource(StringFilter source) {
        this.source = source;
    }

    public StringFilter getServiceId() {
        return serviceId;
    }

    public Optional<StringFilter> optionalServiceId() {
        return Optional.ofNullable(serviceId);
    }

    public StringFilter serviceId() {
        if (serviceId == null) {
            setServiceId(new StringFilter());
        }
        return serviceId;
    }

    public void setServiceId(StringFilter serviceId) {
        this.serviceId = serviceId;
    }

    public StringFilter getUri() {
        return uri;
    }

    public Optional<StringFilter> optionalUri() {
        return Optional.ofNullable(uri);
    }

    public StringFilter uri() {
        if (uri == null) {
            setUri(new StringFilter());
        }
        return uri;
    }

    public void setUri(StringFilter uri) {
        this.uri = uri;
    }

    public StringFilter getUrl() {
        return url;
    }

    public Optional<StringFilter> optionalUrl() {
        return Optional.ofNullable(url);
    }

    public StringFilter url() {
        if (url == null) {
            setUrl(new StringFilter());
        }
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public StringFilter getStatusCode() {
        return statusCode;
    }

    public Optional<StringFilter> optionalStatusCode() {
        return Optional.ofNullable(statusCode);
    }

    public StringFilter statusCode() {
        if (statusCode == null) {
            setStatusCode(new StringFilter());
        }
        return statusCode;
    }

    public void setStatusCode(StringFilter statusCode) {
        this.statusCode = statusCode;
    }

    public StringFilter getRtt() {
        return rtt;
    }

    public Optional<StringFilter> optionalRtt() {
        return Optional.ofNullable(rtt);
    }

    public StringFilter rtt() {
        if (rtt == null) {
            setRtt(new StringFilter());
        }
        return rtt;
    }

    public void setRtt(StringFilter rtt) {
        this.rtt = rtt;
    }

    public StringFilter getRequestMethod() {
        return requestMethod;
    }

    public Optional<StringFilter> optionalRequestMethod() {
        return Optional.ofNullable(requestMethod);
    }

    public StringFilter requestMethod() {
        if (requestMethod == null) {
            setRequestMethod(new StringFilter());
        }
        return requestMethod;
    }

    public void setRequestMethod(StringFilter requestMethod) {
        this.requestMethod = requestMethod;
    }

    public StringFilter getAccessToken1() {
        return accessToken1;
    }

    public Optional<StringFilter> optionalAccessToken1() {
        return Optional.ofNullable(accessToken1);
    }

    public StringFilter accessToken1() {
        if (accessToken1 == null) {
            setAccessToken1(new StringFilter());
        }
        return accessToken1;
    }

    public void setAccessToken1(StringFilter accessToken1) {
        this.accessToken1 = accessToken1;
    }

    public StringFilter getAccessToken2() {
        return accessToken2;
    }

    public Optional<StringFilter> optionalAccessToken2() {
        return Optional.ofNullable(accessToken2);
    }

    public StringFilter accessToken2() {
        if (accessToken2 == null) {
            setAccessToken2(new StringFilter());
        }
        return accessToken2;
    }

    public void setAccessToken2(StringFilter accessToken2) {
        this.accessToken2 = accessToken2;
    }

    public StringFilter getJhiFrom() {
        return jhiFrom;
    }

    public Optional<StringFilter> optionalJhiFrom() {
        return Optional.ofNullable(jhiFrom);
    }

    public StringFilter jhiFrom() {
        if (jhiFrom == null) {
            setJhiFrom(new StringFilter());
        }
        return jhiFrom;
    }

    public void setJhiFrom(StringFilter jhiFrom) {
        this.jhiFrom = jhiFrom;
    }

    public StringFilter getJhiTo() {
        return jhiTo;
    }

    public Optional<StringFilter> optionalJhiTo() {
        return Optional.ofNullable(jhiTo);
    }

    public StringFilter jhiTo() {
        if (jhiTo == null) {
            setJhiTo(new StringFilter());
        }
        return jhiTo;
    }

    public void setJhiTo(StringFilter jhiTo) {
        this.jhiTo = jhiTo;
    }

    public StringFilter getCost() {
        return cost;
    }

    public Optional<StringFilter> optionalCost() {
        return Optional.ofNullable(cost);
    }

    public StringFilter cost() {
        if (cost == null) {
            setCost(new StringFilter());
        }
        return cost;
    }

    public void setCost(StringFilter cost) {
        this.cost = cost;
    }

    public StringFilter getCharged() {
        return charged;
    }

    public Optional<StringFilter> optionalCharged() {
        return Optional.ofNullable(charged);
    }

    public StringFilter charged() {
        if (charged == null) {
            setCharged(new StringFilter());
        }
        return charged;
    }

    public void setCharged(StringFilter charged) {
        this.charged = charged;
    }

    public StringFilter getSynced() {
        return synced;
    }

    public Optional<StringFilter> optionalSynced() {
        return Optional.ofNullable(synced);
    }

    public StringFilter synced() {
        if (synced == null) {
            setSynced(new StringFilter());
        }
        return synced;
    }

    public void setSynced(StringFilter synced) {
        this.synced = synced;
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
        final ApiTrackCriteria that = (ApiTrackCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(uuid, that.uuid) &&
            Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(source, that.source) &&
            Objects.equals(serviceId, that.serviceId) &&
            Objects.equals(uri, that.uri) &&
            Objects.equals(url, that.url) &&
            Objects.equals(statusCode, that.statusCode) &&
            Objects.equals(rtt, that.rtt) &&
            Objects.equals(requestMethod, that.requestMethod) &&
            Objects.equals(accessToken1, that.accessToken1) &&
            Objects.equals(accessToken2, that.accessToken2) &&
            Objects.equals(jhiFrom, that.jhiFrom) &&
            Objects.equals(jhiTo, that.jhiTo) &&
            Objects.equals(cost, that.cost) &&
            Objects.equals(charged, that.charged) &&
            Objects.equals(synced, that.synced) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            uuid,
            timestamp,
            source,
            serviceId,
            uri,
            url,
            statusCode,
            rtt,
            requestMethod,
            accessToken1,
            accessToken2,
            jhiFrom,
            jhiTo,
            cost,
            charged,
            synced,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApiTrackCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalUuid().map(f -> "uuid=" + f + ", ").orElse("") +
            optionalTimestamp().map(f -> "timestamp=" + f + ", ").orElse("") +
            optionalSource().map(f -> "source=" + f + ", ").orElse("") +
            optionalServiceId().map(f -> "serviceId=" + f + ", ").orElse("") +
            optionalUri().map(f -> "uri=" + f + ", ").orElse("") +
            optionalUrl().map(f -> "url=" + f + ", ").orElse("") +
            optionalStatusCode().map(f -> "statusCode=" + f + ", ").orElse("") +
            optionalRtt().map(f -> "rtt=" + f + ", ").orElse("") +
            optionalRequestMethod().map(f -> "requestMethod=" + f + ", ").orElse("") +
            optionalAccessToken1().map(f -> "accessToken1=" + f + ", ").orElse("") +
            optionalAccessToken2().map(f -> "accessToken2=" + f + ", ").orElse("") +
            optionalJhiFrom().map(f -> "jhiFrom=" + f + ", ").orElse("") +
            optionalJhiTo().map(f -> "jhiTo=" + f + ", ").orElse("") +
            optionalCost().map(f -> "cost=" + f + ", ").orElse("") +
            optionalCharged().map(f -> "charged=" + f + ", ").orElse("") +
            optionalSynced().map(f -> "synced=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
