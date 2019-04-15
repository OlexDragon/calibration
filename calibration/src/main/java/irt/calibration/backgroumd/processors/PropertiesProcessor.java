package irt.calibration.backgroumd.processors;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes("irt.calibration.backgroumd.anotations.CalibrationValue")
public class PropertiesProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> arg0, RoundEnvironment roundEnvironment) {
		processingEnv.getMessager().printMessage(Kind.ERROR, "Yeeee");
		return true;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}
}
