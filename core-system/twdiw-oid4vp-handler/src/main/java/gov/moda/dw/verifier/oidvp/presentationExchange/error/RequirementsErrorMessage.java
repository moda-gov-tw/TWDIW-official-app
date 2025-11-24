package gov.moda.dw.verifier.oidvp.presentationExchange.error;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class RequirementsErrorMessage extends PresentationError {

    //    private final List<Integer> trace;
    private final Deque<Integer> traceDeque;
    private final int currentIndex;
    private final List<CountErrorInfo> countErrorInfo;

//    public RequirementsErrorMessage(int currentIndex, List<Integer> trace, List<CountErrorInfo> countErrorInfo) {
//        this.currentIndex = currentIndex;
//        this.trace = trace;
//        this.countErrorInfo = countErrorInfo;
//    }

    public RequirementsErrorMessage(Deque<Integer> traceDeque, int currentIndex, List<CountErrorInfo> countErrorInfo) {
        this.traceDeque = traceDeque;
        this.currentIndex = currentIndex;
        this.countErrorInfo = countErrorInfo;
    }

//    private String getErrorTraceDescription(List<Integer> trace, int currentIndex) {
//        if (trace.isEmpty()) {
//            return "submission_requirement[" + currentIndex + "]";
//        } else {
//            StringBuilder sb = new StringBuilder("submission_requirement[" + trace.get(0) + "]");
//            for (int i = 1; i < trace.size(); i++) {
//                sb.append(".from_nested[").append(trace.get(i)).append("]");
//            }
//            sb.append(".from_nested[").append(currentIndex).append("]");
//            return sb.toString();
//        }
//    }

    private String getErrorTraceDescription(Deque<Integer> traceDeque, int currentIndex) {
        if (traceDeque.isEmpty()) {
            return "submission_requirement[" + currentIndex + "]";
        } else {
            StringBuilder sb = new StringBuilder("submission_requirement[" + traceDeque.pollFirst() + "]");
            while (!traceDeque.isEmpty()) {
                sb.append(".from_nested[").append(traceDeque.pollFirst()).append("]");
            }
            sb.append(".from_nested[").append(currentIndex).append("]");
            return sb.toString();
        }
    }

    private String getErrorDescription(Deque<Integer> traceDeque, int currentIndex, List<CountErrorInfo> errors) {
        String base = getErrorTraceDescription(traceDeque, currentIndex);
        StringBuilder sb = new StringBuilder(base);
        sb.append(": mismatch:[");
        Iterator<CountErrorInfo> iterator = errors.iterator();
        while (iterator.hasNext()) {
            CountErrorInfo error = iterator.next();
            sb.append("(");
            sb.append("from=").append(Optional.ofNullable(error.group()).orElse("from_nested")).append(",");
            sb.append("type=").append(error.type()).append(",");
            sb.append("expected=").append(error.expected()).append(",");
            sb.append("satisfied=").append(error.satisfied());
            sb.append(")");
            if (iterator.hasNext()) {
                sb.append(",");
            }
        }
//        for (CountErrorInfo error : errors) {
//            sb.append("(");
//            sb.append("from=").append(Optional.ofNullable(error.group()).orElse("from_nested")).append(",");
//            sb.append("type=").append(error.type()).append(",");
//            sb.append("expected=").append(error.expected()).append(",");
//            sb.append("satisfied=").append(error.satisfied());
//            sb.append(")");
//        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String getEvaluationErrorMessage() {
        return getErrorDescription(traceDeque, currentIndex, countErrorInfo);
    }

    @Override
    public String toString() {
        return getEvaluationErrorMessage();
    }
}
