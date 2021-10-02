package de.morrigan.dev.test.muphit.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphit.core.annotation.Phase;
import de.morrigan.dev.muphit.core.annotation.WorkflowTest;
import de.morrigan.dev.muphit.core.phase.SetupPhase;
import de.morrigan.dev.muphit.core.phase.TearDownPhase;
import de.morrigan.dev.muphit.core.runner.WorkflowRunner;

@RunWith(WorkflowRunner.class)
@WorkflowTest(WorkflowB.class)
public class TestClassB {

  private static final Logger LOG = LoggerFactory.getLogger(TestClassB.class);

  @Test
  @Phase(beforePhase = SetupPhase.class)
  public void testWorkflowBBeforeSetupPhase() {
    LOG.info("Workflow B before phase Setup");
  }

  @Test
  @Phase(afterPhase = SetupPhase.class)
  public void testWorkflowBAfterSetupPhase1() {
    LOG.info("Workflow B after phase Setup 1");
  }

  @Test
  @Phase(afterPhase = SetupPhase.class)
  public void testWorkflowBAfterSetupPhase2() {
    LOG.info("Workflow B after phase Setup 2");
  }

  @Test
  @Phase(beforePhase = TearDownPhase.class)
  public void testWorkflowBBeforeTearDownPhase() {
    LOG.info("Workflow B before phase TearDown");
  }

  @Test
  @Phase(afterPhase = TearDownPhase.class)
  public void testWorkflowBAfterTearDownPhase() {
    LOG.info("Workflow B after phase TearDown");
  }

  @Test
  @Phase(beforePhase = TestPhaseA.class)
  public void testWorkflowBBeforeTestPhaseA() {
    LOG.info("Workflow B before phase TestPhaseA");
  }

  @Test
  @Phase(afterPhase = TestPhaseA.class)
  public void testWorkflowBAfterTestPhaseA() {
    LOG.info("Workflow B after phase TestPhaseA");
  }

  @Test
  @Phase(beforePhase = TestPhaseC.class)
  public void testWorkflowBBeforeTestPhaseC() {
    LOG.info("Workflow B before phase TestPhaseC");
  }

  @Test
  @Phase(afterPhase = TestPhaseC.class)
  public void testWorkflowBAfterTestPhaseC() {
    LOG.info("Workflow B after phase TestPhaseC");
  }
}
