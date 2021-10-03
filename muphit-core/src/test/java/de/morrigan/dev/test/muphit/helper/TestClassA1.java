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
@WorkflowTest(WorkflowA.class)
public class TestClassA1 {

  private static final Logger LOG = LoggerFactory.getLogger(TestClassA1.class);

  @Test
  @Phase(beforePhase = SetupPhase.class)
  public void testTC1WorkflowABeforeSetupPhase() {
    //    LOG.info("Workflow A before phase Setup");
  }

  @Test
  @Phase(afterPhase = SetupPhase.class)
  public void testTC1WorkflowAAfterSetupPhase() {
    //    LOG.info("Workflow A after phase Setup");
  }

  @Test
  @Phase(beforePhase = TearDownPhase.class)
  public void testTC1WorkflowABeforeTearDownPhase() {
    //    LOG.info("Workflow A before phase TearDown");
  }

  @Test
  @Phase(afterPhase = TearDownPhase.class)
  public void testTC1WorkflowAAfterTearDownPhase() {
    //    LOG.info("Workflow A after phase TearDown");
  }

  @Test
  @Phase(beforePhase = TestPhaseA.class)
  public void testTC1WorkflowABeforeTestPhaseA() {
    //    LOG.info("Workflow A before phase TestPhaseA");
  }

  @Test
  @Phase(afterPhase = TestPhaseA.class)
  public void testTC1WorkflowAAfterTestPhaseA() {
    //    LOG.info("Workflow A after phase TestPhaseA");
  }

  @Test
  @Phase(beforePhase = TestPhaseB.class)
  public void testTC1WorkflowABeforeTestPhaseB() {
    //    LOG.info("Workflow A before phase TestPhaseB");
  }

  @Test
  @Phase(afterPhase = TestPhaseB.class)
  public void testTC1WorkflowAAfterTestPhaseB() {
    //    LOG.info("Workflow A after phase TestPhaseB");
  }
}