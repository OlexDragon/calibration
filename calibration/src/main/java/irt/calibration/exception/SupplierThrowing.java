package irt.calibration.exception;

@FunctionalInterface
public interface SupplierThrowing<T, E extends Exception> {

	T get() throws E;
}
