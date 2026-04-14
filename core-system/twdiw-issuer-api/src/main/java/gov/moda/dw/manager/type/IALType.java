package gov.moda.dw.manager.type;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum IALType {
	IAL_1("1", "IAL 1"), 
	IAL_2("2", "IAL 2"), 
	IAL_3("3", "IAL 3");

	@Getter
	private String code;

	@Getter
	private String name;

	public static IALType toIALType(String code) {
		for (IALType tmp : IALType.values()) {
			if (tmp.getCode().equals(code)) {
				return tmp;
			}
		}
		return null;
	}

	public static String getNameByCode(String code) {
		if (StringUtils.isNotEmpty(code) || StringUtils.isNotBlank(code)) {
			for (IALType tmp : IALType.values()) {
				if (tmp.getCode().equals(code)) {
					return tmp.getName();
				}
			}
		}
		return "";
	}
}
