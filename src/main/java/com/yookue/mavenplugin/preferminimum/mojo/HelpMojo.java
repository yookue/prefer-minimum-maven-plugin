/*
 * Copyright (c) 2021 Yookue Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yookue.mavenplugin.preferminimum.mojo;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;


/**
 * Displays help information for plugin
 *
 * @see org.apache.maven.plugins.resources.HelpMojo
 */
@Mojo(name = "help", requiresProject = false, threadSafe = true)
@SuppressWarnings({"unused", "JavadocReference"})
public class HelpMojo extends AbstractMojo {
    private static final String HELP_FILE_PATH = "META-INF/usage.txt";    // $NON-NLS-1$

    @Override
    public void execute() throws MojoExecutionException {
        try {
            String content = IOUtils.resourceToString(HELP_FILE_PATH, StandardCharsets.UTF_8, getClass().getClassLoader());
            if (StringUtils.isNotBlank(content)) {
                content = RegExUtils.replaceAll(content, "\\r\\n", System.lineSeparator());    // $NON-NLS-1$
                getLog().info(content);
            } else {
                getLog().warn(String.format("Help file '%s' is missing", HELP_FILE_PATH));
            }
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }
}
