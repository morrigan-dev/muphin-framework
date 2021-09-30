package de.morrigan.dev.muphit.core.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.MethodSorter;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphit.core.annotation.Phase;
import de.morrigan.dev.muphit.core.annotation.WorkflowTest;
import de.morrigan.dev.muphit.core.phase.AbstractPhase;
import de.morrigan.dev.muphit.core.phase.NoPhase;
import de.morrigan.dev.muphit.core.phase.SetupPhase;
import de.morrigan.dev.muphit.core.phase.TearDownPhase;
import de.morrigan.dev.muphit.core.workflow.AbstractWorkflow;

public class WorkflowRunner extends Runner {

  /** Logger für Debug/Fehlerausgaben */
  private static final Logger LOG = LoggerFactory.getLogger(WorkflowRunner.class);

  private static final String BEFORE_PHASE = "before ";
  private static final String AFTER_PHASE = "after ";

  private static Set<Class<?>> scanForWorkflowTestClasses(String packageName) {
    Set<Class<?>> result = new HashSet<>();
    Reflections refUtil = new Reflections(packageName);
    try {
      result.addAll(refUtil.getTypesAnnotatedWith(WorkflowTest.class));
    } catch (ReflectionsException e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  private final Lock childrenLock = new ReentrantLock();
  private TestClass testClass;

  // Guarded by childrenLock
  private Map<String, Map<String, List<FrameworkMethod>>> children;
  private Map<Class<? extends AbstractWorkflow>, AbstractWorkflow> workflowCache;
  private Map<Class<? extends AbstractPhase<?>>, AbstractPhase<?>> phaseCache;

  private final ConcurrentMap<FrameworkMethod, Description> methodDescriptions = new ConcurrentHashMap<>();

  public WorkflowRunner(Class<?> testClass) throws InstantiationException, IllegalAccessException {
    this(testClass, "");
  }

  public WorkflowRunner(Class<?> testClass, String packageName) {
    super();

    this.workflowCache = new HashMap<>();
    this.phaseCache = new HashMap<>();
    this.testClass = new TestClass(testClass);
    Set<Class<?>> workflowTestClasses = scanForWorkflowTestClasses(packageName);
    scanForPhases(workflowTestClasses);
  }

  private AbstractWorkflow getWorkflow(Class<? extends AbstractWorkflow> workflowClass) {
    this.workflowCache.compute(workflowClass, (key, value) -> {
      if (value == null) {
        try {
          value = workflowClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          LOG.error(e.getMessage(), e);
        }
      }
      return value;
    });
    return this.workflowCache.get(workflowClass);
  }

  private AbstractPhase<?> getPhase(Class<? extends AbstractPhase<?>> phaseClass) {
    this.phaseCache.compute(phaseClass, (key, value) -> {
      if (value == null) {
        try {
          value = phaseClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          LOG.error(e.getMessage(), e);
        }
      }
      return value;
    });
    return this.phaseCache.get(phaseClass);
  }

  private String beforePhaseKey(AbstractPhase<?> phase) {
    return StringUtils.join(BEFORE_PHASE, phase.getName());
  }

  private String afterPhaseKey(AbstractPhase<?> phase) {
    return StringUtils.join(AFTER_PHASE, phase.getName());
  }

  private boolean ignorePhase(AbstractPhase<?> phaseClass) {
    return phaseClass instanceof NoPhase;
  }

  private void scanForPhases(Set<Class<?>> workflowTestClasses) {

    this.children = new HashMap<>();

    for (Class<?> workflowTestClass : workflowTestClasses) {
      WorkflowTest annotation = workflowTestClass.getAnnotation(WorkflowTest.class);
      Class<? extends AbstractWorkflow> workflowClass = annotation.value();

      AbstractWorkflow workflow = getWorkflow(workflowClass);

      this.children.compute(workflow.getName(), (key, value) -> {
        if (value == null) {
          value = new HashMap<>();
        }

        Method[] methods = MethodSorter.getDeclaredMethods(workflowTestClass);
        for (Method method : methods) {
          Phase phaseAnnotation = method.getAnnotation(Phase.class);
          if (phaseAnnotation != null) {
            computePhases(value, method, phaseAnnotation, true);
            computePhases(value, method, phaseAnnotation, false);
          }
        }

        return value;
      });
    }
    LOG.info("{}", this.children);
  }

  private void computePhases(Map<String, List<FrameworkMethod>> value, Method method,
      Phase phaseAnnotation, boolean before) {
    Class<? extends AbstractPhase<?>> phaseClass = before ? phaseAnnotation.beforePhase()
        : phaseAnnotation.afterPhase();
    AbstractPhase<?> phase = getPhase(phaseClass);
    if (!ignorePhase(phase)) {
      String phaseKey = before ? beforePhaseKey(phase) : afterPhaseKey(phase);
      value.compute(phaseKey, (key, methodsOfPhase) -> {
        if (methodsOfPhase == null) {
          methodsOfPhase = new ArrayList<>();
        }
        methodsOfPhase.add(new FrameworkMethod(method));
        return methodsOfPhase;
      });
    }
  }

  private List<FrameworkMethod> getChildren(String workflow, String phaseKey) {
    List<FrameworkMethod> result = new ArrayList<>();
    Map<String, List<FrameworkMethod>> childrenOfWorkflow = this.children.get(workflow);
    if (childrenOfWorkflow != null) {
      result = childrenOfWorkflow.get(phaseKey);
    }
    return result;
  }

  /**
   * @return the annotations that should be attached to this runner's description.
   */
  protected Annotation[] getRunnerAnnotations() {
    return this.testClass.getAnnotations();
  }

  /**
   * Returns a name used to describe this Runner
   */
  protected String getName() {
    return this.testClass.getName();
  }

  /**
   * Returns a {@link TestClass} object wrapping the class to be executed.
   */
  public final TestClass getTestClass() {
    return this.testClass;
  }

  /**
   * Returns the name that describes {@code method} for {@link Description}s. Default implementation is the method's
   * name
   */
  protected String testName(FrameworkMethod method) {
    return method.getName();
  }

  protected Description describeChild(FrameworkMethod method) {
    Description description = this.methodDescriptions.get(method);

    if (description == null) {
      description = Description.createTestDescription(method.getMethod().getDeclaringClass(),
          testName(method), method.getAnnotations());
      this.methodDescriptions.putIfAbsent(method, description);
    }

    return description;
  }

  @Override
  public Description getDescription() {
    Class<?> clazz = getTestClass().getJavaClass();
    Description description;
    // if subclass overrides `getName()` then we should use it
    // to maintain backwards compatibility with JUnit 4.12
    if (clazz == null || !clazz.getName().equals(getName())) {
      description = Description.createSuiteDescription(getName(), getRunnerAnnotations());
    } else {
      description = Description.createSuiteDescription(clazz, getRunnerAnnotations());
    }

    for (AbstractWorkflow workflow : getWorkflows()) {
      Description workflowDescription = Description.createTestDescription(workflow.getClass(),
          workflow.getClass().getSimpleName());
      List<AbstractPhase<?>> phases = getPhases(workflow);
      for (AbstractPhase<?> phase : phases) {
        Description phaseDescription = Description.createTestDescription(phase.getClass(),
            phase.getClass().getSimpleName());

        List<FrameworkMethod> beforePhaseMethods = getChildren(workflow.getName(), beforePhaseKey(phase));
        for (FrameworkMethod child : beforePhaseMethods) {
          phaseDescription.addChild(describeChild(child));
        }

        List<FrameworkMethod> afterPhaseMethods = getChildren(workflow.getName(), afterPhaseKey(phase));
        for (FrameworkMethod child : afterPhaseMethods) {
          phaseDescription.addChild(describeChild(child));
        }

        workflowDescription.addChild(phaseDescription);
      }
      description.addChild(workflowDescription);
    }

    return description;
  }

  @Override
  public void run(RunNotifier notifier) {
    for (AbstractWorkflow workflow : getWorkflows()) {
      LOG.info("Run tests for workflow: {}", workflow.getName());
      List<AbstractPhase<?>> phases = getPhases(workflow);
      for (AbstractPhase<?> phase : phases) {
        LOG.info("Run tests for phase: {}", phase.getName());

        List<FrameworkMethod> beforePhaseMethods = getChildren(workflow.getName(), beforePhaseKey(phase));
        for (FrameworkMethod testMethod : beforePhaseMethods) {
          LOG.info("{}", testMethod);
          runChild(testMethod, notifier);
        }
        LOG.info("Run phase: {}", phase.getName());
        List<FrameworkMethod> afterPhaseMethods = getChildren(workflow.getName(), afterPhaseKey(phase));
        for (FrameworkMethod testMethod : afterPhaseMethods) {
          LOG.info("{}", testMethod);
          runChild(testMethod, notifier);
        }

      }
    }
  }

  /**
   * Runs a {@link Statement} that represents a leaf (aka atomic) test.
   */
  protected final void runLeaf(Statement statement, Description description,
      RunNotifier notifier) {
    EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
    eachNotifier.fireTestStarted();
    try {
      statement.evaluate();
    } catch (AssumptionViolatedException e) {
      eachNotifier.addFailedAssumption(e);
    } catch (Throwable e) {
      eachNotifier.addFailure(e);
    } finally {
      eachNotifier.fireTestFinished();
    }
  }

  protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
    Description description = describeChild(method);
    if (isIgnored(method)) {
      notifier.fireTestIgnored(description);
    } else {
      Statement statement = new Statement() {
        @Override
        public void evaluate() throws Throwable {
          methodBlock(method).evaluate();
        }
      };
      runLeaf(statement, description, notifier);
    }
  }

  /**
   * Returns a new fixture for running a test. Default implementation executes the test class's no-argument constructor
   * (validation should have ensured one exists).
   */
  protected Object createTest(FrameworkMethod method) throws Exception {
    return method.getDeclaringClass().getConstructor().newInstance();
  }

  protected Statement methodBlock(final FrameworkMethod method) {
    Object test;
    try {
      test = new ReflectiveCallable() {
        @Override
        protected Object runReflectiveCall() throws Throwable {
          return createTest(method);
        }
      }.run();
    } catch (Throwable e) {
      return new Fail(e);
    }

    Statement statement = methodInvoker(method, test);
    return statement;
  }

  /**
   * Returns a {@link Statement} that invokes {@code method} on {@code test}
   */
  protected Statement methodInvoker(FrameworkMethod method, Object test) {
    return new InvokeMethod(method, test);
  }

  /**
   * Evaluates whether {@link FrameworkMethod}s are ignored based on the {@link Ignore} annotation.
   */
  protected boolean isIgnored(FrameworkMethod child) {
    return child.getAnnotation(Ignore.class) != null;
  }

  private List<AbstractWorkflow> getWorkflows() {
    List<AbstractWorkflow> workflows = new ArrayList<>();
    this.workflowCache.forEach((workflowClass, workflow) -> {
      workflows.add(workflow);
    });
    Collections.sort(workflows, (w1, w2) -> w1.getName().compareTo(w2.getName()));
    return workflows;
  }

  private List<AbstractPhase<?>> getPhases(AbstractWorkflow workflow) {
    List<AbstractPhase<?>> phases = new ArrayList<>();
    phases.add(getPhase(SetupPhase.class));
    phases.addAll(workflow.getPhases());
    phases.add(getPhase(TearDownPhase.class));
    return phases;
  }
}
