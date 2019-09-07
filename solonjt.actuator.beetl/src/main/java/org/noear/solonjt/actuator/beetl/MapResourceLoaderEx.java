package org.noear.solonjt.actuator.beetl;

import org.beetl.core.Resource;
import org.beetl.core.resource.MapResourceLoader;
import org.noear.solonjt.actuator.ActuatorFactory;
import org.noear.solonjt.model.AFileModel;

public class MapResourceLoaderEx extends MapResourceLoader {
    @Override
    public Resource getResource(String key) {
        if(key.startsWith("/")){
            String path = key;
            String path2 = path; //AFileUtil.path2(path);//不用转为*
            String name = path2.replace("/", "__");

            Resource tml = super.getResource(name);

            if (tml != null) {
                return tml;
            }

            try {
                AFileModel file = ActuatorFactory.fileGet(path2);

                if (BeetlJtActuator.singleton().put(name, file)) {
                    return super.getResource(name);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }
        return super.getResource(key);
    }
}
