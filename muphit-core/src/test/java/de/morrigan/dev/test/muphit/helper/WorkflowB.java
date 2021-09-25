package de.morrigan.dev.test.muphit.helper;

import java.util.Arrays;

import de.morrigan.dev.muphit.core.phase.AbstractPhase;
import de.morrigan.dev.muphit.core.workflow.AbstractWorkflow;

public class WorkflowB extends AbstractWorkflow {

  private static final AbstractPhase<?>[] phases = new AbstractPhase<?>[] {
      new TestPhaseA(),
      new TestPhaseC(),
  };

  public WorkflowB() {
    super("Workflow B", Arrays.asList(phases));
  }
}
