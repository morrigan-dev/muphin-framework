package de.morrigan.dev.test.muphin.helper;

import java.util.List;

import de.morrigan.dev.muphin.core.phase.AbstractPhase;
import de.morrigan.dev.muphin.core.workflow.AbstractWorkflow;

public class WorkflowX extends AbstractWorkflow {

   public WorkflowX(List<Class<? extends AbstractPhase>> phases) {
      super("Workflow X", phases);
   }
}
