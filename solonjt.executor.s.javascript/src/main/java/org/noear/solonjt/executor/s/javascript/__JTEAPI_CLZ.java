package org.noear.solonjt.executor.s.javascript;

import org.noear.snack.ONode;
import org.noear.solon.core.XContext;
import org.noear.solonjt.executor.ExecutorFactory;
import org.noear.solonjt.model.AFileModel;

import java.util.Map;

public class __JTEAPI_CLZ {

    public String serialize_java(Object obj) throws Exception{
        return ONode.serialize(obj);
    }

    public String require(String path) throws Exception {
        String name = path.replace("/", "__");
        String name2 = name.replace(".", "_") + "__lib";

        AFileModel file = ExecutorFactory.fileGet(path);

        JavascriptJtExecutor.singleton().preLoad(name2, file);

        return name2;
    }

    public Object modelAndView(String path, Map<String, Object> model) throws Exception {
        String path2 = path;//AFileUtil.path2(path);//不用转为*
        String name = path2.replace("/", "__");

        AFileModel file = ExecutorFactory.fileGet(path2);

        if (file.file_id > 0) {
            return ExecutorFactory.call(name,file, XContext.current(),model,true);
        } else {
            return "";
        }
    }
}
