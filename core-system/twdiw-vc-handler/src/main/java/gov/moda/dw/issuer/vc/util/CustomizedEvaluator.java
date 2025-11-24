package gov.moda.dw.issuer.vc.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

public class CustomizedEvaluator extends EventEvaluatorBase{

	@Override
    public boolean evaluate(Object o) throws NullPointerException, EvaluationException {
        ILoggingEvent event = (ILoggingEvent)o;
        String message = event.getMessage();
		// 這就是目標字串
        return message.contains("could not obtain lock");
    }
}
