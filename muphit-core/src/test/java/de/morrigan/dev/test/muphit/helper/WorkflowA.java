package de.morrigan.dev.test.muphit.helper;

import java.util.Arrays;

import de.morrigan.dev.muphit.core.workflow.AbstractWorkflow;

public class WorkflowA extends AbstractWorkflow {

  public WorkflowA() {
    super("Workflow A", Arrays.asList(TestPhaseA.class, TestPhaseB.class));
  }
}
