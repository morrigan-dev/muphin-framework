package de.morrigan.dev.muphin.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;

import de.morrigan.dev.muphin.core.phase.AbstractPhase;
import de.morrigan.dev.muphin.core.workflow.AbstractWorkflow;

public class InstanceManager {

   private static final InstanceManager INSTANCE = new InstanceManager();

   public static final InstanceManager getInstance() {
      return INSTANCE;
   }

   private ConcurrentMap<Class<? extends AbstractWorkflow>, AbstractWorkflow> workflows;
   private ConcurrentMap<Class<? extends AbstractPhase>, AbstractPhase> phases;

   private InstanceManager() {
      super();
      this.workflows = new ConcurrentHashMap<>();
      this.phases = new ConcurrentHashMap<>();
   }

   public List<AbstractWorkflow> getWorkflows() {
      List<AbstractWorkflow> listOfWorkflows = new ArrayList<>();
      this.workflows.forEach((workflowClass, workflow) -> listOfWorkflows.add(workflow));
      Collections.sort(listOfWorkflows, (w1, w2) -> w1.getName().compareTo(w2.getName()));
      return listOfWorkflows;
   }

   public AbstractWorkflow getWorkflow(Class<? extends AbstractWorkflow> workflowClass) {
      return this.workflows.compute(workflowClass, (key, value) -> {
         if (value == null) {
            try {
               value = workflowClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
               throw new IllegalArgumentException(StringUtils.join("Can't create a new instance from ", workflowClass,
                        ". Please make sure a default constructor exists."), e);
            }
         }
         return value;
      });
   }

   public AbstractPhase getPhase(Class<? extends AbstractPhase> phaseClass) {
      return this.phases.compute(phaseClass, (phaseKey, phase) -> {
         if (phase == null) {
            try {
               phase = phaseClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
               throw new IllegalArgumentException(StringUtils.join("Can't create a new instance from ", phaseClass,
                        ". Please make sure a default constructor exists."), e);
            }
         }
         return phase;
      });
   }

   public void clear() {
      this.workflows.clear();
      this.phases.clear();
   }
}