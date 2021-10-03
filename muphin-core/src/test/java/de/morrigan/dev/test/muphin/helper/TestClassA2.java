package de.morrigan.dev.test.muphin.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphin.core.annotation.Phase;
import de.morrigan.dev.muphin.core.annotation.WorkflowTest;
import de.morrigan.dev.muphin.core.phase.SetupPhase;
import de.morrigan.dev.muphin.core.phase.TearDownPhase;
import de.morrigan.dev.muphin.core.runner.WorkflowRunner;

@RunWith(WorkflowRunner.class)
@WorkflowTest(WorkflowA.class)
public class TestClassA2 {

  private static final Logger LOG = LoggerFactory.getLogger(TestClassA2.class);

  @Test
  @Phase(beforePhase = SetupPhase.class)
  public void testTC2WorkflowABeforeSetupPhase() {
    //    LOG.info("Workflow A before phase Setup");
  }

  @Test
  @Phase(afterPhase = SetupPhase.class)
  public void testTC2WorkflowAAfterSetupPhase() {
    //    LOG.info("Workflow A after phase Setup");
  }

  @Test
  @Phase(beforePhase = TearDownPhase.class)
  public void testTC2WorkflowABeforeTearDownPhase() {
    //    LOG.info("Workflow A before phase TearDown");
  }

  @Test
  @Phase(afterPhase = TearDownPhase.class)
  public void testTC2WorkflowAAfterTearDownPhase() {
    //    LOG.info("Workflow A after phase TearDown");
  }

  @Test
  @Phase(beforePhase = TestPhaseA.class)
  public void testTC2WorkflowABeforeTestPhaseA() {
    //    LOG.info("Workflow A before phase TestPhaseA");
  }

  @Test
  @Phase(afterPhase = TestPhaseA.class)
  public void testTC2WorkflowAAfterTestPhaseA() {
    //    LOG.info("Workflow A after phase TestPhaseA");
  }

  @Test
  @Phase(beforePhase = TestPhaseB.class)
  public void testTC2WorkflowABeforeTestPhaseB() {
    //    LOG.info("Workflow A before phase TestPhaseB");
  }

  @Test
  @Phase(afterPhase = TestPhaseB.class)
  public void testTC2WorkflowAAfterTestPhaseB() {
    //    LOG.info("Workflow A after phase TestPhaseB");
  }
}