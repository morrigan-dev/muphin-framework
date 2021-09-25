package de.morrigan.dev.muphit.core.runner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphit.core.annotation.WorkflowTest;
import de.morrigan.dev.muphit.core.workflow.AbstractWorkflow;

public class WorkflowRunner extends Runner {

  /** Logger für Debug/Fehlerausgaben */
  private static final Logger LOG = LoggerFactory.getLogger(WorkflowRunner.class);

  private static Set<Class<?>> getWorkflowTestClasses(String packageName) {
    Set<Class<?>> result = new HashSet<>();
    Reflections refUtil = new Reflections(packageName);
    try {
      result.addAll(refUtil.getTypesAnnotatedWith(WorkflowTest.class));
    } catch (ReflectionsException e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  private Map<Class<? extends AbstractWorkflow>, Set<Class<?>>> testClassesByWorkflow;

  public WorkflowRunner() {
    this("");
  }

  public WorkflowRunner(String packageName) {
    super();

    Set<Class<?>> workflowTestClasses = getWorkflowTestClasses(packageName);
    this.testClassesByWorkflow = splitTestClassesByWorkflow(workflowTestClasses);
  }

  private Map<Class<? extends AbstractWorkflow>, Set<Class<?>>> splitTestClassesByWorkflow(
      Set<Class<?>> workflowTestClasses) {
    Map<Class<? extends AbstractWorkflow>, Set<Class<?>>> result = new HashMap<>();
    for (Class<?> testClass : workflowTestClasses) {
      WorkflowTest annotation = testClass.getAnnotation(WorkflowTest.class);
      Class<? extends AbstractWorkflow> workflow = annotation.value();
      result.compute(workflow, (key, value) -> {
        if (value == null) {
          value = new HashSet<>();
        }
        value.add(testClass);
        return value;
      });
    }
    return result;
  }

  @Override
  public Description getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void run(RunNotifier notifier) {
    this.testClassesByWorkflow.forEach((workflow, testClasses) -> {
      LOG.info("Run tests for workflow: {}", workflow);

    });

  }

  public Set<Class<?>> getWorkflowTestClasses(Class<? extends AbstractWorkflow> workflow) {
    return new HashSet<>(this.testClassesByWorkflow.get(workflow));
  }

  public Map<Class<? extends AbstractWorkflow>, Set<Class<?>>> getTestClassesByWorkflow() {
    return this.testClassesByWorkflow;
  }

}
