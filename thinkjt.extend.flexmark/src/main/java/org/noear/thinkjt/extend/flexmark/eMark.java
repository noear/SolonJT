package org.noear.thinkjt.extend.flexmark;

import org.noear.thinkjt.extend.flexmark.utils.MarkdownUtils;
import org.noear.solon.annotation.XNote;

public class eMark {
    @XNote("md格式转为html格式")
    public String mdToHtml(String markdown){
        return MarkdownUtils.markdown2Html(markdown);
    }
}
