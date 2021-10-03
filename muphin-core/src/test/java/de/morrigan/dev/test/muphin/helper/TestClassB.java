package de.morrigan.dev.test.muphin.helper;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.morrigan.dev.muphin.core.annotation.Phase;
import de.morrigan.dev.muphin.core.annotation.WorkflowTest;
import de.morrigan.dev.muphin.core.phase.SetupPhase;
import de.morrigan.dev.muphin.core.phase.TearDownPhase;
import de.morrigan.dev.muphin.core.runner.WorkflowRunner;

@RunWith(WorkflowRunner.class)
@WorkflowTest(WorkflowB.class)
public class TestClassB {

  @Test
  @Ignore("just for testing the @Ignore annotation")
  @Phase(beforePhase = SetupPhase.class)
  public void testWorkflowBBeforeSetupPhaseIgnored() {
    assertTrue(true);
  }

  @Test
  @Phase(beforePhase = SetupPhase.class)
  public void testWorkflowBBeforeSetupPhase() {
    assertTrue(true);
  }

  @Test
  @Phase(afterPhase = SetupPhase.class)
  public void testWorkflowBAfterSetupPhase1() {
    assertTrue(true);
  }

  @Test
  @Phase(afterPhase = SetupPhase.class)
  public void testWorkflowBAfterSetupPhase2() {
    assertTrue(true);
  }

  @Test
  @Phase(beforePhase = TearDownPhase.class)
  public void testWorkflowBBeforeTearDownPhase() {
    assertTrue(true);
  }

  @Test
  @Phase(afterPhase = TearDownPhase.class)
  public void testWorkflowBAfterTearDownPhase() {
    assertTrue(true);
  }

  @Test
  @Phase(beforePhase = TestPhaseA.class)
  public void testWorkflowBBeforeTestPhaseA() {
    assertTrue(true);
  }

  @Test
  @Phase(afterPhase = TestPhaseA.class)
  public void testWorkflowBAfterTestPhaseA() {
    assertTrue(true);
  }

  @Test
  @Phase(beforePhase = TestPhaseC.class)
  public void testWorkflowBBeforeTestPhaseC() {
    assertTrue(true);
  }

  @Test
  @Phase(afterPhase = TestPhaseC.class)
  public void testWorkflowBAfterTestPhaseC() {
    assertTrue(true);
  }
}