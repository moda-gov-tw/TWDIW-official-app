package gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry;

@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonInclude(value = Include.NON_NULL)
public class DescriptorMap {

    private String id;

    private FormatRegistry format;

    private String path;

    @JsonProperty("path_nested")
    private DescriptorMap pathNested;


    private DescriptorMap() {
    }

    public DescriptorMap(String id, FormatRegistry format, String path) {
        this.id = id;
        this.format = format;
        this.path = path;
    }

    public void setPathNested(DescriptorMap pathNested) {
        this.pathNested = pathNested;
    }

    public String getId() {
        return id;
    }

    public FormatRegistry getFormat() {
        return format;
    }

    public String getPath() {
        return path;
    }

    public DescriptorMap getPathNested() {
        return pathNested;
    }
}
