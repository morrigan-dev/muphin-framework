package de.morrigan.dev.test.muphin.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphin.core.phase.AbstractPhase;

public class TestPhaseB extends AbstractPhase {

  /** Logger für Debug/Fehlerausgaben */
  private static final Logger LOG = LoggerFactory.getLogger(TestPhaseB.class);

  public TestPhaseB() {
    super("Test", "Phase B");
  }

  @Override
  public boolean execute() {
    LOG.info("execute action from phase B");
    return true;
  }
}
