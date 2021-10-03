package de.morrigan.dev.test.muphin.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphin.core.phase.AbstractPhase;

public class TestPhaseC extends AbstractPhase<String> {

  /** Logger für Debug/Fehlerausgaben */
  private static final Logger LOG = LoggerFactory.getLogger(TestPhaseC.class);

  public TestPhaseC() {
    super("Phase C", "");
  }

  @Override
  public boolean execute(String data) {
    LOG.info("execute action from phase C");
    return true;
  }
}
