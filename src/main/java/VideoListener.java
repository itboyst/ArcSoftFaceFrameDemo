
import org.bytedeco.opencv.opencv_core.IplImage;

/**
 * @author st7251
 * @date 2020/3/2 15:37
 */
public interface VideoListener {

    void onStart();

    /**
     * 预览数据回调
     * @param iplImage 预览数据
     */
    void onPreview(IplImage iplImage);


    void onCancel();

    /**
     * 当出现异常时执行
     * @param e 相机相关异常
     */
    void onError(Exception e);

}
