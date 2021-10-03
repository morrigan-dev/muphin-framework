package de.morrigan.dev.test.muphin.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphin.core.phase.AbstractPhase;

public class TestPhaseX extends AbstractPhase<String> {

  /** Logger für Debug/Fehlerausgaben */
  private static final Logger LOG = LoggerFactory.getLogger(TestPhaseX.class);

  public TestPhaseX(String data) {
    super("Phase X", data);
  }

  @Override
  public boolean execute(String data) {
    LOG.info("execute action from phase X");
    return true;
  }
}