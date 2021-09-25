package de.morrigan.dev.test.muphit.core.runner;

import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphit.core.runner.WorkflowRunner;

public class WorkflowRunnerTest {

  /** Logger für Debug/Fehlerausgaben */
  private static final Logger LOG = LoggerFactory.getLogger(WorkflowRunnerTest.class);

  @Test
  public void testConstructor() {
    WorkflowRunner workflowRunner = new WorkflowRunner("de.morrigan.dev.test.muphit.helper");
    LOG.info("{}", workflowRunner.getTestClassesByWorkflow());
    RunNotifier notifier = new RunNotifier();
    workflowRunner.run(notifier);
  }

}