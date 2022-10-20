
import com.arcsoft.face.enums.ImageFormat;
import com.arcsoft.face.toolkit.ImageInfo;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.CvPoint;
import org.bytedeco.opencv.opencv_core.CvScalar;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_imgproc.CvFont;
import java.util.List;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import static org.bytedeco.opencv.global.opencv_core.cvPoint;
import static org.bytedeco.opencv.global.opencv_core.cvScalar;
import static org.bytedeco.opencv.global.opencv_imgproc.cvFont;

/**
 * @author st7251
 * @date 2020/3/2 15:25
 */
public class MainApplication {


    public static void main(String[] args) {

         String libPath = System.getProperty("user.dir")+"\\libs\\WIN64";//虹软引擎库存放路径
         String appId = "";
         String sdkKey = "";
         String videoPath="E:\\FFOutput\\06.mp4";//视频文件路径
         String imagePath="D:\\demoJpg";//需要识别人的注册照路径


        Loader.load(opencv_imgproc.class);
        Loader.load(CvPoint.class);
        Loader.load(CvFont.class);

        CanvasFrame canvas = new CanvasFrame("预览");//新建一个窗口

        canvas.setDefaultCloseOperation(EXIT_ON_CLOSE);

//                VideoPlayer videoPlayer = new VideoPlayer(videoPath);//视频文件
        VideoPlayer videoPlayer = new VideoPlayer(0);//本地相机

        FaceRecognize faceRecognize = new FaceRecognize();
        faceRecognize.initEngine(libPath,appId,sdkKey);

        faceRecognize.registerFace(imagePath);//注册人脸

        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

        videoPlayer.setListener(new VideoListener() {

            CvScalar color = cvScalar(0, 0, 255, 0);       // blue [green] [red]
            CvFont cvFont = cvFont(opencv_imgproc.FONT_HERSHEY_DUPLEX);

            @Override
            public void onStart() {
            }

            @Override
            public void onPreview(IplImage iplImage) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setWidth(iplImage.width());
                imageInfo.setHeight(iplImage.height());
                imageInfo.setImageFormat(ImageFormat.CP_PAF_BGR24);
                byte[] imageData = new byte[iplImage.imageSize()];
                iplImage.imageData().get(imageData);
                imageInfo.setImageData(imageData);
                List<FaceRecognize.FacePreviewInfo> previewInfoList = faceRecognize.detectFaces(imageInfo);

                for (FaceRecognize.FacePreviewInfo facePreviewInfo : previewInfoList) {
                    int x = facePreviewInfo.getFaceInfo().getRect().getLeft();
                    int y = facePreviewInfo.getFaceInfo().getRect().getTop();
                    int xMax = facePreviewInfo.getFaceInfo().getRect().getRight();
                    int yMax = facePreviewInfo.getFaceInfo().getRect().getBottom();

                    CvPoint pt1 = cvPoint(x, y);
                    CvPoint pt2 = cvPoint(xMax, yMax);
                    opencv_imgproc.cvRectangle(iplImage, pt1, pt2, color, 1, 4, 0);

                    FaceRecognize.FaceResult faceResult = faceRecognize.getFaceResult(facePreviewInfo.getFaceInfo(), imageInfo);
                    if (faceResult != null) {
                        try {
                            CvPoint pt3 = cvPoint(x, y - 2);
                            opencv_imgproc.cvPutText(iplImage, faceResult.getName(), pt3, cvFont, color);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                Frame frame = converter.convert(iplImage);
                canvas.showImage(frame);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(Exception e) {

            }
        });

        videoPlayer.start();

        Object o = new Object();
        synchronized (o) {
            try {
                o.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}
