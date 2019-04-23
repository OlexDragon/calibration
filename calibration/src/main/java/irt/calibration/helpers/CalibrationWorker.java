package irt.calibration.helpers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.anotations.CalibrationTool;
import irt.calibration.anotations.ToolAction;
import irt.calibration.tools.Tool;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Window;

public class CalibrationWorker implements Runnable{
	private final static Logger logger = LogManager.getLogger();

	public final static String DEFAULT_PATH = "Z:\\4Olex\\Calibration";

	private static Tool[] tools;

	private TextArea taAnswers;

	private boolean run = true;

	private Path path;

	public CalibrationWorker(Path path, TextArea taAnswers) {
		this.taAnswers = taAnswers;
		ThreadWorker.runThread(this);
		this.path = path;
	}

	public void cansel() {
		run = false;
		Platform.runLater(()->taAnswers.appendText("\n\tCalibration cancelled."));
	}

	@Override
	public void run() {
		try {
 
			Files.lines(path)
			.filter(line->run)
			.forEach(line->{
				taAnswers.appendText(line);
				taAnswers.appendText("\n");
				final String[] split = line.split(",");

				logger.error(line);

				// Get Tool
				Arrays.stream(tools)
				.filter(
						tool->
						Optional.ofNullable(tool.getClass().getAnnotation(CalibrationTool.class))
						.map(CalibrationTool::value)
						.map(value->value.equals(split[0]))
						.orElse(false))
				.findAny()
				.ifPresent(tool->{
					// Get Method
					Arrays.stream(tool.getClass().getMethods())
					.filter(
							method->
							Optional.ofNullable(method.getAnnotation(ToolAction.class))
							.map(ToolAction::value)
							.map(value->value.equals(split[1]))
							.orElse(false))
					.findAny()
					.ifPresent(method->{
						try {
							String[] values = split.length>2 ? Arrays.copyOfRange(split, 2, split.length) : new String[0];
							final Parameter[] parameters = method.getParameters();

							if(values.length!=parameters.length) {
								logger.error("Something went wrong. {} : {}", (Object)values, parameters);
								return;
							}

							final Object[] args = IntStream.range(0, values.length).mapToObj(
									index->{
										try {
											return mapToObj(values[index], parameters[index]);
										} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
											logger.catching(e);
										}
										return null;
									}).toArray();

							method.invoke(tool, args);

						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							logger.catching(e);
						}
					});
					
				});
			});

		} catch (IOException e) {
			logger.catching(e);
		}
	}

	private Object mapToObj(String value, Parameter parameter) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		final Class<?> type = parameter.getType();

		if(type.isEnum()) {
			final Method method = type.getMethod("valueOf", String.class);
			return method.invoke(null, value);
		}

		return value;
	}

	public static List<Path> getCalibrationTypes() throws IOException {

		Path path = Paths.get(DEFAULT_PATH);
		return Files.walk(path, 2).filter(Files::isDirectory).filter(p->!p.equals(path)).sorted().collect(Collectors.toList());
	}

	public static void creatNewGroupName(String newGroupName) {
		Paths.get(DEFAULT_PATH, newGroupName).toFile().mkdir();
	}

	public static TextInputDialog getNewGroupDialog(Window window) {
		final TextInputDialog dialog = new TextInputDialog();
    	dialog.initOwner(window);
    	dialog.setTitle("Calibration Type");
    	dialog.setHeaderText("Enter the calibration group name in the text field.");
    	dialog.setContentText("Group Name:");
    	return dialog;
	}

	public static void setTools(Tool[] tools) {
		CalibrationWorker.tools = tools;
	}
}
