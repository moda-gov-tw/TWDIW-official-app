package gov.moda.dw.issuer.vc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonSanitizer {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final String[] SENSITIVE_FIELDS = {"password", "creditCardNumber", "ssn"};

	/**
	 * 從 JSON 字串中移除所有敏感欄位。
	 *
	 * @param jsonString 原始 JSON 字串
	 * @return 移除敏感欄位後的 JSON 字串
	 */
	public static String sanitize(String jsonString) {
		try {
			JsonNode rootNode = objectMapper.readTree(jsonString);
			removeFields(rootNode);
			return objectMapper.writeValueAsString(rootNode);
		} catch (Exception e) {
			// 如果 JSON 格式不正確，直接返回原始字串，或拋出例外
			return jsonString;
		}
	}

	private static void removeFields(JsonNode node) {
		if (node.isObject()) {
			ObjectNode objectNode = (ObjectNode) node;
			for (String field : SENSITIVE_FIELDS) {
				objectNode.remove(field);
			}
			// 遞迴處理巢狀 JSON
			objectNode.elements().forEachRemaining(JsonSanitizer::removeFields);
		} else if (node.isArray()) {
			// 遞迴處理 JSON 陣列中的元素
			node.elements().forEachRemaining(JsonSanitizer::removeFields);
		}
	}
}
