package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.RegularExpression;
import gov.moda.dw.manager.repository.RegularExpressionRepository;
import gov.moda.dw.manager.repository.custom.CustomRegularExpressionRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.RegularExpressionService;
import gov.moda.dw.manager.service.dto.RegularExpressionDTO;
import gov.moda.dw.manager.service.mapper.RegularExpressionMapper;
import gov.moda.dw.manager.util.RegexUtils;
import gov.moda.dw.manager.web.rest.errors.BadRequestAlertException;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomRegularExpressionService extends RegularExpressionService {

    private final Logger log = LoggerFactory.getLogger(RegularExpressionService.class);

    private final RegularExpressionMapper regularExpressionMapper;

    private final CustomRegularExpressionRepository customRegularExpressionRepository;

    public CustomRegularExpressionService(
        RegularExpressionRepository regularExpressionRepository,
        RegularExpressionMapper regularExpressionMapper,
        CustomRegularExpressionRepository customRegularExpressionRepository
    ) {
        super(regularExpressionRepository, regularExpressionMapper);
        this.regularExpressionMapper = regularExpressionMapper;
        this.customRegularExpressionRepository = customRegularExpressionRepository;
    }

    public RegularExpressionDTO save(RegularExpressionDTO regularExpressionDTO) {
        log.debug("Request to save RegularExpression : {}", regularExpressionDTO);

        regularExpressionDTO.setType(SecurityUtils.getJwtUserObject().get(0).getOrgId());

        // 編譯正則表達式（具超時保護）
        Pattern pattern = RegexUtils.matchWithTimeout(regularExpressionDTO.getRegularExpression());
        if (null == pattern) {
            throw new BadRequestAlertException("Regex error", "CustomRegularExpressionService", "Regex_Error");
        }

        RegularExpression regularExpression = regularExpressionMapper.toEntity(regularExpressionDTO);

        regularExpression = customRegularExpressionRepository.save(regularExpression);

        return regularExpressionMapper.toDto(regularExpression);
    }
}
