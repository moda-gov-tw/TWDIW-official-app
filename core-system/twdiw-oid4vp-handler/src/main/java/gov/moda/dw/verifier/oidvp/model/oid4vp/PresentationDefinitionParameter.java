package gov.moda.dw.verifier.oidvp.model.oid4vp;

import gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition.PresentationDefinition;
import java.net.URI;

public class PresentationDefinitionParameter {

  private final PresentationDefinition presentationDefinition;

  private final URI presentationDefinitionUri;

  private PresentationDefinitionParameter(
      final PresentationDefinition presentationDefinition, final URI presentationDefinitionUri) {
    this.presentationDefinition = presentationDefinition;
    this.presentationDefinitionUri = presentationDefinitionUri;
  }

  public PresentationDefinitionParameter(final PresentationDefinition presentationDefinition) {
    this(presentationDefinition, null);
  }

  public PresentationDefinitionParameter(final URI presentationDefinitionUri) {
    this(null, presentationDefinitionUri);
  }

  public PresentationDefinition getPresentationDefinition() {
    return presentationDefinition;
  }

  public URI getPresentationDefinitionUri() {
    return presentationDefinitionUri;
  }
}
