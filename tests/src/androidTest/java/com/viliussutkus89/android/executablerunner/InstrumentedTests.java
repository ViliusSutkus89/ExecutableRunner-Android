package com.viliussutkus89.android.executablerunner;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class InstrumentedTests {

  @Test
  public void runSimpleExecutable() throws IOException {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    ExecutableRunner runner = new ExecutableRunner(appContext, "libSimpleProgram.so");
    assertEquals(246, runner.run("123"));
  }

  @Test
  public void runExecutableLinkedAgainstLibrary() throws IOException {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    ExecutableRunner runner = new ExecutableRunner(appContext, "libExecutableLinkedAgainstLibrary.so");
    assertEquals(246, runner.run("123"));
  }

  @Test
  public void EnvironmentVariableVerification() throws IOException {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    ExecutableRunner runner = new ExecutableRunner(appContext, "libVerifyEnvVar.so");
    runner.addEnvironmentValue("hello", "world");
    assertEquals(0, runner.run("hello", "world"));
  }

  @Test
  public void EnvironmentVariableVerificationNoFalsePositive() throws IOException {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    ExecutableRunner runner = new ExecutableRunner(appContext, "libVerifyEnvVar.so");
    runner.addEnvironmentValue("hello", "not world");
    assertNotEquals(0, runner.run("hello", "world"));
  }

}
