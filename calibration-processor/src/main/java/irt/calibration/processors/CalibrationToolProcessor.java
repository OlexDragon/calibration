package irt.calibration.processors;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.google.auto.service.AutoService;

import irt.calibration.anotations.CalibrationTool;

@SupportedAnnotationTypes("irt.calibration.anotations.CalibrationTool")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CalibrationToolProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		annotations.parallelStream()

				.map(roundEnv::getElementsAnnotatedWith)
				.map(annotatedElements->(Set<? extends Element>)annotatedElements)
				.forEach(
						annotatedElements->{
							List<Element> annotatedClasses = annotatedElements.stream().map(Element.class::cast)

									.collect(
											Collectors.toList());

							Map<String, String> setterMap = annotatedClasses.stream()
									.collect(
											Collectors.toMap(

													element -> element.getAnnotation(CalibrationTool.class).value(),
													element -> processingEnv.getElementUtils().getPackageOf(element) + "." + element.getSimpleName()));

							try {

								final Properties properties = new Properties();
								properties.putAll(setterMap);

								final FileObject resource = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", "calibration.properties");
							    try (OutputStream outputStream = resource.openOutputStream()) {
							    	properties.store(outputStream, "Classes annotated with " + CalibrationTool.class.getSimpleName() + " annotation");
							    }

							} catch (IOException e) {
								e.printStackTrace();
							}
						});

		return true;
	}
}
