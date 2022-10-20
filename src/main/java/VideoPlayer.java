
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.IplImage;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_BGR24;

/**
 * @author st7251
 * @date 2020/3/2 15:30
 */
public class VideoPlayer {

    private FrameGrabber frameGrabber;

    private VideoListener videoListener;

    private Timer timer = new Timer(true);

    public VideoPlayer(File deviceFile) {
        try {
            frameGrabber = FFmpegFrameGrabber.createDefault(deviceFile);
        } catch (FFmpegFrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    public VideoPlayer(String devicePath) {
        try {
            frameGrabber = FFmpegFrameGrabber.createDefault(devicePath);
        } catch (FFmpegFrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    public VideoPlayer(int deviceNumber) {
        try {
            frameGrabber= VideoInputFrameGrabber.createDefault(deviceNumber);
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    public void setListener(VideoListener videoListener) {
        this.videoListener = videoListener;
    }



    public void start() {
        try {
            frameGrabber.setPixelFormat(AV_PIX_FMT_BGR24 );
            frameGrabber.start();
            videoListener.onStart();
        } catch (FrameGrabber.Exception e) {
            videoListener.onError(e);
        }
//        final int[] lengthInVideoFrames = {frameGrabber.getLengthInVideoFrames()};

//        Java2DFrameConverter converter = new Java2DFrameConverter();
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();//转换器


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    Frame grab = frameGrabber.grab();
//                    lengthInVideoFrames[0]--;
                    if (grab != null) {
//                        BufferedImage bufferedImage = converter.convert(grab);
//                        videoListener.onPreview(bufferedImage);
                        IplImage iplImage = converter.convert(grab);//抓取一帧视频并将其转换为图像，至于用这个图像用来做什么？加水印，人脸识别等等自行添加
                        if (iplImage != null) {
                            videoListener.onPreview(iplImage);
                        }
                    }
//                    if (lengthInVideoFrames[0] <= 0) {
//                        stop();
//                    }
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
            frameGrabber.stop();
            videoListener.onCancel();
        } catch (FrameGrabber.Exception e) {
            videoListener.onError(e);
        }
    }
}
