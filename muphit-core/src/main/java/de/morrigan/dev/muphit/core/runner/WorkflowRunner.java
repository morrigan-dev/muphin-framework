package de.morrigan.dev.muphit.core.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
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

public class WorkflowRunner extends Runner implements Filterable {

  private interface Callback {
    default void workflow(AbstractWorkflow workflow) {}

    default void phase(AbstractPhase<?> phase) {}

    default void runBeforePhase(FrameworkMethod method, Iterator<FrameworkMethod> iter) {}

    default void runPhase(AbstractWorkflow workflow, AbstractPhase<?> phase) {}

    default void runAfterPhase(FrameworkMethod method, Iterator<FrameworkMethod> iter) {}
  }

  private static final Logger LOG = LoggerFactory.getLogger(WorkflowRunner.class);

  private final Lock childrenLock = new ReentrantLock();
  private TestClass testClass;

  // Guarded by childrenLock
  private Map<String, Map<String, List<FrameworkMethod>>> filteredChildren;
  private Map<Class<? extends AbstractWorkflow>, AbstractWorkflow> workflowCache;
  private Map<Class<? extends AbstractPhase<?>>, AbstractPhase<?>> phaseCache;

  private final ConcurrentMap<FrameworkMethod, Description> methodDescriptions = new ConcurrentHashMap<>();

  public WorkflowRunner(Class<?> testClass) {
    super();

    this.workflowCache = new HashMap<>();
    this.phaseCache = new HashMap<>();
    this.testClass = new TestClass(testClass);
  }

  /**
   * Returns a {@link TestClass} object wrapping the class to be executed.
   */
  public final TestClass getTestClass() {
    return this.testClass;
  }

  @Override
  public Description getDescription() {
    Class<?> clazz = getTestClass().getJavaClass();
    Description description;
    description = Description.createSuiteDescription(clazz, this.testClass.getAnnotations());

    iterateThroughChildren(getFilteredChildren(), new Callback() {
      private Description workflowDescription;
      private Description phaseDescription;

      @Override
      public void workflow(AbstractWorkflow workflow) {
        this.workflowDescription = Description.createTestDescription(workflow.getClass(),
            workflow.getClass().getSimpleName());
        description.addChild(this.workflowDescription);
      }

      @Override
      public void phase(AbstractPhase<?> phase) {
        this.phaseDescription = Description.createTestDescription(phase.getClass(),
            phase.getClass().getSimpleName());
        this.workflowDescription.addChild(this.phaseDescription);
      }

      @Override
      public void runBeforePhase(FrameworkMethod method, Iterator<FrameworkMethod> iter) {
        this.phaseDescription.addChild(describeChild(method));
      }

      @Override
      public void runAfterPhase(FrameworkMethod method, Iterator<FrameworkMethod> iter) {
        this.phaseDescription.addChild(describeChild(method));
      }
    });

    return description;
  }

  @Override
  public void run(RunNotifier notifier) {
    iterateThroughChildren(getFilteredChildren(), new Callback() {

      @Override
      public void runPhase(AbstractWorkflow workflow, AbstractPhase<?> phase) {
        LOG.info("Run phase: {}", phase.getName());
      }

      @Override
      public void runBeforePhase(FrameworkMethod method, Iterator<FrameworkMethod> iter) {
        LOG.info("run test before phase: {}", method);
        runChild(method, notifier);
      }

      @Override
      public void runAfterPhase(FrameworkMethod method, Iterator<FrameworkMethod> iter) {
        LOG.info("run test after phase: {}", method);
        runChild(method, notifier);
      }
    });
  }

  @Override
  public void filter(Filter filter) throws NoTestsRemainException {
    this.childrenLock.lock();
    try {
      Map<String, Map<String, List<FrameworkMethod>>> children = getFilteredChildren();
      iterateThroughChildren(children, new Callback() {

        @Override
        public void runBeforePhase(FrameworkMethod method, Iterator<FrameworkMethod> iter) {
          filter(filter, method, iter);
        }

        @Override
        public void runAfterPhase(FrameworkMethod method, Iterator<FrameworkMethod> iter) {
          filter(filter, method, iter);
        }

        private void filter(Filter filter, FrameworkMethod method, Iterator<FrameworkMethod> iter) {
          if (shouldRun(filter, method)) {
            try {
              filter.apply(method);
            } catch (NoTestsRemainException e) {
              iter.remove();
            }
          } else {
            iter.remove();
          }
        }
      });
      this.filteredChildren = Collections.unmodifiableMap(children);
      if (getFrameworkMethods(this.filteredChildren).isEmpty()) {
        throw new NoTestsRemainException();
      }
    } finally {
      this.childrenLock.unlock();
    }
  }

