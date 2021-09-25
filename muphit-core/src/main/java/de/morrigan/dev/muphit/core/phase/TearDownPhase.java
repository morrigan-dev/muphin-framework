package de.morrigan.dev.muphit.core.phase;

/**
 * The tear down phase is always the last phase of a workflow and is therefore always executed last.
 * <p>
 * This phase is inserted internally by the framework at the end of a workflow and therefore does not have to be
 * inserted manually by the developer. Nevertheless, test methods can register in the tear down phase and are executed
 * in this phase before the internal tear down mechanisms.
 *
 * @author morrigan
 * @since 0.0.1
 */
public class TearDownPhase extends AbstractPhase<String> {

  public static final String NAME = "Tear down phase";

  public TearDownPhase() {
    super(NAME, "");
  }
}
