package irt.calibration;

import static irt.calibration.exception.ExceptionWrapper.catchConsumerException;
import static irt.calibration.helpers.CalibrationWorker.creatNewGroupName;
import static irt.calibration.helpers.CalibrationWorker.getCalibrationTypes;
import static irt.calibration.helpers.CalibrationWorker.getNewGroupDialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.helpers.CalibrationSequenceDialog;
import irt.calibration.helpers.CalibrationWorker;
import irt.calibration.helpers.ThreadWorker;
import irt.calibration.tools.Tool;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

public class CalibrationController extends AnchorPane implements Observer{
	private final static Logger logger = LogManager.getLogger();

	public CalibrationController(Tool... tools) {

		CalibrationSequenceDialog.setTools(tools);
		CalibrationWorker.setTools(tools);
		Arrays.stream(tools).forEach(tool->tool.addObserver(this));

		try {

			String fxmlFile = "/fxml/Calibration.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch (IOException exc) {
			logger.catching(exc);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		logger.error(arg);
	}

    private StringConverter<Path> pathConverter = new StringConverter<Path>() {
		
		@Override public String toString(Path path) {
			return path.getFileName().toString().replace(".seq", "");
		}
		
		@Override public Path fromString(String string) { return null; }
	};

    @FXML private ChoiceBox<Path> chbCalibrationGroupName;
    @FXML private ChoiceBox<Path> chbCalibrationSequence;
    @FXML private TextArea taAnswers;
    @FXML private MenuItem miEditSequence;
    @FXML private MenuItem miShowSequence;
    @FXML private MenuItem miDeleteSequence;
    @FXML private Button btnStart;
    @FXML private Button btnCansel;

	@FXML void initialize() throws IOException {
		chbCalibrationGroupName.setConverter(pathConverter);
		chbCalibrationSequence.setConverter(pathConverter);
		chbCalibrationSequence.getSelectionModel().selectedIndexProperty()
		.addListener(
				(o,ov,nv)->{
					final boolean disable = nv.intValue()<0;
					miEditSequence.setDisable(disable);
					miShowSequence.setDisable(disable);
					miDeleteSequence.setDisable(disable);
					btnStart.setDisable(disable);});
		fillCalibrationGroupName();
	}

	@FXML void onCreateNewSquence() throws IOException {

    	new CalibrationSequenceDialog(chbCalibrationGroupName.getScene().getWindow()).showAndWait()
    	.ifPresent(saveSequence());
    }

	public Consumer<? super Map<Integer, List<String>>> saveSequence() {
		return map->{

    		final List<String> list = map.get(0);
    		list.parallelStream()
     		.findAny()
    		.map(sequenceName->sequenceName + ".seq")
    		.ifPresent(saveSequenceToFile(map));
    	};
	}

	@FXML void onEditSquence() throws IOException {
		final CalibrationSequenceDialog dialog = new CalibrationSequenceDialog(chbCalibrationGroupName.getScene().getWindow());
		dialog.initialize(chbCalibrationSequence.getSelectionModel().getSelectedItem());
		dialog.showAndWait()
    	.ifPresent(saveSequence());
    }

    @FXML void onCreateGroupName() {

    	TextInputDialog dialog = getNewGroupDialog(chbCalibrationGroupName.getScene().getWindow());
    	addTextFieldListener(dialog);

    	dialog.showAndWait()
    	.ifPresent(
    			catchConsumerException(
    					newType->{
    						ThreadWorker.runThread(()->creatNewGroupName(newType));
    						fillCalibrationGroupName();
    					}));
    }

    @FXML void onShowSquence() {
    	Optional.ofNullable(chbCalibrationSequence.getSelectionModel().getSelectedItem())
    	.ifPresent(path->{
    		try {
				final byte[] bytes = Files.readAllBytes(path);
				taAnswers.setText(new String(bytes));
			} catch (IOException e) {
				logger.catching(e);
			}
    	});
    }

    @FXML void onDeleteSquence() {
    	Optional.ofNullable(chbCalibrationSequence.getSelectionModel().getSelectedItem())
    	.ifPresent(source->{
    		try {

    			Path target = source.getParent().resolve(source.getFileName().toString().replaceAll(".seq", ".old"));
				Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
				fillCalibrationSequence(chbCalibrationGroupName.getSelectionModel().getSelectedItem());

    		} catch (IOException e) {
				logger.catching(e);
			}
    	});
    }

    CalibrationWorker calibrationWorker;
    @FXML void onStart() {
    	btnCansel.setDisable(false);
    	btnStart.setDisable(true);
    	final Path path = chbCalibrationSequence.getSelectionModel().getSelectedItem();
    	calibrationWorker = new CalibrationWorker(path);
    	calibrationWorker.addObserver(this);
    }

    @FXML void onCansel() {
    	Optional.ofNullable(calibrationWorker).ifPresent(CalibrationWorker::cansel);
    }

	public Consumer<? super String> saveSequenceToFile(Map<Integer, List<String>> map) {
		return fileName->{

			logger.error(map);
			StringBuffer sb = new StringBuffer();
			map.entrySet().stream().map(Entry<Integer, List<String>>::getValue)
			.filter(action->action.size()>1)
			.forEach(
					action->{

						logger.error(action);
						final String actionLine = action.stream().collect(Collectors.joining(","));

						sb.append(actionLine);
						sb.append(System.lineSeparator());
					});
			Path path = chbCalibrationGroupName.getSelectionModel().getSelectedItem().resolve(fileName);
			final File file = path.toFile();
			try {
				if(!file.exists())
					file.createNewFile();

				try(	FileWriter fileWriter = new FileWriter(file);
						BufferedWriter writer = new BufferedWriter(fileWriter)){

					writer.append(sb);
					writer.flush();
				}

				fillCalibrationSequence(chbCalibrationGroupName.getSelectionModel().getSelectedItem());

			} catch (IOException e) {
				logger.catching(e);
			}
 		};
	}

    private void fillCalibrationGroupName() throws IOException {
		ObservableList<Path> items = FXCollections.observableArrayList(getCalibrationTypes());
		chbCalibrationGroupName.setItems(items );
		final SingleSelectionModel<Path> selectionModel = chbCalibrationGroupName.getSelectionModel();
		selectionModel.selectedIndexProperty().addListener((o,ov,nv)->chbCalibrationSequence.setDisable(selectionModel.getSelectedIndex()<0));
		selectionModel.selectedItemProperty().addListener(groupNameListener());
	}

    private ChangeListener<? super Path> groupNameListener() {
		return (o,ov,nv)->{

			try {

				fillCalibrationSequence(nv);

			} catch (IOException e) {
				logger.catching(e);
			}
		};
	}

	public void fillCalibrationSequence(Path path) throws IOException {
		if(path==null) {
			chbCalibrationSequence.getItems().clear();
			return;
		}
		final List<Path> paths = Files.walk(path).filter(Files::isRegularFile).filter(p->p.getFileName().toString().endsWith(".seq")).sorted().collect(Collectors.toList());
		ObservableList<Path> items = FXCollections.observableArrayList(paths);
		chbCalibrationSequence.setItems(items );
	}

	private void addTextFieldListener(TextInputDialog dialog) {

		Node button = dialog.getDialogPane().lookupButton(ButtonType.OK);
		button.setDisable(true);

		((TextField) dialog.getDialogPane().lookup(".text-field")).textProperty()
		.addListener(
				(o,ov,nv)->{
					if(nv.trim().isEmpty()) {

						button.setDisable(true);
						return;
					}
					boolean typeContainsThisName = chbCalibrationGroupName.getItems().parallelStream().filter(name->name.getFileName().toString().equalsIgnoreCase(nv)).findAny().isPresent();
					button.setDisable(typeContainsThisName);
				});
	}
}
