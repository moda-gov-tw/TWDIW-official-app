package gov.moda.dw.manager.util.record;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author AlexChang
 * @create 2024/07/26
 * @description
 */
public record ProtectedHeader(@JsonProperty("alg") String alg, @JsonProperty("x5c") String[] x5c) {}
