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

package com.yookue.mavenplugin.preferminimum.filter;


import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.maven.shared.filtering.DefaultMavenResourcesFiltering;
import org.apache.maven.shared.filtering.MavenFileFilter;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.sonatype.plexus.build.incremental.BuildContext;


/**
 * Packages minimum css/js resources instead of original ones
 *
 * @see org.apache.maven.shared.filtering.DefaultMavenResourcesFiltering
 * @reference "https://maven.apache.org/plugins/maven-resources-plugin/examples/custom-resource-filters.html"
 */
@Component(role = MavenResourcesFiltering.class, hint = MinimumResourceFilter.FILTER_NAME, description = "Packages minimum css/js resources instead of original ones")
@SuppressWarnings({"unused", "JavadocDeclaration", "JavadocLinkAsPlainText"})
public class MinimumResourceFilter extends AbstractLogEnabled implements MavenResourcesFiltering, Initializable {
    /**
     * The value is {@code "preferMinimumResourceFilter"}
     */
    static final String FILTER_NAME = "preferMinimumResourceFilter";    // $NON-NLS-1$
    private final DefaultMavenResourcesFiltering resourceFilter = new DefaultMavenResourcesFiltering();

    @Requirement
    private BuildContext buildContext;

    @Requirement(role = MavenFileFilter.class, hint = MinimumFileFilter.FILTER_NAME)
    private MavenFileFilter fileFilter;

    @Override
    public void initialize() throws InitializationException {
        resourceFilter.enableLogging(getLogger());
        resourceFilter.initialize();
        try {
            FieldUtils.writeDeclaredField(resourceFilter, "buildContext", buildContext, true);    // $NON-NLS-1$
            FieldUtils.writeDeclaredField(resourceFilter, "mavenFileFilter", fileFilter, true);    // $NON-NLS-1$
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            getLogger().error(ex.getMessage(), ex);
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("DefaultMavenResourcesFiltering delegator initialized complete");
        }
    }

    @Override
    public void filterResources(@Nonnull MavenResourcesExecution execution) throws MavenFilteringException {
        MavenResourcesExecution cloneExecution = execution.copyOf();
        cloneExecution.setUseDefaultFilterWrappers(false);
        resourceFilter.filterResources(cloneExecution);
    }

    @Override
    public boolean filteredFileExtension(@Nonnull String fileName, @Nullable List<String> userNonFilteredFileExtensions) {
        String extension = FilenameUtils.getExtension(fileName);
        return StringUtils.equalsAnyIgnoreCase(extension, "js", "css");    // $NON-NLS-1$ // $NON-NLS-2$
    }

    @Override
    public List<String> getDefaultNonFilteredFileExtensions() {
        List<String> extensions = resourceFilter.getDefaultNonFilteredFileExtensions();
        Collections.addAll(extensions, "java", "jar", "class");    // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
        Collections.addAll(extensions, "txt", "rtf", "log");    // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
        Collections.addAll(extensions, "zip", "rar", "7z", "tar", "gz");    // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$ // $NON-NLS-4$ // $NON-NLS-5$
        Collections.addAll(extensions, "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf");    // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$ // $NON-NLS-4$ // $NON-NLS-5$ // $NON-NLS-6$ // $NON-NLS-7$
        Collections.addAll(extensions, "less", "tiff", "svg", "ttf", "woff", "woff2");    // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$ // $NON-NLS-4$ // $NON-NLS-5$ // $NON-NLS-6$
        Collections.addAll(extensions, "json", "properties", "xml", "yml", "yaml");    // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$ // $NON-NLS-4$ // $NON-NLS-5$
        return extensions;
    }
}
