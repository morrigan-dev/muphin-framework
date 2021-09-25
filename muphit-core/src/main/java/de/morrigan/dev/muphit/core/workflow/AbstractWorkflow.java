package de.morrigan.dev.muphit.core.workflow;

import java.util.ArrayList;
import java.util.List;

import de.morrigan.dev.muphit.core.phase.AbstractPhase;

/**
 * Defines an arbitrary process or workflow.
 * <p>
 * All user-defined workflow classes must inherit from this abstract workflow class. Each workflow must be given a
 * unique name and a collection of phases that belong to that workflow. The phases are specified in a sorted order.
 *
 * @author morrigan
 * @since 0.0.1
 */
public abstract class AbstractWorkflow {

  private final String name;
  private final List<AbstractPhase<?>> phases;

  /**
   * Create a new workflow.
   *
   * @param name a unique name of this workflow
   * @param phases a collection of phases that belongs to this workflow
   * @since 0.0.1
   */
  protected AbstractWorkflow(String name, List<AbstractPhase<?>> phases) {
    super();

    this.name = name;
    this.phases = new ArrayList<>(phases);
  }

  /**
   * @return a unique name of this workflow
   * @since 0.0.1
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return a collection of phases that belongs to this workflow
   * @since 0.0.1
   */
  public List<AbstractPhase<?>> getPhases() {
    return new ArrayList<>(this.phases);
  }
}
