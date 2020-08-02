package com.yuanwj.console;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;

public class ConsoleActionGroup {

    private static Runnable myFilterAction;

    public static void withFilter(Runnable filter) {
        ConsoleActionGroup.myFilterAction = filter;
    }

    public static Runnable getMyFilterAction() {
        return ConsoleActionGroup.myFilterAction;
    }

    /**
     * 过滤按钮
     */
    public static class FilterAction extends AnAction implements DumbAware {
        public FilterAction() {
            super("Filter", "Filter", AllIcons.General.Filter);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            ConsoleActionGroup.myFilterAction.run();
        }
    }
}
