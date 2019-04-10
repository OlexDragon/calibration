package irt.calibration.tools;

import java.util.Optional;

import irt.calibration.tools.furnace.data.CommandParameter;
import static irt.calibration.exception.ExceptionWrapper.catchFunctionException;

public interface CommandWithParameter extends ToolCommand {

	Optional<CommandParameter[]> getParameterValues();

	public static Optional<CommandParameter[]> getValuesOf(Class<? extends CommandParameter> parameterClass){

		return Optional.ofNullable(parameterClass)
				.map(
						catchFunctionException(
								clazz->clazz.getMethod("values")))
				.map(
						catchFunctionException(
								method->(CommandParameter[]) method.invoke(null)));
	}
}