  protected Description describeChild(FrameworkMethod method) {
    Description description = this.methodDescriptions.get(method);

    if (description == null) {
      description = Description.createTestDescription(method.getMethod().getDeclaringClass(),
          method.getName(), method.getAnnotations());
      this.methodDescriptions.putIfAbsent(method, description);
    }

    return description;
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

    return methodInvoker(method, test);
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

  private List<FrameworkMethod> getFrameworkMethods(Map<String, Map<String, List<FrameworkMethod>>> children) {
    List<FrameworkMethod> frameworkMethods = new ArrayList<>();
    iterateThroughChildren(children, new Callback() {

      @Override
      public void runBeforePhase(FrameworkMethod method, Iterator<FrameworkMethod> iter) {
        frameworkMethods.add(method);
      }

      @Override
      public void runAfterPhase(FrameworkMethod method, Iterator<FrameworkMethod> iter) {
        frameworkMethods.add(method);
      }
    });
    return frameworkMethods;
  }

  private void iterateThroughChildren(Map<String, Map<String, List<FrameworkMethod>>> children, Callback callback) {
    for (AbstractWorkflow workflow : getWorkflows()) {
      callback.workflow(workflow);
      List<AbstractPhase<?>> phases = getPhases(workflow);
      for (AbstractPhase<?> phase : phases) {
        callback.phase(phase);
        Map<String, List<FrameworkMethod>> workflowChildren = children.get(workflow.getName());
        if (workflowChildren != null) {
          for (Iterator<FrameworkMethod> iter = getIterator(workflowChildren, beforePhaseKey(phase)); iter.hasNext();) {
            FrameworkMethod testMethod = iter.next();
            callback.runBeforePhase(testMethod, iter);
          }
          callback.runPhase(workflow, phase);
          for (Iterator<FrameworkMethod> iter = getIterator(workflowChildren, afterPhaseKey(phase)); iter.hasNext();) {
            FrameworkMethod testMethod = iter.next();
            callback.runAfterPhase(testMethod, iter);
          }
        }
      }
    }
  }

  private Iterator<FrameworkMethod> getIterator(Map<String, List<FrameworkMethod>> childrenOfWorkflow, String key) {
    List<FrameworkMethod> methods = childrenOfWorkflow.get(key);
    if (methods == null) {
      methods = new ArrayList<>();
    }
    return methods.iterator();
  }

  private Map<String, Map<String, List<FrameworkMethod>>> getFilteredChildren() {
    if (this.filteredChildren == null) {
      this.childrenLock.lock();
      try {
        this.filteredChildren = Collections.unmodifiableMap(new HashMap<>(getChildren()));
      } finally {
        this.childrenLock.unlock();
      }
    }
    return this.filteredChildren;
  }

  private Map<String, Map<String, List<FrameworkMethod>>> getChildren() {
    return scanForPhases(scanForWorkflowTestClasses());
  }

  private Set<Class<?>> scanForWorkflowTestClasses() {
    Set<Class<?>> workflowTestClasses = new HashSet<>();
    WorkflowTest annotation = this.testClass.getJavaClass().getAnnotation(WorkflowTest.class);
    if (annotation == null) {
      Reflections refUtil = new Reflections("");
      try {
        workflowTestClasses.addAll(refUtil.getTypesAnnotatedWith(WorkflowTest.class));
      } catch (ReflectionsException e) {
        throw new IllegalStateException("An error occurs while scanning classpath for test classes", e);
      }
    } else {
      workflowTestClasses.add(this.testClass.getJavaClass());
    }
    LOG.info("{} classes with workflow test cases found.", workflowTestClasses.size());
    return workflowTestClasses;
  }

  private Map<String, Map<String, List<FrameworkMethod>>> scanForPhases(Set<Class<?>> workflowTestClasses) {
    Map<String, Map<String, List<FrameworkMethod>>> children = new HashMap<>();

    /* Each test method is assigned to a workflow and a phase. Therefore, all workflow test classes must be searched
     * first. Then the @Phase annotation can be used to collect the test methods belonging to this workflow and assign
     * them to their phase.
     * A distinction must be made between test methods that are executed before a phase and test methods that are
     * executed after a phase.
     */
    for (Class<?> workflowTestClass : workflowTestClasses) {
      WorkflowTest annotation = workflowTestClass.getAnnotation(WorkflowTest.class);
      AbstractWorkflow workflow = getWorkflow(annotation.value());
      children.compute(workflow.getName(), (key, value) -> {
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
    LOG.trace("All test methods on the classpath grouped by workflow and phases: {}", children);
    return children;
  }

  private void computePhases(Map<String, List<FrameworkMethod>> value, Method method,
      Phase annotation, boolean before) {
    Class<? extends AbstractPhase<?>> phaseClass = before ? annotation.beforePhase() : annotation.afterPhase();
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

  private boolean ignorePhase(AbstractPhase<?> phaseClass) {
    return phaseClass instanceof NoPhase;
  }

  private List<AbstractWorkflow> getWorkflows() {
    this.childrenLock.lock();
    List<AbstractWorkflow> workflows = new ArrayList<>();
    try {
      this.workflowCache.forEach((workflowClass, workflow) -> workflows.add(workflow));
      Collections.sort(workflows, (w1, w2) -> w1.getName().compareTo(w2.getName()));
    } finally {
      this.childrenLock.unlock();
    }
    return workflows;
  }

  private List<AbstractPhase<?>> getPhases(AbstractWorkflow workflow) {
    List<AbstractPhase<?>> phases = new ArrayList<>();
    phases.add(getPhase(SetupPhase.class));
    phases.addAll(workflow.getPhases());
    phases.add(getPhase(TearDownPhase.class));
    return phases;
  }

  private AbstractWorkflow getWorkflow(Class<? extends AbstractWorkflow> workflowClass) {
    this.childrenLock.lock();
    try {
      return this.workflowCache.compute(workflowClass, (key, value) -> {
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
    } finally {
      this.childrenLock.unlock();
    }
  }

  private AbstractPhase<?> getPhase(Class<? extends AbstractPhase<?>> phaseClass) {
    this.childrenLock.lock();
    try {
      return this.phaseCache.compute(phaseClass, (phaseKey, phase) -> {
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
    } finally {
      this.childrenLock.unlock();
    }
  }

  private boolean shouldRun(Filter filter, FrameworkMethod each) {
    return filter.shouldRun(describeChild(each));
  }

  private String beforePhaseKey(AbstractPhase<?> phase) {
    return StringUtils.join("before ", phase.getName());
  }

  private String afterPhaseKey(AbstractPhase<?> phase) {
    return StringUtils.join("after ", phase.getName());
  }
}
