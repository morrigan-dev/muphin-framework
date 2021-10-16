package de.morrigan.dev.test.muphin.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphin.core.phase.AbstractPhase;

public class TestPhaseA extends AbstractPhase {

  /** Logger für Debug/Fehlerausgaben */
  private static final Logger LOG = LoggerFactory.getLogger(TestPhaseA.class);

  public TestPhaseA() {
    super("Test", "Phase A");
  }

  @Override
  public boolean execute() {
    LOG.info("execute action from phase A");
    return true;
  }
}
