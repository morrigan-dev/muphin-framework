package de.morrigan.dev.test.muphit.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphit.core.phase.AbstractPhase;

public class TestPhaseB extends AbstractPhase<String> {

  /** Logger für Debug/Fehlerausgaben */
  private static final Logger LOG = LoggerFactory.getLogger(TestPhaseB.class);

  public TestPhaseB() {
    super("Phase B", "");
  }

  @Override
  public boolean execute(String data) {
    LOG.info("execute action from phase B");
    return true;
  }
}
