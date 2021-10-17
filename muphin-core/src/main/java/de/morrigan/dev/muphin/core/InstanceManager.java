package de.morrigan.dev.muphin.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;

import de.morrigan.dev.muphin.core.phase.AbstractPhase;
import de.morrigan.dev.muphin.core.workflow.AbstractWorkflow;

/**
 * This manager holds all instances of workflows and phases so that they do not have to be created again and again, but
 * can be obtained from this manager via their class.
 *
 * @author morrigan
 * @since 0.0.1
 */
public class InstanceManager {

  private static final InstanceManager INSTANCE = new InstanceManager();

  /**
   * @return only instance of this manager
   * @since 0.0.1
   */
  public static final InstanceManager getInstance() {
    return INSTANCE;
  }

  private ConcurrentMap<Class<? extends AbstractWorkflow>, AbstractWorkflow> workflows;
  private ConcurrentMap<Class<? extends AbstractPhase>, AbstractPhase> phases;

  private InstanceManager() {
    super();
    this.workflows = new ConcurrentHashMap<>();
    this.phases = new ConcurrentHashMap<>();
  }

  /**
   * Creates a new list of workflows sorted by name.
   *
   * @return a list of workflows
   * @since 0.0.1
   */
  public List<AbstractWorkflow> getWorkflows() {
    List<AbstractWorkflow> listOfWorkflows = new ArrayList<>();
    this.workflows.forEach((workflowClass, workflow) -> listOfWorkflows.add(workflow));
    Collections.sort(listOfWorkflows, (w1, w2) -> w1.getName().compareTo(w2.getName()));
    return listOfWorkflows;
  }

  /**
   * Returns an instance of the workflow whose classes were passed. If the instance does not yet exist in the manager,
   * it will be created and added to it.
   *
   * @param workflowClass a class of which workflow is to be delivered
   * @return a single workflow
   * @throws IllegalArgumentException if the workflow instance cannot be created
   * @since 0.0.1
   */
  public AbstractWorkflow getWorkflow(Class<? extends AbstractWorkflow> workflowClass) {
    return this.workflows.compute(workflowClass, (key, value) -> {
      if (value == null) {
        try {
          value = workflowClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new IllegalArgumentException(StringUtils.join("Can't create a new instance from ", workflowClass,
              ". Please make sure a default constructor exists."), e);
        }
      }
      return value;
    });
  }

  /**
   * Returns an instance of the phase whose classes were passed. If the instance does not yet exist in the manager, it
   * will be created and added to it.
   *
   * @param phaseClass a class of which phase is to be delivered
   * @return a single phase
   * @throws IllegalArgumentException if the phase instance cannot be created
   * @since 0.0.1
   */
  public AbstractPhase getPhase(Class<? extends AbstractPhase> phaseClass) {
    return this.phases.compute(phaseClass, (phaseKey, phase) -> {
      if (phase == null) {
        try {
          phase = phaseClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new IllegalArgumentException(StringUtils.join("Can't create a new instance from ", phaseClass,
              ". Please make sure a default constructor exists."), e);
        }
      }
      return phase;
    });
  }

  /**
   * Clears all instances in this manager.
   *
   * @since 0.0.1
   */
  public void clear() {
    this.workflows.clear();
    this.phases.clear();
  }
}