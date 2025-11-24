package gov.moda.dw.verifier.oidvp.presentationExchange.schema;

import com.jayway.jsonpath.JsonPath;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.DescriptorMap;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationSubmission;
import java.util.List;

public class PresentationSubmissionValidator extends AbstractValidator<PresentationSubmission> {

    private static final int VALID = 0;
    private static final int ID_OF_EACH_LEVEL_IS_NOT_THE_SAME = 1;
    private static final int NESTED_LEVEL_TOO_BIG = 2;
    private static final int INVALID_JSONPATH = 3;

    @Override
    public ValidateResult validate(PresentationSubmission ps) {
        if (ps == null) {
            return new ValidateResult(false, "PresentationSubmission must not be null");
        }
        if (ps.getId() == null || ps.getId().isEmpty()) {
            return new ValidateResult(false, "presentation_submission id should not be empty");
        }
        if (ps.getDefinitionId() == null || ps.getDefinitionId().isEmpty()) {
            return new ValidateResult(false, "presentation_submission 'definition_id' should not be empty");
        }
        if (ps.getDescriptorMap().isEmpty()){
            return new ValidateResult(false,"descriptor_map should not be empty");
        }

        Result result = isSameDescriptorMapId(ps.getDescriptorMap());
        if (!result.isValid()) {
            String message = getMessage(result);
            return new ValidateResult(false, message);
        }

        return new ValidateResult(true, "presentation_submission is valid");
    }


    private Result isSameDescriptorMapId(List<DescriptorMap> descriptorMaps) {
        for (DescriptorMap descriptorMap : descriptorMaps) {
            Result result = isSameDescriptorMapId(descriptorMap, 0);
            if (!result.isValid()) {
                return result;
            }

            if (!isValidJsonPath(descriptorMap.getPath())) {
                return new Result(false, INVALID_JSONPATH);
            }
        }
        return new Result(true, VALID);
    }

    private Result isSameDescriptorMapId(DescriptorMap dm, int level) {
        DescriptorMap pathNested = dm.getPathNested();
        int currentLevel = level;

        // oidvp spec only supports one level of nesting path_nested object now
        if (level > 1) {
            return new Result(false, NESTED_LEVEL_TOO_BIG);
        }

        if (pathNested == null) {
            return new Result(true, NESTED_LEVEL_TOO_BIG);
        } else {
            if (dm.getId().equals(pathNested.getId())) {
                currentLevel++;
                return isSameDescriptorMapId(pathNested, currentLevel);
            } else {
                return new Result(false, ID_OF_EACH_LEVEL_IS_NOT_THE_SAME);
            }
        }
    }

    private boolean isValidJsonPath(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }

        try {
            // is jsonpath point to single item
            return JsonPath.isPathDefinite(path);
        } catch (Exception e) {
            return false;
        }
    }

    private static String getMessage(Result result) {
        String message = "invalid presentation_submission";
        switch (result.errorCode()) {
            case ID_OF_EACH_LEVEL_IS_NOT_THE_SAME ->
                message = "invalid presentation_submission: 'descriptor_map id' is not the same for each level of nesting";

            case NESTED_LEVEL_TOO_BIG ->
                message = "invalid presentation_submission: nesting path_nested level bigger than 1";

            case INVALID_JSONPATH -> message = "invalid presentation_submission: 'path' is not the valid jsonpath";
        }
        return message;
    }

    private record Result(boolean isValid, int errorCode) {}
}
