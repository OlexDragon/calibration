package irt.calibration.exception;

@FunctionalInterface
public interface ConsumerThrowing<T, E extends Exception> {

	void accept(T t) throws E;
}
