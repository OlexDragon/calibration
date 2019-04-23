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
import irt.calibration.anotations.ToolAction;

@SupportedAnnotationTypes("irt.calibration.anotations.CalibrationTool")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CalibrationToolProcessor extends AbstractProcessor {

	public static final String CALIBRATION_PROPERTIES = "calibration.properties";

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		annotations.parallelStream()

				.map(roundEnv::getElementsAnnotatedWith)
				.map(annotatedElements->(Set<? extends Element>)annotatedElements)
				.forEach(
						annotatedElements->{

							List<Element> annotatedClasses = annotatedElements.stream().map(Element.class::cast).collect(Collectors.toList());

							Map<String, String> classesMap = annotatedClasses.stream()
									.collect(
											Collectors.toMap(

													element -> element.getAnnotation(CalibrationTool.class).value(),
													element -> processingEnv.getElementUtils().getPackageOf(element) + "." + element.getSimpleName()));

							if(classesMap.size()==0)
								return;

							// Save properties file with tool mane and class name
							try {

								final Properties properties = new Properties();
								properties.putAll(classesMap);

								final FileObject resource = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", CALIBRATION_PROPERTIES);
							    try (OutputStream outputStream = resource.openOutputStream()) {
							    	properties.store(outputStream, "Classes annotated with " + CalibrationTool.class.getSimpleName() + " annotation");
							    }

							} catch (IOException e) {
								e.printStackTrace();
							}

							annotatedClasses.parallelStream()
							.forEach(
									element->{
										final String annotationValue = element.getAnnotation(CalibrationTool.class).value();
										String fileName = getFileName(annotationValue);

										Map<String, String> methodsMap = element.getEnclosedElements()

												.stream()
												.filter(enclosedElement->(enclosedElement.getAnnotation(ToolAction.class)!=null))
												.collect(
														Collectors.toMap(

																enclosedElement -> enclosedElement.getAnnotation(ToolAction.class).value(),
																enclosedElement -> enclosedElement.getSimpleName().toString()));

										if(methodsMap.size()==0)
											return;

										// Save properties file with class methods
										try {

											final Properties properties = new Properties();
											properties.putAll(methodsMap);

											final FileObject resource = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", fileName);
										    try (OutputStream outputStream = resource.openOutputStream()) {
										    	properties.store(outputStream, "The Methods with " + ToolAction.class.getSimpleName() + " annotation");
										    }

										} catch (IOException e) {
											e.printStackTrace();
										}
									});
						});

		return true;
	}

	public static String getFileName(final String annotationValue) {
		return annotationValue.replaceAll(" ", "") + ".properties";
	}
}
