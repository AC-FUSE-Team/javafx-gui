import java.io.ByteArrayInputStream;

import javafx.animation.AnimationTimer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
/**
 * @title ${type_name}
 * @author Aspen
 * @date ${date}{time}
 * @version 
 */

public class LearningController extends Controller {
	
	@FXML
	private ImageView cameraView;
	@FXML
	private Tab pointerTab;
	@FXML
	private Tab finishTab;	
	@FXML
	private TabPane tabPane;
	@FXML
	private Label resultLabel;
	
	private VideoCapture camvideo;
    private AnimationTimer timer;
    private Mat frozenMat;
    private Image frozenFrame;

    private ComputerVisionDriver cvDriver;
    
    @FXML
    private Tab setSceneTab;
    @FXML
    private Tab takeSceneTab;
	
    BoneTable boneTable = new BoneTable("Skeleton.csv");
	/**
     * 
     */
    @FXML
    public void initialize() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        cvDriver = new ComputerVisionDriver(true);
        frozenMat = new Mat();
        initCamera();

        // 
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == pointerTab) {
                cameraView.setVisible(true);
                // startCamera();
                //
                startDetection();
            } else if (newTab == finishTab) {
                cameraView.setVisible(false);
                stopCamera();
            }
        });
    }
    
    @FXML
    private void clickStartVideo() {
        cameraView.setVisible(true);
        startDetection();
    }
    
    @FXML
    private void clickSnapshot() {
        freezeCamera();
    }
    
    @FXML
    private void clickStopVideo() {
        stopCamera();
        System.out.println("Video stopped");
    }
    
    /**
     * 
     */
    private void startDetection() {
        if (timer != null) timer.stop();

        timer = new AnimationTimer() {
            private final Mat mat = new Mat();

            @Override
            public void handle(long now) {
                if (camvideo.isOpened()) {
                    camvideo.read(mat);
                    if (!mat.empty()) {
                        Mat resultMat = mat.clone();
                        int selectedMarker = cvDriver.findSelectedMarker(mat, resultMat);

                        if (selectedMarker > 0) {
                            Scalar[] markerColor = cvDriver.markerTable.getColor(selectedMarker);
                            if (markerColor != null) {
                                int b = (int) markerColor[0].val[0];
                                int g = (int) markerColor[0].val[1];
                                int r = (int) markerColor[0].val[2];
                                String boneName = boneTable.findBoneByColor(b, g, r);
                                resultLabel.setText(boneName);
                            }
                        }
                        cameraView.setImage(mat2Image(resultMat));
                    }
                }
            }
        };
        timer.start();
    }

    
    private void initCamera() {
        camvideo = new VideoCapture(0);
        if (!camvideo.isOpened()) {
            camvideo.open(0);
        }
        if (!camvideo.isOpened()) {
            System.out.println("Failed to open camera");
        }
    }

    //
    private void startCamera() {
        if (timer != null) timer.stop();

        timer = new AnimationTimer() {
            private final Mat mat = new Mat();

            @Override
            public void handle(long now) {
                if (camvideo.isOpened()) {
                    camvideo.read(mat);
                    if (!mat.empty()) {
                        Image img = mat2Image(mat);
                        cameraView.setImage(img);
                    }
                }
            }
        };
        timer.start();
    }

    //
    private void freezeCamera() {
        if (timer != null) {
            timer.stop();
        }

        // 
        Mat mat = new Mat();
        if (camvideo.isOpened()) {
            camvideo.read(mat);
            if (!mat.empty()) {
            	mat.copyTo(frozenMat);
            	
            	Mat resultMat = mat.clone();
                int selectedMarker = cvDriver.findSelectedMarker(mat, resultMat);
                
                if (selectedMarker > 0) {
                    Scalar[] markerColor = cvDriver.markerTable.getColor(selectedMarker);
                    if (markerColor != null) {
                        int b = (int) markerColor[0].val[0];
                        int g = (int) markerColor[0].val[1];
                        int r = (int) markerColor[0].val[2];
                        String boneName = boneTable.findBoneByColor(b, g, r);
                        resultLabel.setText(boneName);
                    }
                }
                
                frozenFrame = mat2Image(mat);
                cameraView.setImage(frozenFrame);
                System.out.println("Snapshot saved");
                // TODO:
            }
        }
    }

    //
    private void stopCamera() {
        if (timer != null) {
            timer.stop();
        }
    }

    //
    public Image getFrozenFrame() {
        return frozenFrame;
    }

    private Image mat2Image(Mat mat) {
        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".png", mat, byteMat);
        return new Image(new ByteArrayInputStream(byteMat.toArray()));
    }

}
