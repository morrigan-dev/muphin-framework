package de.morrigan.dev.muphin.core.phase;

import java.util.Optional;

import de.morrigan.dev.muphin.core.MuphinSession;
import de.morrigan.dev.muphin.core.cmd.CmdSession;

/**
 * The tear down phase is always the last phase of a workflow and is therefore always executed last.
 * <p>
 * This phase is inserted internally by the framework at the end of a workflow and therefore does not have to be
 * inserted manually by the developer. Nevertheless, test methods can register in the tear down phase and are executed
 * in this phase before the internal tear down mechanisms.
 *
 * @author morrigan
 * @since 0.0.1
 */
public class TearDownPhase extends AbstractPhase {

   public TearDownPhase() {
      super(AbstractPhase.INTERNAL_KIND, TearDownPhase.class.getSimpleName());
   }

   @Override
   public boolean execute() {
      MuphinSession muphinSession = MuphinSession.getInstance();
      Optional<CmdSession> optCmdSession = muphinSession.getData(MuphinSession.CMD_SESSION, CmdSession.class);
      if (optCmdSession.isPresent()) {
         optCmdSession.get().close();
      }
      return true;
   }
}
