package de.morrigan.dev.muphit.core.phase;

/**
 * The setup phase is always the first phase of a workflow and is therefore always executed first.
 * <p>
 * This phase is inserted internally by the framework at the beginning of a workflow and therefore does not have to be
 * inserted manually by the developer. Nevertheless, test methods can register in the setup phase and are executed
 * according to the internal setup mechanisms.
 *
 * @author morrigan
 * @since 0.0.1
 */
public class SetupPhase extends AbstractPhase<String> {

  public static final String NAME = "Setup phase";

  public SetupPhase() {
    super(NAME, "");
  }
}
