
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.IplImage;
import java.util.Timer;
import java.util.TimerTask;
import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_BGR24;

/**
 * @author st7251
 * @date 2020/3/2 15:30
 */
public class VideoPlayer {

    private FFmpegFrameGrabber fFmpegFrameGrabber;

    private VideoListener videoListener;

    private Timer timer = new Timer(true);

    public VideoPlayer(String path) {
        fFmpegFrameGrabber = new FFmpegFrameGrabber(path);
    }

    public void setListener(VideoListener videoListener) {
        this.videoListener = videoListener;
    }

    public void start() {
        try {
            fFmpegFrameGrabber.setPixelFormat(AV_PIX_FMT_BGR24 );
            fFmpegFrameGrabber.start();
            videoListener.onStart();
        } catch (FrameGrabber.Exception e) {
            videoListener.onError(e);
        }
        final int[] lengthInVideoFrames = {fFmpegFrameGrabber.getLengthInVideoFrames()};

//        Java2DFrameConverter converter = new Java2DFrameConverter();
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();//转换器


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    Frame grab = fFmpegFrameGrabber.grabImage();
                    lengthInVideoFrames[0]--;
                    if (grab != null) {
//                        BufferedImage bufferedImage = converter.convert(grab);
//                        videoListener.onPreview(bufferedImage);
                        IplImage iplImage = converter.convert(grab);//抓取一帧视频并将其转换为图像，至于用这个图像用来做什么？加水印，人脸识别等等自行添加
                        if (iplImage != null) {
                            videoListener.onPreview(iplImage);
                        }
                    }
                    if (lengthInVideoFrames[0] <= 0) {
                        stop();
                    }
                } catch (Exception e) {
                    videoListener.onError(e);
                }
            }
        };

        timer.schedule(task, 0, 40);
    }

    public void stop() {
        timer.cancel();

        try {
            fFmpegFrameGrabber.stop();
            videoListener.onCancel();
        } catch (FrameGrabber.Exception e) {
            videoListener.onError(e);
        }
    }
}
