package irt.calibration.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;

import irt.calibration.backgroumd.anotations.CalibrationValue;

public class CalibrationWorker {

	public final static String DEFAULT_PATH = "Z:\\4Olex\\Calibration";

	@CalibrationValue("Yes #1")
	private static String test;

	public static List<String> getCalibrationTypes() throws IOException {
		LogManager.getLogger().error(test);
		Path path = Paths.get(DEFAULT_PATH);
		return Files.walk(path, 2).filter(Files::isDirectory).filter(p->!p.equals(path)).map(Path::getFileName).map(Object::toString).collect(Collectors.toList());
	}

	public static void creatNewCalibrationType(String newType) {
		Paths.get(DEFAULT_PATH, newType).toFile().mkdir();
	}
}
