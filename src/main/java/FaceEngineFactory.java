import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @Author: st7251
 * @Date: 2018/10/16 13:47
 */
@Data
@Slf4j
public class FaceEngineFactory extends BasePooledObjectFactory<FaceEngine> {

    private String libPath;
    private String appId;
    private String sdkKey;
    private EngineConfiguration engineConfiguration;


    public FaceEngineFactory(String libPath, String appId, String sdkKey,  EngineConfiguration engineConfiguration) {
        this.appId = appId;
        this.sdkKey = sdkKey;
        this.libPath = libPath;
        this.engineConfiguration = engineConfiguration;
    }


    @Override
    public FaceEngine create() throws Exception {


        FaceEngine faceEngine = new FaceEngine(libPath);
        int activeCode = faceEngine.activeOnline(appId, sdkKey);
        log.info("faceEngineActiveCode:" + activeCode);
        int initCode = faceEngine.init(engineConfiguration);
        log.info("faceEngineInitCode:" + initCode);
        return faceEngine;
    }

    @Override
    public PooledObject<FaceEngine> wrap(FaceEngine faceEngine) {
        return new DefaultPooledObject<>(faceEngine);
    }


    @Override
    public void destroyObject(PooledObject<FaceEngine> p) throws Exception {
        FaceEngine faceEngine = p.getObject();
        int result = faceEngine.unInit();
        super.destroyObject(p);
    }
}
