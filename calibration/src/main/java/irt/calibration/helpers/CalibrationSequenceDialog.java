package irt.calibration.helpers;

import static irt.calibration.exception.ExceptionWrapper.catchConsumerException;
import static irt.calibration.helpers.OptionalIfElse.of;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.CalibrationApp;
import irt.calibration.anotations.ToolAction;
import irt.calibration.processors.CalibrationToolProcessor;
import irt.calibration.tools.Tool;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class CalibrationSequenceDialog extends Dialog<Map<Integer, List<String>>> {
	private static final String TOOL_NAME = "Tool Name";

	private static final String SEQUENCE_NAME = "sequence-name";

	private final static Logger logger = LogManager.getLogger();

	private static Tool[] tools;

	private final StringConverter<Map.Entry<Object,Object>> converter = new StringConverter<Map.Entry<Object,Object>>() {
		
		@Override
		public String toString(Entry<Object, Object> entry) {
			return entry.getKey().toString();
		}

		@Override public Entry<Object, Object> fromString(String string) { return null; }
	};

	private final GridPane gridPane;

	public CalibrationSequenceDialog(Window window) throws IOException {

		setTitle("Calibration Sequence Editor");
		setHeaderText(null);
		setResizable(true);
    	initOwner(window);
    	final DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    	final ScrollPane scrollPane = new ScrollPane();
    	dialogPane.setContent(scrollPane);
    	dialogPane.setMinSize(700, 200);

    	gridPane = getGridPane();
    	scrollPane.setContent(gridPane);
    	gridPane.addRow(0, getSequenceName());
    	gridPane.addRow(1, getActionLine());

    	setResultConverter(resultConverter());
	}

	public Callback<ButtonType, Map<Integer, List<String>>> resultConverter() {
		return button->{

			if(button!=ButtonType.OK)
				return null;

			final ObservableList<Node> children = gridPane.getChildren();
			final Map<Integer, List<Node>> collect = children.parallelStream()

					.filter(node->!(node instanceof Text))
					.filter(node->!(node instanceof HBox))
					.collect(Collectors.groupingBy(GridPane::getRowIndex));

			// Remove lines with empty fields
			return collect.entrySet().stream()
					.map(mapValueToString())
					.filter(hasEmptyValue())
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		};
	}

    private Predicate<Map.Entry<Integer, List<String>>> hasEmptyValue() {
		return entry->{
			return !entry.getValue().stream().map(String::isEmpty).filter(Boolean::booleanValue).findAny().orElse(false);
		};
	}

	private Function<Map.Entry<Integer, List<Node>>, Map.Entry<Integer, List<String>>> mapValueToString() {

    	return entry->{
    		final List<String> value = entry.getValue().stream().map(nodeToString()).collect(Collectors.toList());
    		return new SimpleEntry<Integer, List<String>>(entry.getKey(), value);
		};
    }

	private Function<Node, String> nodeToString() {
		return node->{
    		final String simpleName = node.getClass().getSimpleName();
    		switch(simpleName) {

    		case "TextField":
    			return ((TextField)node).getText().trim();

    		case "ChoiceBox":
    			return choiceBoxToString(node);

    		default:
    			logger.error("Have to add '{}' case", simpleName);
    			return "";
    		}
		};
	}

	public String choiceBoxToString(Node node) {
		return Optional.ofNullable(((ChoiceBox<?>)node).getSelectionModel().getSelectedItem())
				.map(
				item->{
					final String simpleName = item.getClass().getSimpleName();
					switch (simpleName) {

					case "Entry":
						return ((Entry<?, ?>)item).getKey();

					default:
						return item;
					}})
				.map(Object::toString)
				.orElse("");
	}

	private Node okButton;
	private final ChangeListener<? super String> listener = (o, ov, nv)->okButton.setDisable(nv.trim().isEmpty());

	private GridPane getGridPane() throws IOException {

		GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(10, 10, 10, 10));

		//Enable/Disable login button depending on whether a sequence name(file name) was entered.
		okButton = getDialogPane().lookupButton(ButtonType.OK);
		okButton.setDisable(true);
		okButton.addEventFilter(ActionEvent.ACTION, 
				e->{
					if(!isValid()) {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.initOwner(okButton.getScene().getWindow());
						alert.setTitle("Validation Error");
						alert.setHeaderText("Empty field detected");
						alert.setContentText("To edit, click Cancel. Click 'OK' if you want to ignore lines with empty fields.");
						alert.showAndWait()
						.ifPresent(
								button->{
									if(button!=ButtonType.OK)
										e.consume();});
					}
				});
		return grid;
	}

	private boolean isValid() {
		return !gridPane.getChildren().parallelStream()
				.filter(node->!(node instanceof Text))
				.filter(node->!(node instanceof HBox))
				.map(control->hasValue(control))
				.filter(b->!b)
				.findAny()
				.isPresent();
	}

	private Node[] getSequenceName() {
 
		Text text = new Text("Sequence Name:");
		TextField tfSequenceName = new TextField();
		tfSequenceName.textProperty().addListener(listener);
		tfSequenceName.setId(SEQUENCE_NAME);

		return new Node[] {text, tfSequenceName};
	}

	private Node[] getActionLine() throws IOException {

		ChoiceBox<Entry<Object, Object>> cbToolName = new ChoiceBox<>();
		cbToolName.setId(TOOL_NAME);
		cbToolName.setItems(getToolNames());
		cbToolName.setConverter(converter);

		ChoiceBox<Entry<Object, Object>> cbToolCommand = new ChoiceBox<>();
		cbToolName.setId("Tool Command");
		cbToolCommand.setConverter(converter);

		cbToolName.setOnAction(onToolNameAction(cbToolCommand));
		cbToolCommand.setOnAction(onToolCommandAction());

		return new Node[] {cbToolName, cbToolCommand};
	}

	private EventHandler<ActionEvent> onToolNameAction(ChoiceBox<Entry<Object, Object>> cbToolCommand) {
		return event->{
			event.consume();

			cbToolCommand.getItems().clear();

			final ChoiceBox<?> source = (ChoiceBox<?>) event.getSource();
			final Entry<?, ?> selectedItem = (Entry<?, ?>) source.getSelectionModel().getSelectedItem();
			final String fileName = CalibrationToolProcessor.getFileName((String) selectedItem.getKey());
			// Save Tool's class name
			cbToolCommand.setUserData(selectedItem.getValue());
			Optional.ofNullable(getClass().getResourceAsStream("/" + fileName))
			.ifPresent(resource->{
				Properties properties = new Properties();
				try {
					properties.load(resource);
					ObservableList<Entry<Object, Object>> items = FXCollections.observableArrayList(properties.entrySet());
					cbToolCommand.setItems(items);
				} catch (IOException e) {
					logger.catching(e);
				}
			});
		};
	}

	private EventHandler<ActionEvent> onToolCommandAction() {
		return event->{
			event.consume();

			final ChoiceBox<?> cbToolCommand = (ChoiceBox<?>) event.getSource();
			final GridPane gridPane = (GridPane) cbToolCommand.getParent();
			final ObservableList<Node> gridChildren = gridPane.getChildren();
			final Integer rowIndex = GridPane.getRowIndex(cbToolCommand);
			final Integer columnIndex = GridPane.getColumnIndex(cbToolCommand);

			removeRowEnd(rowIndex, columnIndex, gridChildren);
			addRowEnd(rowIndex, cbToolCommand);
		};
	}

	private void removeRowEnd(Integer rowIndex, Integer columnIndex, ObservableList<Node> gridChildren) {
		final List<Node> nodesToRemove = gridChildren.parallelStream()

				.filter(node->GridPane.getRowIndex(node)==rowIndex)
				.filter(node->GridPane.getColumnIndex(node)>columnIndex)
				.collect(Collectors.toList());

		gridChildren.removeAll(nodesToRemove);
	}

	private void addRowEnd(int rowIndex, ChoiceBox<?> cbToolCommand) {

		Optional.ofNullable(cbToolCommand.getSelectionModel().getSelectedItem())
		.map(si->(Map.Entry<?,?>)si)
		.map(entry->entry.getKey())
		.map(String.class::cast)
		.ifPresent(selectedToolCommand->{
			String className = (String) cbToolCommand.getUserData();
			Arrays.stream(tools).filter(tool->tool.getClass().getName().equals(className)).findAny()
			.map(Tool::getClass)
			.map(Class::getMethods)
			.flatMap(
					methods->
					Arrays.stream(methods)
					.filter(method->{
						final ToolAction annotation = method.getAnnotation(ToolAction.class);
						return annotation!=null && annotation.value().equals(selectedToolCommand);
					})
					.findAny())
			.ifPresent(
					method->{

						// Add new Node if needed
						final Class<?>[] parameterTypes = method.getParameterTypes();
						if(parameterTypes.length==0) {
							addButtons(rowIndex);
							return;
						}

						Arrays.stream(parameterTypes)
						.forEach(
								catchConsumerException(
										parameterType->
										Optional.ofNullable(getControl(parameterType))
										.ifPresent(
												control->
												gridPane.addRow(rowIndex, control))));
					});
		});
	}

	private ObservableList<Entry<Object, Object>> getToolNames() throws IOException{
		final Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/" + CalibrationToolProcessor.CALIBRATION_PROPERTIES));
		final Set<Entry<Object, Object>> entrySet = properties.entrySet();
		return FXCollections.observableArrayList(entrySet);
	}

	public static void setTools(Tool[] tools) {
		CalibrationSequenceDialog.tools = tools;
	}

	private Control getControl(Class<?> parameterType) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		if(parameterType.equals(String.class)) {
			final TextField textField = new TextField();
			textField.textProperty().addListener(getRowListener());
			return textField;
		}

		if(parameterType.isEnum()) {
			final ChoiceBox<Object> choiceBox = getChoiceBox(parameterType);
			choiceBox.getSelectionModel().selectedItemProperty().addListener(getRowListener());
			return choiceBox;
		}

		LogManager.getLogger().error(parameterType);
		return null;
	}

	private ChangeListener<Object> getRowListener() {
		return (o,ov,nv)->{
			try {

				int rowIndex = getRowIndex(o);

				addButtons(rowIndex);

			} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				logger.catching(e);
			}
		};
	}

	public void addButtons(int rowIndex) {
		removeRowButtons(rowIndex, gridPane);

		of(getRowNodeStream(rowIndex)
				.filter(node->!(node instanceof Text))
				.filter(node->!(node instanceof HBox))
				.filter(node->!hasValue(node)).findAny())
		.ifNotPresent(
				()->
				Platform.runLater(
						()->{

							final Button addButton = new Button("Add Row");
							addButton.setOnAction(onAddRow());

							final Button removeButton = new Button("Remove Row");
							removeButton.setOnAction(onRemoveRow());

							final HBox hBox = new HBox(addButton, removeButton);
							hBox.setId("buttons");
							gridPane.addRow(rowIndex, hBox);}));
	}

	private EventHandler<ActionEvent> onRemoveRow() {
		return event->{

			final HBox buttons = (HBox) ((Button) event.getSource()).getParent();
			int rowIndex = GridPane.getRowIndex(buttons);
			int rowCount = getLastRowIndex();

			if(rowCount==1) {
				CalibrationApp.showAlert("Remove Row", "This row can not be removed.", AlertType.INFORMATION);
				return;
			}

			final List<Node> nodesToRemove = getRowNodeStream(rowIndex).collect(Collectors.toList());
			gridPane.getChildren().removeAll(nodesToRemove);

			if(rowIndex!=rowCount){
				// Shift rows
				IntStream.range(++rowIndex, ++rowCount)
				.forEach(
						rIndex->{
							final int newRowIndex = rIndex - 1;
							getRowNodeStream(rIndex).forEach(rowNodes->GridPane.setRowIndex(rowNodes, newRowIndex));});
			}
		};
	}

	private EventHandler<ActionEvent> onAddRow() {
		return event->{

			final HBox buttons = (HBox) ((Button) event.getSource()).getParent();
			int rowIndex = GridPane.getRowIndex(buttons);
			addActionRow(rowIndex);
		};
	}

	private Node[] addActionRow() {
		int lastRowIndex = getLastRowIndex();
		return addActionRow(lastRowIndex);
	}

	public Node[] addActionRow(int rowIndex) {
		int rowCount = getLastRowIndex();

		if(rowIndex!=rowCount){
			// Shift rows
			long limit = rowCount-rowIndex;
			AtomicInteger counter = new AtomicInteger(rowCount);
			IntStream.generate(counter::getAndDecrement).limit(limit)
			.forEach(
					rIndex->{
						final int newRowIndex = rIndex + 1;
						getRowNodeStream(rIndex).forEach(rowNodes->GridPane.setRowIndex(rowNodes, newRowIndex));});
		}

		try {

			final Node[] actionLine = getActionLine();
			gridPane.addRow(++rowIndex, actionLine);

			return actionLine;

		} catch (IOException e) {
			logger.catching(e);
		}

		return null;
	}

	private void removeRowButtons(int row, GridPane gridPane) {
		getRowNodeStream(row)
		.filter(
				node->
				Optional.ofNullable(node.getId())
				.filter(
						id->
						id.equals("buttons"))
				.isPresent())
		.forEach(
				buttons->
				Platform.runLater(
						()->
						gridPane.getChildren().remove(buttons)));
	}

	private boolean hasValue(Node node) {

			final String simpleName = node.getClass().getSimpleName();
			switch(simpleName) {

			case "TextField":
				return !((TextField)node).getText().trim().isEmpty();

			case "ChoiceBox":
				return ((ChoiceBox<?>)node).getSelectionModel().getSelectedIndex()>=0;

			case "Button":
				return true;

			default:
				logger.error("Have to add '{}' case", simpleName);
				return false;
			}
	}

	private int getLastRowIndex() {
		return gridPane.getChildren().parallelStream()
				.mapToInt(GridPane::getRowIndex)
				.max()
				.orElse(-1);
	}

	public Stream<Node> getRowNodeStream(int row) {
		return gridPane.getChildren().parallelStream()
				.filter(c->GridPane.getRowIndex(c)==row);
	}

	private int getRowIndex(ObservableValue<? extends Object> observableValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{

		final Object bean = ((ReadOnlyProperty<?>)observableValue).getBean();
		final String simpleName = bean.getClass().getSimpleName();

		Node node;
		switch(simpleName) {

		case "ChoiceBoxSelectionModel":
			node = getChoiceBox((SingleSelectionModel<?>)bean);
			break;

		default:
			node = (Node) bean;
		}
		return GridPane.getRowIndex(node);
	}

	private ChoiceBox<?> getChoiceBox(SingleSelectionModel<?> bean) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final Field field = bean.getClass().getDeclaredField("choiceBox");
		field.setAccessible(true);
		return (ChoiceBox<?>) field.get(bean);
	}

	private ChoiceBox<Object> getChoiceBox(Class<?> parameterType) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		final Method method = parameterType.getMethod("values");
		final Object[] invoke = (Object[]) method.invoke(null);
		ObservableList<Object> items = FXCollections.observableArrayList(invoke);
		return new ChoiceBox<>(items);
	}

	public void initialize(Path path) throws IOException {
		// set Sequence Name
		String sequenceName = path.getFileName().toString().replace(".seq", "");
		getRowNodeStream(0).filter(TextField.class::isInstance).map(TextField.class::cast).findAny().ifPresent(tf->tf.setText(sequenceName));

		final List<String> lines = Files.readAllLines(path);
		// Fill First Row
		lines.stream()
		.forEach(
				line->{

					final String[] values = line.split(",");

					final Node[] nodes = addActionRow();
					select((ChoiceBox<?>)nodes[0], values[0]);
					select((ChoiceBox<?>)nodes[1], values[1]);

					final List<Node> rowNodes = getRowNodeStream(getLastRowIndex()).collect(Collectors.toList());
					if(rowNodes.size()!=values.length) {
						CalibrationApp.showAlert("Edit Sequence", "Something went wrong", AlertType.ERROR);
						logger.error("Something went wrong. {} : {}", (Object)values, rowNodes);
						return;
					}

					for(int i=2; i<values.length; i++)
						setValue(rowNodes.get(i), values[i]);
				});
	}

	private void setValue(Node node, String value) {
		final String simpleName = node.getClass().getSimpleName();
		switch(simpleName) {

		case "TextField":
			((TextField)node).setText(value);
			break;

		case "ChoiceBox":
			select((ChoiceBox<?>) node, value);
			break;

		default:
			logger.error("Something went wrong. {} : {}", node, value);
		}
	}

	public void select(final ChoiceBox<?> choiceBox, String name) {
		final ObservableList<?> items = choiceBox.getItems();

		if(items.size()==0) {
			logger.error("Something went wrong. {} : {}", choiceBox, name);
			return;
		}

		Stream<?> stream = items.stream();
		final Class<? extends Object> itemClass = items.get(0).getClass();
		Optional<?> oValue;
		if(itemClass.isEnum()) {
			
			try {

				final Method method = itemClass.getDeclaredMethod("valueOf", String.class);
				final Object enumValue = method.invoke(null, name);
				oValue = Optional.of(enumValue);

			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.catching(e);
				oValue = Optional.empty();
			}
		}else {

			oValue = stream
					.filter(item->((Map.Entry<?,?>)item).getKey().equals(name))
					.findAny();
		}

		oValue
		.map(Object.class::cast)
		.ifPresent(toSelect->{

			final int indexOf = items.indexOf(toSelect);
			SingleSelectionModel<?> selectionModel = choiceBox.getSelectionModel();
			selectionModel.select(indexOf);
		});
	}
}
