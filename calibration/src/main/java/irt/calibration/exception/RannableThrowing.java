package irt.calibration.exception;

@FunctionalInterface
public interface RannableThrowing<E extends Exception> {

	void run() throws E;
}
