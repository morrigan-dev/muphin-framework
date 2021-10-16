package de.morrigan.dev.muphin.core.phase;

import de.morrigan.dev.muphin.core.annotation.Phase;

/**
 * Serves only as dummy phase for the {@link Phase} annotation to make the parameters for the phases optional.
 *
 * @author morrigan
 * @since 0.0.1
 */
public class NoPhase extends AbstractPhase {

  public NoPhase() {
    super(AbstractPhase.INTERNAL_KIND, NoPhase.class.getSimpleName());
  }

  @Override
  public boolean execute() {
    return true;
  }
}
