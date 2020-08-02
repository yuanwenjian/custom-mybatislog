package com.yuanwj;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.yuanwj.console.ConsolePanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MyBatisLogToolWindow implements ToolWindowFactory {


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ConsolePanel consolePanel = new ConsolePanel();
        final JComponent jComponent = consolePanel.ConsolePanel(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(jComponent, "", false);
        toolWindow.setIcon(Icons.MyBatisIcon);
        toolWindow.getContentManager().addContent(content);
    }
}