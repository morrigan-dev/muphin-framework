package de.morrigan.dev.test.muphin.core.cmd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.junit.Before;
import org.junit.Test;

import de.morrigan.dev.muphin.core.cmd.DefaultCmdQueryBuilder;

public class DefaultCmdQueryBuilderTest {

  private DefaultCmdQueryBuilder sut;

  @Before
  public void setup() {
    this.sut = new DefaultCmdQueryBuilder();
  }

  @Test
  public void testConstruction() {
    StringBuilder cmdQuery = getCmdQuery(this.sut);
    assertThat(cmdQuery, is(notNullValue()));
    assertThat(cmdQuery.length(), is(equalTo(0)));
  }

  @Test
  public void testChangeDirectory() {
    this.sut.changeDirectory("testDir");
    StringBuilder cmdQuery = getCmdQuery(this.sut);
    assertThat(cmdQuery, is(notNullValue()));
    assertThat(cmdQuery.toString(), is(equalTo("cd testDir")));
  }

  @Test
  public void testGitPull() {
    this.sut.gitPull();
    StringBuilder cmdQuery = getCmdQuery(this.sut);
    assertThat(cmdQuery, is(notNullValue()));
    assertThat(cmdQuery.toString(), is(equalTo("git pull")));
  }

  @Test
  public void testAddCustomCommand() {
    this.sut.addCustomCommand("testCommand");
    StringBuilder cmdQuery = getCmdQuery(this.sut);
    assertThat(cmdQuery, is(notNullValue()));
    assertThat(cmdQuery.toString(), is(equalTo("testCommand")));
  }

  @Test
  public void testMultipleCommands() {
    this.sut.addCustomCommand("testCommand1");
    this.sut.addCustomCommand("testCommand2");
    StringBuilder cmdQuery = getCmdQuery(this.sut);
    assertThat(cmdQuery, is(notNullValue()));
    assertThat(cmdQuery.toString(), is(equalTo("testCommand1 && testCommand2")));
  }

  public void testGetCommand() {
    this.sut.addCustomCommand("testCommand1");
    this.sut.addCustomCommand("testCommand2");
    assertThat(this.sut.getCommand(), is(equalTo("testCommand1 && testCommand2")));
  }

  private StringBuilder getCmdQuery(DefaultCmdQueryBuilder sut) {
    try {
      return (StringBuilder) ReflectionUtil.getFieldValue(DefaultCmdQueryBuilder.class.getDeclaredField("cmdQuery"),
          sut);
    } catch (NoSuchFieldException | SecurityException e) {
      throw new AssertionError(e.getMessage(), e);
    }
  }
}
