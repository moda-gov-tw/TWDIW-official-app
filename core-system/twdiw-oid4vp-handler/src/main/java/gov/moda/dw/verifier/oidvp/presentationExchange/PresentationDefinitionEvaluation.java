package gov.moda.dw.verifier.oidvp.presentationExchange;

import static gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry.JWT_VC;
import static gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry.JWT_VC_JSON;
import static gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry.JWT_VP;
import static gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry.JWT_VP_JSON;
import static gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry.LDP_VC;
import static gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry.LDP_VP;

import com.fasterxml.jackson.databind.JsonNode;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.model.errors.PresentationEvaluationException;
import gov.moda.dw.verifier.oidvp.model.errors.VcValidatedException;
import gov.moda.dw.verifier.oidvp.model.vc.DecodedVerifiableCredential;
import gov.moda.dw.verifier.oidvp.presentationExchange.error.CountErrorInfo;
import gov.moda.dw.verifier.oidvp.presentationExchange.error.CountErrorInfo.CountType;
import gov.moda.dw.verifier.oidvp.presentationExchange.error.RequirementsErrorMessage;
import gov.moda.dw.verifier.oidvp.presentationExchange.handler.PresentationDefinitionHandlerClient;
import gov.moda.dw.verifier.oidvp.presentationExchange.handler.PresentationDefinitionHandlerClient.PresentationDefinitionHandlerResult;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.InputDescriptor;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.DescriptorMap;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationSubmission;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.SubmissionRequirement;
import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.SubmissionRequirement.SubmissionRequirementRule;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class PresentationDefinitionEvaluation {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresentationDefinitionEvaluation.class);

    /**
     * To evaluate vp(decoded jwt_vp/jwt_vp_json、ldp_vp)、vc(decoded jwt_vc/jwt_vc_json、ldp_vc) against the
     * presentation_definition.
     *
     * <p>
     * NOTE: Some credential format and Relational Constraint Feature in presentation_definition are currently not
     * supported.
     *
     * @param pd  presentation_definition
     * @param ps  presentation_submission
     * @param vcs the map of CredentialPath and vc to be validated
     * @return if vc is satisfied the presentation_definition will return PresentationDefinitionHandlerResult, otherwise
     * will throw the exception.
     * @throws PresentationEvaluationException when presentation_definition or presentation_submission is in the wrong
     *                                         format.
     * @throws VcValidatedException            if vc didn't satisfy the presentation_definition.
     */
    public PresentationDefinitionHandlerResult evaluatePresentations(PresentationDefinition pd, PresentationSubmission ps, Map<CredentialPath, CredentialInfo> vcs)
        throws PresentationEvaluationException, VcValidatedException
    {
        if (pd == null) {
            throw new IllegalArgumentException("PresentationDefinition must not be null");
        }
        if (ps == null) {
            throw new IllegalArgumentException("PresentationSubmission must not be null");
        }
        if (vcs == null || vcs.isEmpty()) {
            throw new IllegalArgumentException("vcs must not be empty");
        }

        return evaluateAndCheckPresentations(pd, ps, vcs);
    }

    private PresentationDefinitionHandlerResult evaluateAndCheckPresentations(PresentationDefinition pd, PresentationSubmission ps, Map<CredentialPath, CredentialInfo> vcs)
        throws VcValidatedException, PresentationEvaluationException
    {
        if (!isDefinitionIdMatchedPresentationDefinition(pd, ps)) {
            throw new VcValidatedException(OidvpError.PS_NOT_MATCH_PD, "invalid presentation_submission's definition_id: not match with presentation_definition's id");
        }

        ArrayList<PresentationSubmissionDTO> psDTOs = parsePresentationSubmission(ps);
        if (!isVcNumberMatchPresentationSubmission(psDTOs, vcs)) {
            throw new VcValidatedException(OidvpError.CREDENTIAL_NUMBER_NOT_MATCH, "number of vp/vc doesn't match the vp/vc described in presentation_submission");
        }
        List<SubmissionInfo> submissionInfos = toSubmissionInfo(pd, psDTOs, vcs);
        return evaluatePresentationsAgainstSubmission(pd, submissionInfos);
    }

    private PresentationDefinitionHandlerResult evaluatePresentationsAgainstSubmission(PresentationDefinition pd, List<SubmissionInfo> submissionInfos)
        throws VcValidatedException, PresentationEvaluationException
    {
        if (isUseSubmissionRequirement(pd)) {
            validateIsSubmissionSatisfySubmissionRequirements(pd, submissionInfos);
            LOGGER.info("[check is submitted vc satisfied submission_requirements]: PASS");
        }
        PresentationDefinitionHandlerClient handlerClient = new PresentationDefinitionHandlerClient();
        PresentationDefinitionHandlerResult evaluationResult = handlerClient.evaluate(pd, submissionInfos);
        LOGGER.info("[check is vc satisfied presentation_definition]: PASS");
        return evaluationResult;
    }

    public boolean isDefinitionIdMatchedPresentationDefinition(PresentationDefinition pd, PresentationSubmission ps) {
        return pd.getId().equals(ps.getDefinitionId());
    }

    private InputDescriptor findInputDescriptorById(PresentationDefinition pd, String id) {
        for (InputDescriptor inputDescriptor : pd.getInputDescriptors()) {
            if (inputDescriptor.getId().equals(id)) {
                return inputDescriptor;
            }
        }
        return null;
    }

    private boolean isUseSubmissionRequirement(PresentationDefinition pd) {
        return pd.getSubmissionRequirements() != null && !pd.getSubmissionRequirements().isEmpty();
    }

    private List<InputDescriptor> validateIsSubmissionSatisfySubmissionRequirements(PresentationDefinition pd, List<SubmissionInfo> submissionInfos)
        throws VcValidatedException, PresentationEvaluationException
    {
        Map<String, Integer> groupCount = parseGroupCountMap(pd);
        try {
            EvaluateRequirementsResult result = evaluateSubmissionRequirements(pd.getSubmissionRequirements(), groupCount, submissionInfos);
            if (result.errorInfoList().isEmpty()) {
                return result.selected();
            } else {
                result.errorInfoList().forEach(error -> LOGGER.warn(error.getEvaluationErrorMessage()));
                throw new VcValidatedException(OidvpError.VC_NOT_MATCH_PRESENTATION_DEFINITION, "received vc not satisfy submission_requirements");
            }
        } catch (PresentationEvaluationException e) {
            LOGGER.error("invalid presentation_definition: {}", e.getMessage());
            throw e;
        }
    }

    private EvaluateRequirementsResult evaluateSubmissionRequirements(
        List<SubmissionRequirement> submissionRequirements, Map<String, Integer> groupCount, List<SubmissionInfo> submissionInfos)
        throws PresentationEvaluationException
    {
        ArrayDeque<Integer> nestedIndexStack = new ArrayDeque<>();
        return evaluateSubmissionRequirements(submissionRequirements, groupCount, submissionInfos, nestedIndexStack);
    }

    private EvaluateRequirementsResult evaluateSubmissionRequirements(
        List<SubmissionRequirement> submissionRequirements, Map<String, Integer> groupCount, List<SubmissionInfo> submissionInfos, Deque<Integer> nestedIndexStack)
        throws PresentationEvaluationException
    {
        int matched = 0;
        int currentLevel = nestedIndexStack.size();
        ArrayList<InputDescriptor> selected = new ArrayList<>();
        List<RequirementsErrorMessage> errorInfoList = new ArrayList<>();

        for (int i = 0; i < submissionRequirements.size(); i++) {
            SubmissionRequirement sr = submissionRequirements.get(i);
            if (sr.getFrom() != null && !sr.getFrom().isBlank()) {
                List<InputDescriptor> inputDescriptors = getMatchedInputDescriptor(sr, submissionInfos);
                List<CountErrorInfo> errors = checkCount(sr, inputDescriptors.size(), groupCount);
                if (errors.isEmpty()) {
                    matched++;
                    selected.addAll(inputDescriptors);
                } else {
                    errorInfoList.add(new RequirementsErrorMessage(new ArrayDeque<>(nestedIndexStack), i, errors));
                }
            } else if (sr.getFromNested() != null && !sr.getFromNested().isEmpty()) {
                nestedIndexStack.addLast(i);
                EvaluateRequirementsResult recursiveResult = evaluateSubmissionRequirements(sr.getFromNested(), groupCount, submissionInfos, nestedIndexStack);
                nestedIndexStack.removeLast();
                List<CountErrorInfo> errors = checkCount(sr, recursiveResult.matched(), null);
                if (errors.isEmpty()) {
                    matched += recursiveResult.matched();
                    selected.addAll(recursiveResult.selected());
                } else {
                    errorInfoList.add(new RequirementsErrorMessage(new ArrayDeque<>(nestedIndexStack), i, errors));
                    errorInfoList.addAll(recursiveResult.errorInfoList());
                }
            } else {
                throw new PresentationEvaluationException(OidvpError.INVALID_PRESENTATION_DEFINITION, "invalid submission_requirement: must contain either 'from' or 'from_nested'");
            }

            // reset nestedIndexStack for next submission_requirement
            if (currentLevel == 0) {
                nestedIndexStack.clear();
            }
        }
        return new EvaluateRequirementsResult(matched, selected, errorInfoList);
    }

    private List<InputDescriptor> getMatchedInputDescriptor(SubmissionRequirement sr, List<SubmissionInfo> submissionInfos)
        throws PresentationEvaluationException
    {
        List<InputDescriptor> results = new ArrayList<>();
        String from = sr.getFrom();
        for (SubmissionInfo submissionInfo : submissionInfos) {
            InputDescriptor inputDescriptor = submissionInfo.inputDescriptor();
            if (inputDescriptor.getGroup() == null) {
                throw new PresentationEvaluationException(OidvpError.INVALID_PRESENTATION_DEFINITION, "invalid input_descriptor: submission_requirements is present, input_descriptor must contain 'group'");
            }
            if (inputDescriptor.getGroup().contains(from)) {
                results.add(inputDescriptor);
            }
        }
        return results;
    }

    private Map<String, Integer> parseGroupCountMap(PresentationDefinition pd) {
        HashMap<String, Integer> groupCount = new HashMap<>();
        for (InputDescriptor inputDescriptor : pd.getInputDescriptors()) {
            if (inputDescriptor != null && inputDescriptor.getGroup() != null) {
                for (String group : inputDescriptor.getGroup()) {
                    if (groupCount.containsKey(group)) {
                        groupCount.put(group, groupCount.get(group) + 1);
                    } else {
                        groupCount.put(group, 1);
                    }
                }
            }
        }
        return groupCount;
    }

    private List<CountErrorInfo> checkCount(SubmissionRequirement sr, int count, Map<String, Integer> groupCount)
        throws PresentationEvaluationException
    {
        ArrayList<CountErrorInfo> errorInfos = new ArrayList<>();
        String from = sr.getFrom();
        if (SubmissionRequirementRule.PICK.equals(sr.getRule())) {
            Integer _count = sr.getCount();
            if (_count != null) {
                if (_count < 1) {
                    throw new PresentationEvaluationException(OidvpError.INVALID_PRESENTATION_DEFINITION, "invalid submission_requirement: the value of 'count' is invalid");
                } else if (_count != count) {
                    errorInfos.add(new CountErrorInfo(from, CountType.COUNT, _count, count));
                }
            }
            Integer min = sr.getMin();
            if (min != null) {
                if (min < 0) {
                    throw new PresentationEvaluationException(OidvpError.INVALID_PRESENTATION_DEFINITION, "invalid submission_requirement: the value of 'min' is invalid");
                } else if (min > count) {
                    errorInfos.add(new CountErrorInfo(from, CountType.MIN, min, count));
                }
            }
            Integer max = sr.getMax();
            if (max != null) {
                if (max < 1) {
                    throw new PresentationEvaluationException(OidvpError.INVALID_PRESENTATION_DEFINITION, "invalid submission_requirement: the value of 'max' is invalid");
                } else if (max < count) {
                    errorInfos.add(new CountErrorInfo(from, CountType.MAX, max, count));
                }
            }
        } else if (SubmissionRequirementRule.ALL.equals(sr.getRule())) {
            int _count = (from == null) ? sr.getFromNested().size() : groupCount.get(from);
            if (_count != count) {
                errorInfos.add(new CountErrorInfo(from, CountType.ALL, _count, count));
            }
        } else {
            throw new PresentationEvaluationException(OidvpError.INVALID_PRESENTATION_DEFINITION, "invalid submission_requirement: invalid 'rule' value");
        }
        return errorInfos;
    }

    private ArrayList<PresentationSubmissionDTO> parsePresentationSubmission(PresentationSubmission ps)
        throws PresentationEvaluationException
    {
        ArrayList<PresentationSubmissionDTO> list = new ArrayList<>();
        // currently for jwt vc/vp
        for (DescriptorMap descriptorMap : ps.getDescriptorMap()) {
            FormatRegistry vpFormat = descriptorMap.getFormat();
            FormatRegistry vcFormat = Optional.ofNullable(descriptorMap.getPathNested())
                                              .map(DescriptorMap::getFormat)
                                              .orElse(null);
            if (!JWT_VP.equals(vpFormat) && !JWT_VP_JSON.equals(vpFormat) && !LDP_VP.equals(vpFormat)) {
                throw new IllegalArgumentException("unsupported vp format");
            }
            if (!JWT_VC.equals(vcFormat) && !JWT_VC_JSON.equals(vcFormat) && !LDP_VC.equals(vcFormat)) {
                throw new IllegalArgumentException("unsupported vc format");
            }

            String vpPath = Optional.ofNullable(descriptorMap.getPath())
                                    .orElseThrow(() -> new PresentationEvaluationException(OidvpError.INVALID_PRESENTATION_SUBMISSION));
            String vcPath = Optional.ofNullable(descriptorMap.getPathNested())
                                    .map(DescriptorMap::getPath)
                                    .orElseThrow(() -> new PresentationEvaluationException(OidvpError.INVALID_PRESENTATION_SUBMISSION));
            String inputDescriptorId = descriptorMap.getId();
            FormatInfo formatInfo = new FormatInfo(vpFormat, vcFormat);
            CredentialPath credentialPath = new CredentialPath(vpPath, vcPath);
            DescriptorMapInfo descriptorMapInfo = new DescriptorMapInfo(formatInfo, inputDescriptorId);

            list.add(new PresentationSubmissionDTO(credentialPath, descriptorMapInfo));
        }
        return list;
    }

    private boolean isVcNumberMatchPresentationSubmission(List<PresentationSubmissionDTO> psDTOs, Map<CredentialPath, CredentialInfo> vcs) {
        List<CredentialPath> distinctVcList = psDTOs.stream()
                                                    .map(PresentationSubmissionDTO::credentialPath)
                                                    .distinct()
                                                    .toList();
        return distinctVcList.size() == vcs.size();
    }

    private InputDescriptor checkIsPresentationDefinitionContainInputDescriptor(PresentationDefinition pd, PresentationSubmissionDTO psDTO)
        throws PresentationEvaluationException
    {
        String inputDescriptorId = psDTO.descriptorMapInfo().inputDescriptorId();
        InputDescriptor inputDescriptor = findInputDescriptorById(pd, inputDescriptorId);
        if (inputDescriptor == null) {
            throw new PresentationEvaluationException(OidvpError.PS_NOT_MATCH_PD,
                "invalid presentation_submission, can not find input_descriptor in presentation_definition" +
                    "(descriptor_map id=" + inputDescriptorId + ")");
        } else {
            return inputDescriptor;
        }
    }

    private ArrayList<SubmissionInfo> toSubmissionInfo(PresentationDefinition pd, List<PresentationSubmissionDTO> psDTOs, Map<CredentialPath, CredentialInfo> vcs)
        throws PresentationEvaluationException, VcValidatedException
    {
        ArrayList<SubmissionInfo> submissionInfos = new ArrayList<>(vcs.size());
        for (PresentationSubmissionDTO psDTO : psDTOs) {
            CredentialPath path = psDTO.credentialPath();
            CredentialInfo credentialInfo = vcs.get(path);
            if (credentialInfo == null) {
                String message = "can not find vc by presentation_submission path=" + path;
                throw new VcValidatedException(OidvpError.VC_NOT_MATCH_PRESENTATION_SUBMISSION, message);
            }

            JsonNode vc = credentialInfo.rawVc();
            if (vc == null || !vc.isObject()) {
                throw new VcValidatedException(OidvpError.INVALID_VC, "vc is not json format");
            }
            InputDescriptor inputDescriptor = checkIsPresentationDefinitionContainInputDescriptor(pd, psDTO);
            FormatInfo formatInfo = psDTO.descriptorMapInfo().formatInfo();
            FormatRegistry vpFormat = formatInfo.vpFormat();
            FormatRegistry vcFormat = formatInfo.vcFormat();
            String vcPath = path.vcPath();
            submissionInfos.add(new SubmissionInfo(inputDescriptor, vpFormat, new DecodedVerifiableCredential(vcPath, vcFormat, vc, credentialInfo.limitDisclosure())));
        }
        return submissionInfos;
    }

    // record
    public record PresentationSubmissionDTO(CredentialPath credentialPath, DescriptorMapInfo descriptorMapInfo) {}

    public record FormatInfo(FormatRegistry vpFormat, FormatRegistry vcFormat) {}

    public record DescriptorMapInfo(FormatInfo formatInfo, String inputDescriptorId) {}

    public record CredentialPath(String vpPath, String vcPath) {

        @Override
        public String toString() {
            return "[" + "vp_path=" + vpPath + ",vc_path=" + vcPath + "]";
        }
    }

    public record SubmissionInfo(InputDescriptor inputDescriptor, FormatRegistry vpFormat, DecodedVerifiableCredential decodedVc) {

        public SubmissionInfo(InputDescriptor inputDescriptor, FormatRegistry vpFormat, DecodedVerifiableCredential decodedVc) {
            Assert.notNull(inputDescriptor, "inputDescriptor must not be null");
            this.inputDescriptor = inputDescriptor;
            Assert.notNull(vpFormat, "vpFormat must not be null");
            this.vpFormat = vpFormat;
            Assert.notNull(decodedVc, "decodedVc must not be null");
            this.decodedVc = decodedVc;
        }
    }

    public record CredentialInfo(JsonNode rawVc, boolean limitDisclosure) {}

    private record EvaluateRequirementsResult(int matched, List<InputDescriptor> selected, List<RequirementsErrorMessage> errorInfoList) {}
}
