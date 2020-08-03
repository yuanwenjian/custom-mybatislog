package com.yuanwj.action;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import com.yuanwj.Icons;
import com.yuanwj.util.ConfigUtil;
import com.yuanwj.util.PrintUtil;
import com.yuanwj.util.RestoreSqlUtil;
import com.yuanwj.util.StringConst;
import org.apache.commons.lang.StringUtils;

import java.awt.*;

/**
 * 初始化数据
 *
 * @author ob
 */
public class CustomMyBatisLog extends AnAction {
    private static String preparingLine = "";
    private static String parametersLine = "";
    private static boolean isEnd = false;

    public CustomMyBatisLog() {
        super(Icons.MyBatisIcon);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }
        CaretModel caretModel = e.getData(LangDataKeys.EDITOR).getCaretModel();
        Caret currentCaret = caretModel.getCurrentCaret();
        String sqlText = currentCaret.getSelectedText();
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(StringConst.WINDOWNAME);
        toolWindow.show(null);
        //激活Restore Sql tab
        toolWindow.activate(null);
        final String preparing = ConfigUtil.getPreparing(project);
        final String parameters = ConfigUtil.getParameters(project);
        if (StringUtils.isNotEmpty(sqlText)) {
            String[] sqlArr = sqlText.split("\n");
            if (isSelectedText(project, sqlText, sqlArr, preparing, parameters)) {
                setSelectedTextFormat(project, sqlArr, preparing, parameters);
            }
        }
    }

    /**
     * 是否存在关键字文本
     *
     * @param project    项目
     * @param sqlText    文本
     * @param preparing  关键字
     * @param parameters 关键字
     */
    private boolean isSelectedText(Project project, String sqlText, String[] sqlArr, String preparing, String parameters) {
        if (StringUtils.isNotBlank(sqlText) && sqlText.contains(preparing) && sqlText.contains(parameters)) {
            if (sqlArr.length >= 2) {
                return true;
            }
        }
        PrintUtil.println(project, "Can't restore sql from selection.", PrintUtil.getOutputAttributes(null, JBColor.YELLOW));
        PrintUtil.println(project, StringConst.SPLIT_LINE, ConsoleViewContentType.USER_INPUT);
        this.reset();
        return false;
    }

    /**
     * 设置显示的文本
     *
     * @param project
     * @param sqlArr
     * @param preparing
     * @param parameters
     */
    private void setSelectedTextFormat(Project project, String[] sqlArr, String preparing, String parameters) {
        for (int i = 0; i < sqlArr.length; ++i) {
            String currentLine = sqlArr[i];
            if (StringUtils.isBlank(currentLine)) {
                continue;
            }
            if (currentLine.contains(preparing)) {
                preparingLine = currentLine;
                continue;
            } else {
                currentLine += "\n";
            }
            if (StringUtils.isEmpty(preparingLine)) {
                continue;
            }
            if (currentLine.contains(parameters)) {
                parametersLine = currentLine;
            } else {
                if (StringUtils.isBlank(parametersLine)) {
                    continue;
                }
                parametersLine += currentLine;
            }
            if (!parametersLine.endsWith("Parameters: \n") && !parametersLine.endsWith("null\n") && !RestoreSqlUtil.endWithAssembledTypes(parametersLine)) {
                if (i == sqlArr.length - 1) {
                    PrintUtil.println(project, "Can't restore sql from selection.", PrintUtil.getOutputAttributes(null, JBColor.YELLOW));
                    PrintUtil.println(project, StringConst.SPLIT_LINE, ConsoleViewContentType.USER_INPUT);
                    this.reset();
                    break;
                }
                continue;
            } else {
                isEnd = true;
            }
            if (StringUtils.isNotEmpty(preparingLine) && StringUtils.isNotEmpty(parametersLine) && isEnd) {
                int indexNum = ConfigUtil.getIndexNum(project);
                String preStr = "--[ "+project.getName()+ " ] " + indexNum + "restore sql from selection  - ==>";
                ConfigUtil.setIndexNum(project, ++indexNum);

                PrintUtil.println(project, preStr, ConsoleViewContentType.USER_INPUT);
                System.out.println(preStr);
                String restoreSql = RestoreSqlUtil.restoreSql(project, preparingLine, parametersLine);
//                if (ConfigUtil.getSqlFormat(project)) {
//                    restoreSql = PrintUtil.format(restoreSql);
//                }
                //高亮显示
                PrintUtil.println(project, restoreSql, PrintUtil.getOutputAttributes(null, new Color(255, 200, 0)));
                PrintUtil.println(project, StringConst.SPLIT_LINE, ConsoleViewContentType.USER_INPUT);
                this.reset();
            }
        }
    }

    /**
     * 重置
     */
    private void reset() {
        preparingLine = "";
        parametersLine = "";
        isEnd = false;
    }


}
