package de.morrigan.dev.test.muphin.helper;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.morrigan.dev.muphin.core.WorkflowRunner;
import de.morrigan.dev.muphin.core.annotation.Phase;
import de.morrigan.dev.muphin.core.annotation.WorkflowTest;
import de.morrigan.dev.muphin.core.phase.SetupPhase;
import de.morrigan.dev.muphin.core.phase.TearDownPhase;

@RunWith(WorkflowRunner.class)
@WorkflowTest(WorkflowA.class)
public class TestClassA1 {

  @Test
  @Phase(beforePhase = SetupPhase.class)
  public void testTC1WorkflowABeforeSetupPhase() {
    assertTrue(true);
  }

  @Test
  @Phase(afterPhase = SetupPhase.class)
  public void testTC1WorkflowAAfterSetupPhase() {
    assertTrue(true);
  }

  @Test
  @Phase(beforePhase = TearDownPhase.class)
  public void testTC1WorkflowABeforeTearDownPhase() {
    assertTrue(true);
  }

  @Test
  @Phase(afterPhase = TearDownPhase.class)
  public void testTC1WorkflowAAfterTearDownPhase() {
    assertTrue(true);
  }

  @Test
  @Phase(beforePhase = TestPhaseA.class)
  public void testTC1WorkflowABeforeTestPhaseA() {
    assertTrue(true);
  }

  @Test
  @Phase(afterPhase = TestPhaseA.class)
  public void testTC1WorkflowAAfterTestPhaseA() {
    assertTrue(true);
  }

  @Test
  @Phase(beforePhase = TestPhaseB.class)
  public void testTC1WorkflowABeforeTestPhaseB() {
    assertTrue(true);
  }

  @Test
  @Phase(afterPhase = TestPhaseB.class)
  public void testTC1WorkflowAAfterTestPhaseB() {
    assertTrue(true);
  }
}