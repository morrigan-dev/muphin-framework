package de.morrigan.dev.test.muphit.helper;

import java.util.Arrays;

import de.morrigan.dev.muphit.core.workflow.AbstractWorkflow;

public class WorkflowB extends AbstractWorkflow {

  public WorkflowB() {
    super("Workflow B", Arrays.asList(TestPhaseA.class, TestPhaseC.class));
  }
}
