package de.morrigan.dev.test.muphin.helper;

import java.util.Arrays;

import de.morrigan.dev.muphin.core.workflow.AbstractWorkflow;

public class WorkflowB extends AbstractWorkflow {

  public WorkflowB() {
    super("Workflow B", Arrays.asList(TestPhaseA.class, TestPhaseC.class));
  }
}
