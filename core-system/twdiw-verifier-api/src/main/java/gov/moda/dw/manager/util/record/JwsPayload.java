package gov.moda.dw.manager.util.record;

/**
 * @author AlexChang
 * @create 2024/07/26
 * @description
 */
public record JwsPayload<T>(T document, String ts) {}
