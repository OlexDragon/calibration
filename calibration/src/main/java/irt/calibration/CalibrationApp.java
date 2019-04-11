package irt.calibration;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Optional;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.helpers.SerialPortWorker;
import irt.calibration.helpers.StageSizeAndPosition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class CalibrationApp extends Application {

	private final static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        launch(args);
    }

	private String name;
	private String version;
	private StageSizeAndPosition size = new StageSizeAndPosition(getClass());
	private static Scene scene;

	public static Properties properties;

    @Override
	public void init() throws Exception {

    	Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

    	properties = new Properties();
		properties.load(getClass().getResourceAsStream("/project.properties"));
		version = properties.getProperty("version");
		name = properties.getProperty("name");
	}

    public void start(Stage stage) throws Exception {

		final ObservableList<Image> icons = stage.getIcons();

		icons.add(new Image(getClass().getResourceAsStream("/images/calibration.ico")));

		String fxmlFile = "/fxml/Main.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

        scene = new Scene(rootNode);
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle(name + " v " + version);
        stage.setScene(scene);
        size.setStageProperties(stage);
        stage.show();
    }

	@Override
	public void stop() throws Exception {
		size.saveStageProperties();
		SerialPortWorker.disconect();
	}

	public static <T> T getException(Class<T> returnClass, Throwable throwable) {

		if(throwable == null)
			return null;

		if(returnClass.isInstance(throwable))
			return returnClass.cast(throwable);

		return getException(returnClass, throwable.getCause());
	}

	private static Alert alert;
	public static void showAlert(String title, final String message, AlertType alertType) {

		Platform.runLater(
					()->{

						if(alert==null) {

							alert = new Alert(alertType);
							Optional.ofNullable(scene).map(Scene::getWindow).ifPresent(alert::initOwner);
							alert.setTitle(title);
							alert.setHeaderText(null);
							alert.setContentText(message);
							alert.showAndWait();
							alert = null;

						}else{

							alert.setAlertType(alertType);
							final String contentText = alert.getContentText() + '\n';
							alert.setContentText(contentText + message);

						}});
	}

	public final static String SERIAL_PORT_IS_BUSY	 = "Serial port %s is busy.";
    UncaughtExceptionHandler uncaughtExceptionHandler = (thread, throwable)->{

    	SerialPortException serialPortException = getException(SerialPortException.class, throwable);

    	if(serialPortException != null && serialPortException.getMessage().contains("Port busy")) {

    		logger.catching(Level.DEBUG, throwable);
			final String format = String.format(SERIAL_PORT_IS_BUSY, serialPortException.getPortName());
			showAlert("Connection error.", format, AlertType.ERROR);
			return;
    	}

    	final SerialPortTimeoutException serialPortTimeoutException = getException(SerialPortTimeoutException.class, throwable);
    	if(serialPortTimeoutException != null) {

    		logger.error(logger.getName());
    		logger.catching(Level.DEBUG, throwable);
			showAlert("Connection error.", "Connection timeout", AlertType.ERROR);
    		return;
    	}

    	final NumberFormatException numberFormatException = getException(NumberFormatException.class, throwable);
    	if(numberFormatException != null) {

    		logger.catching(Level.DEBUG, throwable);
			showAlert("Unit Address Error", "Error in the address field", AlertType.ERROR);
    		return;
    	}
    	logger.error(thread);
    	logger.catching(throwable);
    };
}
