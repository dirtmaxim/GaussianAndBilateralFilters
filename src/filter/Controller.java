package filter;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {

    // Controller fields.
    @FXML
    private BorderPane borderPane0;
    @FXML
    private BorderPane borderPane1;
    @FXML
    private BorderPane borderPane2;
    @FXML
    private BorderPane borderPane3;
    @FXML
    private Button buttonLoad;
    @FXML
    private TextField textFieldGaussianDistanceSigma;
    @FXML
    private Button buttonGaussian;
    @FXML
    private Button buttonDX;
    @FXML
    private Button buttonDY;
    @FXML
    private TextField textFieldBilateralDistanceSigma;
    @FXML
    private TextField textFieldBilateralIntensitySigma;
    @FXML
    private Button buttonBilateral;
    @FXML
    private Button buttonSave;
    @FXML
    private GridPane gridPaneImage0;
    @FXML
    private GridPane gridPaneImage1;
    @FXML
    private GridPane gridPaneImage2;
    @FXML
    private GridPane gridPaneImage3;

    // Fields for logic.
    private Image originalImage;
    private Label inProcess;
    private ImageView imageView0;
    private ImageView imageView0Bilateral;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private String image0Extension;
    private String image0BilateralExtension;
    private String image1Extension;
    private String image2Extension;
    private String image3Extension;
    private GaussianFilter gaussianFilter;
    private BilateralFilter bilateralFilter;
    private EdgeDetectionFilter edgeDetectionFilter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inProcess = new Label("In process...");
        inProcess.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        inProcess.setTextFill(Color.WHITE);

        buttonLoad.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.gif", "*.jpeg", "*.jpg", "*.png"));
            fileChooser.setInitialDirectory(new File("."));
            File file = fileChooser.showOpenDialog(buttonLoad.getScene().getWindow());

            if (file != null) {
                image0Extension = getImageExtension(file);
                originalImage = new Image(file.toURI().toString());
                borderPane0.setCenter(inProcess);
                new Thread(() -> {
                    imageView0 = new ImageView(originalImage);
                    imageView0.setPreserveRatio(true);
                    imageView0.setSmooth(true);
                    imageView0.fitWidthProperty().bind(gridPaneImage0.widthProperty());
                    imageView0.fitHeightProperty().bind(gridPaneImage0.heightProperty());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            borderPane0.setCenter(imageView0);
                            textFieldGaussianDistanceSigma.setDisable(false);
                            buttonGaussian.setDisable(false);
                            buttonDX.setDisable(false);
                            buttonDY.setDisable(false);
                            textFieldBilateralDistanceSigma.setDisable(false);
                            textFieldBilateralIntensitySigma.setDisable(false);
                            buttonBilateral.setDisable(false);
                        }
                    });
                }).start();
            }
        });

        buttonGaussian.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            Float gaussianDistanceSigma;

            try {
                gaussianDistanceSigma = Float.parseFloat(textFieldGaussianDistanceSigma.getText());
            } catch (NumberFormatException e) {
                gaussianDistanceSigma = null;
            }

            if (gaussianDistanceSigma != null && gaussianDistanceSigma > 0.1 && originalImage != null) {
                image1Extension = image0Extension;
                gaussianFilter = new GaussianFilter(originalImage, gaussianDistanceSigma);
                borderPane1.setCenter(inProcess);
                new Thread(() -> {
                    imageView1 = new ImageView(gaussianFilter.filterImage());
                    imageView1.setPreserveRatio(true);
                    imageView1.setSmooth(true);
                    imageView1.fitWidthProperty().bind(gridPaneImage1.widthProperty());
                    imageView1.fitHeightProperty().bind(gridPaneImage1.heightProperty());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            borderPane1.setCenter(imageView1);
                            buttonSave.setDisable(false);
                        }
                    });
                }).start();
            } else {
                textFieldGaussianDistanceSigma.requestFocus();
            }
        });

        buttonDX.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            Float gaussianDistanceSigma;

            try {
                gaussianDistanceSigma = Float.parseFloat(textFieldGaussianDistanceSigma.getText());
            } catch (NumberFormatException e) {
                gaussianDistanceSigma = null;
            }

            if (gaussianDistanceSigma != null && gaussianDistanceSigma > 0.4 && originalImage != null) {
                image2Extension = image0Extension;
                edgeDetectionFilter = new EdgeDetectionFilter(originalImage, gaussianDistanceSigma);
                borderPane2.setCenter(inProcess);
                new Thread(() -> {
                    imageView2 = new ImageView(edgeDetectionFilter.filterImage(DerivativeType.X));
                    imageView2.setPreserveRatio(true);
                    imageView2.setSmooth(true);
                    imageView2.fitWidthProperty().bind(gridPaneImage2.widthProperty());
                    imageView2.fitHeightProperty().bind(gridPaneImage2.heightProperty());
                    Platform.runLater(() -> {
                        borderPane2.setCenter(imageView2);
                        buttonSave.setDisable(false);
                    });
                }).start();
            } else {
                textFieldGaussianDistanceSigma.requestFocus();
            }
        });

        buttonDY.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            Float gaussianDistanceSigma;

            try {
                gaussianDistanceSigma = Float.parseFloat(textFieldGaussianDistanceSigma.getText());
            } catch (NumberFormatException e) {
                gaussianDistanceSigma = null;
            }

            if (gaussianDistanceSigma != null && gaussianDistanceSigma > 0.4 && originalImage != null) {
                image3Extension = image0Extension;
                edgeDetectionFilter = new EdgeDetectionFilter(originalImage, gaussianDistanceSigma);
                borderPane3.setCenter(inProcess);
                new Thread(() -> {
                    imageView3 = new ImageView(edgeDetectionFilter.filterImage(DerivativeType.Y));
                    imageView3.setPreserveRatio(true);
                    imageView3.setSmooth(true);
                    imageView3.fitWidthProperty().bind(gridPaneImage3.widthProperty());
                    imageView3.fitHeightProperty().bind(gridPaneImage3.heightProperty());
                    Platform.runLater(() -> {
                        borderPane3.setCenter(imageView3);
                        buttonSave.setDisable(false);
                    });
                }).start();
            } else {
                textFieldGaussianDistanceSigma.requestFocus();
            }
        });

        buttonBilateral.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            Float bilateralDistanceSigma;
            Float bilateralIntensitySigma;

            try {
                bilateralDistanceSigma = Float.parseFloat(textFieldBilateralDistanceSigma.getText());
            } catch (NumberFormatException e) {
                bilateralDistanceSigma = null;
            }

            try {
                bilateralIntensitySigma = Float.parseFloat(textFieldBilateralIntensitySigma.getText());
            } catch (NumberFormatException e) {
                bilateralIntensitySigma = null;
            }

            if (bilateralDistanceSigma != null && bilateralDistanceSigma > 0.1 && originalImage != null) {
                if (bilateralIntensitySigma != null && bilateralIntensitySigma > 0.1) {
                    image0BilateralExtension = image0Extension;
                    bilateralFilter = new BilateralFilter(originalImage, bilateralDistanceSigma, bilateralIntensitySigma);
                    borderPane0.setCenter(inProcess);
                    new Thread(() -> {
                        imageView0Bilateral = new ImageView(bilateralFilter.filterImage());
                        imageView0Bilateral.setPreserveRatio(true);
                        imageView0Bilateral.setSmooth(true);
                        imageView0Bilateral.fitWidthProperty().bind(gridPaneImage0.widthProperty());
                        imageView0Bilateral.fitHeightProperty().bind(gridPaneImage0.heightProperty());
                        Platform.runLater(() -> {
                            borderPane0.setCenter(imageView0Bilateral);
                            buttonSave.setDisable(false);
                        });
                    }).start();
                } else {
                    textFieldBilateralIntensitySigma.requestFocus();
                }
            } else {
                textFieldBilateralDistanceSigma.requestFocus();
            }
        });

        buttonSave.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose directory");
            directoryChooser.setInitialDirectory(new File("."));
            final File directory = directoryChooser.showDialog(buttonLoad.getScene().getWindow());
            if (directory != null) {
                new Thread(() -> {
                    if (imageView0Bilateral != null) {
                        saveImage(directory, "Processed image (Bilateral filter).", image0BilateralExtension, imageView0Bilateral);
                    }
                    if (imageView1 != null) {
                        saveImage(directory, "Processed image (Gaussian filter).", image1Extension, imageView1);
                    }
                    if (imageView2 != null) {
                        saveImage(directory, "Processed image (Edge detection G(x) filter).", image2Extension, imageView2);
                    }
                    if (imageView3 != null) {
                        saveImage(directory, "Processed image (Edge detection G(y) filter).", image3Extension, imageView3);
                    }
                    Platform.runLater(() -> {
                        buttonSave.setText("Done");
                        new Thread(() -> {
                            try {
                                TimeUnit.SECONDS.sleep(3);
                                Platform.runLater(() -> {
                                    buttonSave.setText("Save");
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    });
                }).start();
            }
        });
    }

    private String getImageExtension(File image) {
        String imageFileName = image.getName();
        int index = imageFileName.lastIndexOf(".");
        if (index > Math.max(imageFileName.lastIndexOf("/"), imageFileName.lastIndexOf("\\"))) {
            return imageFileName.substring(index + 1);
        }
        return null;
    }

    private void saveImage(File directory, String imageName, String imageExtension, ImageView imageView) {
        try {
            BufferedImage processedImage;
            if (imageExtension.equals("jpg") || imageExtension.equals("jpeg")) {
                processedImage = ImageTool.deleteAlphaChannel(SwingFXUtils.fromFXImage(imageView.getImage(), null));
            } else {
                processedImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
            }
            ImageIO.write(processedImage, imageExtension, new File(directory, imageName + imageExtension));
        } catch (IOException e) {
            System.err.println("Something goes wrong while bilateral filtered file saving.");
        }
    }
}