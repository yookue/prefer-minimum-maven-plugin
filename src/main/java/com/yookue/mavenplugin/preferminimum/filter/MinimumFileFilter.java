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


import java.io.File;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.shared.filtering.DefaultMavenFileFilter;
import org.apache.maven.shared.filtering.MavenFileFilter;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.utils.io.FileUtils.FilterWrapper;
import org.codehaus.plexus.component.annotations.Component;


/**
 * Packages minimum css/js files instead of original ones
 *
 * @see org.apache.maven.shared.filtering.DefaultMavenFileFilter
 * @reference "https://github.com/aperto/sourceurl-resourcefilter"
 */
@Component(role = MavenFileFilter.class, hint = MinimumFileFilter.FILTER_NAME, description = "Packages minimum css/js files instead of original ones")
@SuppressWarnings({"unused", "JavadocDeclaration", "JavadocLinkAsPlainText"})
public class MinimumFileFilter extends DefaultMavenFileFilter {
    /**
     * The value is {@code "preferMinimumFileFilter"}
     */
    static final String FILTER_NAME = "preferMinimumFileFilter";    // $NON-NLS-1$
    private static final String DOT_MIN = ".min";    // $NON-NLS-1$
    private static final String HYPHEN_MIN = "-min";    // $NON-NLS-1$

    @Override
    public void copyFile(@Nonnull File from, @Nonnull File to, boolean filtering, @Nonnull List<FilterWrapper> filterWrappers, @Nullable String encoding, boolean overwrite) throws MavenFilteringException {
        boolean copyFile = true;
        String extension = FilenameUtils.getExtension(from.getName());
        if (from.isFile() && StringUtils.equalsAnyIgnoreCase(extension, "js", "css")) {    // $NON-NLS-1$ // $NON-NLS-2$
            String basename = FilenameUtils.getBaseName(from.getName());
            if (!StringUtils.endsWithIgnoreCase(basename, DOT_MIN) && !StringUtils.endsWithIgnoreCase(basename, HYPHEN_MIN)) {
                File dotFrom = new File(from.getParentFile(), StringUtils.join(basename, DOT_MIN, FilenameUtils.EXTENSION_SEPARATOR_STR, extension));
                File minusFrom = new File(from.getParentFile(), StringUtils.join(basename, HYPHEN_MIN, FilenameUtils.EXTENSION_SEPARATOR_STR, extension));
                try {
                    if (FileUtils.directoryContains(from.getParentFile(), dotFrom) || FileUtils.directoryContains(from.getParentFile(), minusFrom)) {
                        copyFile = false;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        if (copyFile) {
            super.copyFile(from, to, filtering, filterWrappers, encoding, overwrite);
        } else {
            FileUtils.deleteQuietly(to);
        }
    }
}
