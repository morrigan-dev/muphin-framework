package de.morrigan.dev.test.muphit.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphit.core.phase.AbstractPhase;

public class TestPhaseA extends AbstractPhase<String> {

  /** Logger für Debug/Fehlerausgaben */
  private static final Logger LOG = LoggerFactory.getLogger(TestPhaseA.class);

  public TestPhaseA() {
    super("Phase A", "");
  }

  @Override
  public boolean execute(String data) {
    LOG.info("execute action from phase A");
    return true;
  }
}
