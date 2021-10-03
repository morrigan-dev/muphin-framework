package de.morrigan.dev.muphit.core.phase;

import de.morrigan.dev.muphit.core.annotation.Phase;

/**
 * Serves only as dummy phase for the {@link Phase} annotation to make the parameters for the phases optional.
 *
 * @author morrigan
 * @since 0.0.1
 */
public class NoPhase extends AbstractPhase<String> {

  public static final String NAME = "No phase";

  public NoPhase() {
    super(NAME, "");
  }

  @Override
  protected boolean execute(String data) {
    return true;
  }
}
