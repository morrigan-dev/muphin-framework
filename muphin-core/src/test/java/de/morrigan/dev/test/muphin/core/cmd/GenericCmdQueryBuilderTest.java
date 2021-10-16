package de.morrigan.dev.test.muphin.core.cmd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.junit.Before;
import org.junit.Test;

import de.morrigan.dev.muphin.core.cmd.DefaultCmdQueryBuilder;
import de.morrigan.dev.muphin.core.cmd.GenericCmdQueryBuilder;

public class GenericCmdQueryBuilderTest {

  private class TestCmdQueryBuilder extends GenericCmdQueryBuilder<TestCmdQueryBuilder> {

    @Override
    protected TestCmdQueryBuilder getBuilder() {
      return this;
    }
  }

  private TestCmdQueryBuilder sut;

  @Before
  public void setup() {
    this.sut = new TestCmdQueryBuilder();
  }

  @Test
  public void testConstruction() {
    DefaultCmdQueryBuilder delegate = getDelegate(this.sut);
    assertThat(delegate, is(notNullValue()));
  }

  @Test
  public void testChangeDirectory() {
    TestCmdQueryBuilder builder = this.sut.changeDirectory("testDir");
    assertThat(builder, is(equalTo(this.sut)));
  }

  @Test
  public void testGitPull() {
    TestCmdQueryBuilder builder = this.sut.gitPull();
    assertThat(builder, is(equalTo(this.sut)));
  }

  @Test
  public void testAddCustomCommand() {
    TestCmdQueryBuilder builder = this.sut.addCustomCommand("testCommnad");
    assertThat(builder, is(equalTo(this.sut)));
  }

  private DefaultCmdQueryBuilder getDelegate(TestCmdQueryBuilder sut) {
    try {
      return (DefaultCmdQueryBuilder) ReflectionUtil.getFieldValue(
          GenericCmdQueryBuilder.class.getDeclaredField("delegate"), sut);
    } catch (NoSuchFieldException | SecurityException e) {
      throw new AssertionError(e.getMessage(), e);
    }
  }
}
