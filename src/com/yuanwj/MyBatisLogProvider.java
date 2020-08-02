package com.yuanwj;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * mybatis log provider
 * @author ob
 */
public class MyBatisLogProvider implements ConsoleFilterProvider {
    @NotNull
    @Override
    public Filter[] getDefaultFilters(@NotNull Project project) {
        Filter filter = new MyBatisLogFilter(project);
        return new Filter[]{filter};
    }
}
