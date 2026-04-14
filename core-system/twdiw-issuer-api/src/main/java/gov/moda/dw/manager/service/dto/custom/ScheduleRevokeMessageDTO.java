package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRevokeMessageDTO {

    private Boolean success;
    private String message;
    private Instant executeTimestamp;
}
