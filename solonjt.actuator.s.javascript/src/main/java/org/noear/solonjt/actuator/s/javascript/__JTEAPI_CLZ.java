package org.noear.solonjt.actuator.s.javascript;

import org.noear.solon.core.XContext;
import org.noear.solonjt.actuator.ActuatorFactory;
import org.noear.solonjt.model.AFileModel;

import java.util.Map;

public class __JTEAPI_CLZ {
    public String require(String path) throws Exception {
        String name = path.replace("/", "__");
        String name2 = name.replace(".", "_") + "__lib";

        AFileModel file = ActuatorFactory.fileGet(path);

        JavascriptJtActuator.singleton().preLoad(name2, file);

        return name2;
    }

    public Object modelAndView(String path, Map<String, Object> model) throws Exception {
        String path2 = path;//AFileUtil.path2(path);//不用转为*
        String name = path2.replace("/", "__");

        AFileModel file = ActuatorFactory.fileGet(path2);

        if (file.file_id > 0) {
            return ActuatorFactory.call(name,file, XContext.current(),model,true);
        } else {
            return "";
        }
    }
}
