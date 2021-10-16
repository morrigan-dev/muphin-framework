package de.morrigan.dev.muphin.core.workflow;

import java.util.ArrayList;
import java.util.List;

import de.morrigan.dev.muphin.core.InstanceManager;
import de.morrigan.dev.muphin.core.phase.AbstractPhase;

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

  private static final InstanceManager INSTANCES = InstanceManager.getInstance();

  private final String name;
  private final List<Class<? extends AbstractPhase>> phaseClasses;

  /**
   * Create a new workflow.
   *
   * @param name a unique name of this workflow
   * @param phaseClasses a collection of phases that belongs to this workflow
   * @since 0.0.1
   */
  protected AbstractWorkflow(String name, List<Class<? extends AbstractPhase>> phaseClasses) {
    super();

    this.name = name;
    this.phaseClasses = new ArrayList<>(phaseClasses);
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
  public List<AbstractPhase> getPhases() {
    List<AbstractPhase> phaseInstances = new ArrayList<>();
    for (Class<? extends AbstractPhase> phaseClass : this.phaseClasses) {
      phaseInstances.add(INSTANCES.getPhase(phaseClass));
    }
    return phaseInstances;
  }
}
