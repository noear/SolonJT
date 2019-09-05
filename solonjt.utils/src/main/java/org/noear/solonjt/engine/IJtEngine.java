package org.noear.solonjt.engine;

import org.noear.solon.core.XContext;
import org.noear.solonjt.model.AFileModel;

import java.util.Map;

public interface IJtEngine {
    /** 支持语言 */
    String language();

    /** 执行 */
    Object exec(String name, AFileModel file, XContext ctx, Map<String, Object> model, boolean asRaw) throws Exception;

    /** 删除代码缓存 */
    void del(String name);

    /** 删除所有代码缓存 */
    void delAll();

    /** 是否已加载 */
    boolean isLoaded(String name);
    /** 预加载代码 */
    boolean preLoad(String name, AFileModel file) throws Exception;
}
