package de.morrigan.dev.test.muphin.core;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphin.core.MuphinSession;
import de.morrigan.dev.test.muphin.helper.TestPhaseA;
import de.morrigan.dev.test.muphin.helper.WorkflowA;

public class MuphinSessionTest {

  /** Logger für Debug/Fehlerausgaben */
  private static final Logger LOG = LoggerFactory.getLogger(MuphinSessionTest.class);

  private MuphinSession sut;

  @Before
  public void setup() {
    this.sut = MuphinSession.getInstance();
  }

  @After
  public void tearDown() {
    this.sut.clear();
  }

  @Test
  public void testConstruction() {
    Map<String, Object> dataCache = getDataCache(this.sut);
    assertThat(dataCache, is(notNullValue()));
    assertThat(dataCache.size(), is(equalTo(0)));
  }

  @Test
  public void testPutData() {
    String testKey = "TestKey";
    String testValue = "TestValue";
    this.sut.putData(testKey, testValue);

    Map<String, Object> dataCache = getDataCache(this.sut);
    assertThat(dataCache, is(notNullValue()));
    assertThat(dataCache.size(), is(equalTo(1)));
    assertThat(dataCache.get(testKey), is(equalTo(testValue)));
  }

  @Test
  public void testRemove() {
    String testKey = "TestKey";
    this.sut.putData(testKey, "TestValue");

    Map<String, Object> dataCache = getDataCache(this.sut);
    assertThat(dataCache, is(notNullValue()));
    assertThat(dataCache.size(), is(equalTo(1)));
    this.sut.remove(testKey);
    assertThat(dataCache.size(), is(equalTo(0)));
  }

  @Test
  public void testClear() {
    String testKey = "TestKey";
    TestPhaseA testPhaseA = new TestPhaseA();
    WorkflowA workflowA = new WorkflowA();
    this.sut.putData(testKey, "TestValue");
    this.sut.setCurrentPhase(testPhaseA);
    this.sut.setCurrentWorkflow(workflowA);
    Map<String, Object> dataCache = getDataCache(this.sut);
    assertThat(dataCache, is(notNullValue()));
    assertThat(dataCache.size(), is(equalTo(1)));
    assertThat(this.sut.getCurrentPhase(), is(equalTo(testPhaseA)));
    assertThat(this.sut.getCurrentWorkflow(), is(equalTo(workflowA)));

    this.sut.clear();

    assertThat(dataCache, is(notNullValue()));
    assertThat(dataCache.size(), is(equalTo(0)));
    assertThat(this.sut.getCurrentPhase(), is(nullValue()));
    assertThat(this.sut.getCurrentWorkflow(), is(nullValue()));
  }

  @Test
  public void testGetDataWithEmptyOptional() {
    Optional<String> optData = this.sut.getData("TestKey", String.class);
    assertThat(optData, emptyOptional());
  }

  @Test
  public void testGetDataWithStringValue() {
    String testKey = "TestKey";
    String testValue = "TestValue";
    this.sut.putData(testKey, testValue);

    Optional<String> optData = this.sut.getData(testKey, String.class);
    assertThat(optData, optionalWithValue(is(equalTo(testValue))));
  }

  @Test
  public void testGetDataWithInvalidType() {
    String testKey = "TestKey";
    String testValue = "TestValue";
    this.sut.putData(testKey, testValue);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> this.sut.getData(testKey, Date.class));
    assertThat(exception.getMessage(), containsString(testKey));
    assertThat(exception.getMessage(), containsString("not match"));
    assertThat(exception.getMessage(), containsString(Date.class.toString()));
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getDataCache(MuphinSession sut) {
    try {
      return (Map<String, Object>) ReflectionUtil.getFieldValue(MuphinSession.class.getDeclaredField("dataCache"), sut);
    } catch (NoSuchFieldException | SecurityException e) {
      throw new AssertionError(e.getMessage(), e);
    }
  }
}
