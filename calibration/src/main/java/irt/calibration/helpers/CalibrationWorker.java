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
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import irt.calibration.anotations.CalibrationTool;
import irt.calibration.anotations.ToolAction;
import irt.calibration.tools.Tool;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Window;

public class CalibrationWorker extends FutureTask<Void>{
	private final static Logger logger = LogManager.getLogger();

	public final static String DEFAULT_PATH = "Z:\\4Olex\\Calibration";

	private static Tool[] tools;
	private final static Observable OBSERVABLE = new Observable() {

		@Override
		public void notifyObservers(Object arg) {
			setChanged();
			super.notifyObservers(arg);
		}};

	private static boolean run = true;
	private static Path path;

	private static Callable<Void> callable = ()->{
		try {
 
			Files.lines(path)
			.filter(line->run)
			.forEach(line->{
				notifyObserver(line + '\n');
				final String[] split = line.split(",");

				logger.error(line);

				// Get Tool
				Arrays.stream(tools)
				.filter(
						tool->
						Optional.ofNullable(tool.getClass().getAnnotation(CalibrationTool.class))
						.map(CalibrationTool::value)
						.map(
								annotationValue->{
									String toolDescription = split[0];
									logger.debug("'{}' : '{}'", annotationValue, toolDescription);
									return annotationValue.equals(toolDescription);
								})
						.orElse(false))
				.findAny()
				.ifPresent(tool->{
					// Get the Method
					Arrays.stream(tool.getClass().getMethods())
					.filter(
							method->
							Optional.ofNullable(method.getAnnotation(ToolAction.class))
							.map(ToolAction::value)
							.map(
									annotationValue->{
										String methodDescription = split[1];
										logger.debug("'{}' : '{}'", annotationValue, methodDescription);
										return annotationValue.equals(methodDescription);
									})
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

							Optional.ofNullable(method.invoke(tool, args))
							.ifPresent(result->notifyObserver(" - " + result));;

						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							logger.catching(e);
							notifyObserver("\n Calibration cancelled. Error message: " + getErrorMessages(e));
						}
					});
					
				});
			});

			logger.error("\nEnd of Calibration");

		} catch (IOException e) {
			logger.catching(e);
		}
	
		return null;
	};

	public CalibrationWorker(Path path) {
		super(callable);
		CalibrationWorker.path = path;
		ThreadWorker.runThread(this);
	}

	private static void notifyObserver(String string) {
		OBSERVABLE.notifyObservers(string);
	}

	public void cansel() {
		run = false;
		Arrays.stream(tools).forEach(Tool::cansel);
		notifyObserver("\n\tCalibration cancelled.");
	}

	private static String getErrorMessages(Exception e) {
		StringBuffer sb = new StringBuffer();
		Throwable cause = e;
		while(cause!=null) {
			Optional.ofNullable(cause.getLocalizedMessage())
			.ifPresent(message->{
				sb.append("\n\t").append(message);
			});
			cause = cause.getCause();
		}
		
		return sb.toString();
	}

	private static Object mapToObj(String value, Parameter parameter) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

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

	public void addObserver(Observer observer) {
		OBSERVABLE.addObserver(observer);
	}
}
