package gov.moda.dw.issuer.vc.vo;

/**
 * enumeration of credential_data status
 *
 * @version 20241016
 */
public enum DataStatus {
	
	VALID ("VALID"),
	EXPIRED ("EXPIRED"),
	USED ("USED"),
	UNKNOWN ("UNKNOWN");
	
	
	private final String value;
	
	DataStatus(String value) {
        this.value = value;
    }
	
	public static DataStatus getByValue(String value) {
		DataStatus[] values = DataStatus.values();
		for(DataStatus dataStatus : values) {
			if(dataStatus.getValue().equalsIgnoreCase(value))
				return dataStatus;
		}
		return UNKNOWN;
	}
	
	public boolean equals(DataStatus dataStatus) {
		
		if(dataStatus != null)
			return value.equalsIgnoreCase(dataStatus.getValue());
		return false;
	}

	public String getValue() {
		return value;
	}
}
