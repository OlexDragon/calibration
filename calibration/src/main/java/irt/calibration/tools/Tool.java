package irt.calibration.tools;

import java.util.Observer;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

public interface Tool {

	void cansel();
//	void setAddress();

	static Preferences prefs = Preferences.userNodeForPackage(Tool.class);

	public static void initializeTextField(TextField textField, String key, int defaultValue, Consumer<Integer> consumer) {

		final int value = prefs.getInt(key, defaultValue);

		consumer.accept(value);
		textField.setText(Integer.toString(value));

		textField.focusedProperty()
		.addListener(saveTextFieldValue(key, consumer));
	}

	public static ChangeListener<? super Boolean> saveTextFieldValue(final String key, Consumer<Integer> consumer) {
		return (o, ov, nv)->

		Optional.of(nv)
		.filter(v->!v)
		.map(v->((TextInputControl)((ReadOnlyBooleanProperty)o).getBean()).getText().replaceAll("\\D", ""))
		.filter(v->!v.isEmpty())
		.map(v->Integer.parseInt(v))
		.ifPresent(
				v->{
					consumer.accept(v);
					prefs.putInt(key, v);
				});
	}

	void addObserver(Observer o);
}
