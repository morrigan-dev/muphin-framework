package de.morrigan.dev.test.muphit.helper;

import java.util.List;

import de.morrigan.dev.muphit.core.phase.AbstractPhase;
import de.morrigan.dev.muphit.core.workflow.AbstractWorkflow;

public class WorkflowX extends AbstractWorkflow {

  public WorkflowX(List<Class<? extends AbstractPhase<?>>> phases) {
    super("Workflow X", phases);
  }
}
