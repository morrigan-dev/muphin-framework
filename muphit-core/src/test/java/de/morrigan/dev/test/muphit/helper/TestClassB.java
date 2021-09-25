package de.morrigan.dev.test.muphit.helper;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphit.core.annotation.Phase;
import de.morrigan.dev.muphit.core.annotation.WorkflowTest;
import de.morrigan.dev.muphit.core.phase.SetupPhase;
import de.morrigan.dev.muphit.core.phase.TearDownPhase;

@WorkflowTest(WorkflowB.class)
public class TestClassB {

  /** Logger für Debug/Fehlerausgaben */
  private static final Logger LOG = LoggerFactory.getLogger(TestClassB.class);

  @Test
  @Phase(beforePhase = SetupPhase.class)
  public void testWorkflowBBeforeSetupPhase() {
    LOG.info("Workflow B before phase Setup");
  }

  @Test
  @Phase(afterPhase = SetupPhase.class)
  public void testWorkflowBAfterSetupPhase() {
    LOG.info("Workflow B after phase Setup");
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
