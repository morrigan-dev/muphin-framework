package de.morrigan.dev.test.muphit.helper;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphit.core.annotation.Phase;
import de.morrigan.dev.muphit.core.annotation.WorkflowTest;
import de.morrigan.dev.muphit.core.phase.SetupPhase;
import de.morrigan.dev.muphit.core.phase.TearDownPhase;

@WorkflowTest(WorkflowA.class)
public class TestClassA1 {

  /** Logger für Debug/Fehlerausgaben */
  private static final Logger LOG = LoggerFactory.getLogger(TestClassA1.class);

  @Test
  @Phase(beforePhase = SetupPhase.class)
  public void testWorkflowABeforeSetupPhase() {
    LOG.info("Workflow A before phase Setup");
  }

  @Test
  @Phase(afterPhase = SetupPhase.class)
  public void testWorkflowAAfterSetupPhase() {
    LOG.info("Workflow A after phase Setup");
  }

  @Test
  @Phase(beforePhase = TearDownPhase.class)
  public void testWorkflowABeforeTearDownPhase() {
    LOG.info("Workflow A before phase TearDown");
  }

  @Test
  @Phase(afterPhase = TearDownPhase.class)
  public void testWorkflowAAfterTearDownPhase() {
    LOG.info("Workflow A after phase TearDown");
  }

  @Test
  @Phase(beforePhase = TestPhaseA.class)
  public void testWorkflowABeforeTestPhaseA() {
    LOG.info("Workflow A before phase TestPhaseA");
  }

  @Test
  @Phase(afterPhase = TestPhaseA.class)
  public void testWorkflowAAfterTestPhaseA() {
    LOG.info("Workflow A after phase TestPhaseA");
  }

  @Test
  @Phase(beforePhase = TestPhaseB.class)
  public void testWorkflowABeforeTestPhaseB() {
    LOG.info("Workflow A before phase TestPhaseB");
  }

  @Test
  @Phase(afterPhase = TestPhaseB.class)
  public void testWorkflowAAfterTestPhaseB() {
    LOG.info("Workflow A after phase TestPhaseB");
  }
}