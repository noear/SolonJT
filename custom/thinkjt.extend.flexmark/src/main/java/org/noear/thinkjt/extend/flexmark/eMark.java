package org.noear.thinkjt.extend.flexmark;

import org.noear.thinkjt.extend.flexmark.utils.MarkdownUtils;
import org.noear.solon.annotation.XNote;

public class eMark {
    @XNote("md格式转为html格式")
    public String mdToHtml(String markdown){
        String html = MarkdownUtils.markdown2Html(markdown);

        String html2 = html
                .replace("<li>[ ]", "<li class='task-list-item'><input type=\"checkbox\" disabled=\"\">")
                .replace("<li>[x]","<li class='task-list-item'><input type=\"checkbox\" disabled=\"\" checked=\"\">")
                .replace("\n\n", "<br/>");

        return  html2;
    }
}
