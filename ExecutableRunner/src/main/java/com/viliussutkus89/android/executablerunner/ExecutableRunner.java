/*
 * ExecutableRunner.java
 *
 * Copyright (C) 2020 - 2021 Vilius Sutkus'89
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.viliussutkus89.android.executablerunner;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ExecutableRunner {
  private final String m_executableName;
  private final File m_executable;
  private final File m_workingDirectory;

  private final Map<String, String> m_environment = new LinkedHashMap<>();

  private OutputStream m_stdout;
  private OutputStream m_stderr;

  public ExecutableRunner(@NonNull Context ctx, @NonNull String executableName) {
    m_executableName = executableName;
    String nativeLibDir = ctx.getApplicationInfo().nativeLibraryDir;
    m_executable = new File(nativeLibDir, executableName);
    m_workingDirectory = ctx.getCacheDir();
    m_environment.put("LD_LIBRARY_PATH", nativeLibDir);
  }

  public ExecutableRunner(@NonNull File executable, @NonNull File workingDirectory) {
    m_executableName = executable.getName();
    m_executable = executable;
    m_workingDirectory = workingDirectory;
    m_environment.put("LD_LIBRARY_PATH", executable.getParent());
  }

  public ExecutableRunner addEnvironmentValue(String key, String value) {
    m_environment.put(key, value);
    return this;
  }

  public ExecutableRunner addEnvironmentValues(Map<String, String> env) {
    for (Map.Entry<String, String> e : env.entrySet()) {
      m_environment.put(e.getKey(), e.getValue());
    }
    return this;
  }

  public ExecutableRunner setStdout(@NonNull OutputStream stdout) {
    m_stdout = stdout;
    return this;
  }

  public ExecutableRunner setStderr(@NonNull OutputStream stderr) {
    m_stderr = stderr;
    return this;
  }

  public int run(String ... arguments) throws IOException {
    Log.v(m_executableName, "Attempting to run executable " + m_executableName);

    if (!m_executable.exists()) {
      throw new FileNotFoundException("Executable " + m_executable.getAbsolutePath() + " not found!");
    }

    List<String> args = new ArrayList<>(1 + arguments.length);
    args.add(m_executable.getAbsolutePath());
    args.addAll(Arrays.asList(arguments));

    ProcessBuilder processBuilder = new ProcessBuilder(args)
        .directory(m_workingDirectory);

    if (m_stdout == m_stderr) {
      processBuilder.redirectErrorStream(true);
    }

    if (0 < this.m_environment.size()) {
      final Map<String, String> environment = processBuilder.environment();
      for (Map.Entry<String, String> e : this.m_environment.entrySet()) {
        environment.put(e.getKey(), e.getValue());
      }
    }

    Process process = processBuilder.start();
    int retVal = ProcessWaitForLoop(process);

    if (null == m_stdout) {
      Scanner s = new Scanner(process.getInputStream()).useDelimiter("\n");
      while (s.hasNext()) {
        Log.i(m_executableName, s.next());
      }
    } else {
      writeStreamToFile(process.getInputStream(), m_stdout);
    }

    if (null != m_stderr && m_stdout != m_stderr) {
      writeStreamToFile(process.getErrorStream(), m_stderr);
    }

    return retVal;
  }

  private static int ProcessWaitForLoop(Process p) {
    // while(Process.isAlive()) is only available from API 26
    while (true) {
      try {
        return p.waitFor();
      } catch (InterruptedException ignored) {
      }
    }
  }

  private static void writeStreamToFile(InputStream stream, OutputStream out) throws IOException {
    int bytesRead;
    byte[] buff = new byte[16 * 1024];
    while (-1 < (bytesRead = stream.read(buff))) {
      out.write(buff, 0, bytesRead);
    }
  }
}
