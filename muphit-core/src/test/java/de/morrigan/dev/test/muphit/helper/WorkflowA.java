package de.morrigan.dev.test.muphit.helper;

import java.util.Arrays;

import de.morrigan.dev.muphit.core.phase.AbstractPhase;
import de.morrigan.dev.muphit.core.workflow.AbstractWorkflow;

public class WorkflowA extends AbstractWorkflow {

  private static final AbstractPhase<?>[] phases = new AbstractPhase<?>[] {
      new TestPhaseA(),
      new TestPhaseB(),
  };

  public WorkflowA() {
    super("Workflow A", Arrays.asList(phases));
  }
}
