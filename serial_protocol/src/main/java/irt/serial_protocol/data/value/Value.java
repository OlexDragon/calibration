package irt.serial_protocol.data.value;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Value extends Observable{

	protected final Logger logger = (Logger) LogManager.getLogger(getClass().getName());

	public enum Status{
				IN_RANGE,
				UNDER_RANGE,
				OVER_RANGE,
				RANGE_SET,
				NUMBER_FORMAT_EXEPTION
	}
	private int type = 0;

	protected long oldValue;
	protected long value;

	private long minValue;
	private long maxValue;
	protected int factor;
	protected String prefix;
	private boolean doStringFormat;

	private boolean error;

	private Status status;

	protected Value(){}

	public Value(long value, long minValue, long maxValue, int precision){
		setFactor(precision);
		setMinMax(minValue, maxValue);
		setValue(value);
		setPrefix();
	}

	public Value(long value, double minValue, double maxValue, int precision){
		setFactor(precision);
		setMinMax(Math.round(minValue*factor), Math.round(maxValue*factor));
		setValue(value);
		setPrefix();
	}

	public Value(String value, String minValue, String maxValue, int precision) {
		logger.entry(value, minValue, maxValue, precision);
		setFactor(precision);
		setMinMax(parse(minValue), parse(maxValue));
		setValue(value!=null ? parse(value) : 0);
		setPrefix();
	}

	public Value(Value value) {
		setMinMax(value.getMinValue(), value.getMaxValue());
		setValue(value.getValue());
		factor = value.getFactor();
		prefix = value.getPrefix();
	}

	private void setFactor(int precision) {
		logger.entry(precision);
		logger.exit(factor = (int) Math.pow(10, precision));
	}

	public void setMinMax(long minValue, long maxValue) {
		logger.entry(value, minValue, maxValue);
		setChanged();
		if (minValue < maxValue) {
			this.minValue = minValue;
			this.maxValue = maxValue;
		} else {
			this.minValue = maxValue;
			this.maxValue = minValue;
		}
		notifyObservers(Status.RANGE_SET);

		setValue(value);
		logger.trace("value={}, minValue={}, maxValue={}", value, minValue, maxValue);
	}

	public void setMinMax(String minValue, String maxValue) {
		setMinMax(parse(minValue), parse(maxValue));
	}

	public long getMinValue() {
		return minValue;
	}

	public int getRelativeMinValue() {
		return 0;
	}

	protected void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	protected void setMinValue(String minValue) {
		long min = parse(minValue);
		setMinValue(min);
	}

	public long getMaxValue() {
		return maxValue;
	}

	public long getRelativeMaxValue() {
		return maxValue-minValue;
	}

	protected void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	protected void setMaxValue(String maxValue) {
		long max = parse(maxValue);
		setMaxValue(max);
	}

	public void setValue(long value) {
		logger.entry(value, minValue, maxValue);

		if (this.value != value || value<minValue || value>maxValue) {
			setChanged();

			if (value > maxValue) {
				oldValue = value;
				this.value = maxValue;
				error = true;
				status = Status.OVER_RANGE;
			} else if (value < minValue) {
				oldValue = value;
				error = true;
				this.value = minValue;
				status = Status.UNDER_RANGE;
			} else {
				oldValue = this.value;
				error = false;
				this.value = value;
				status = Status.IN_RANGE;
			}
		}
		logger.exit(this.value);
	}

	public Value setValue(String text) {
		setValue(parse(text));
		return this;
	}

	public void setValue(double value) {
		setValue((long)(value*factor));
	}

	public long getValue() {
		return value;
	}

	public long getValue(int relativeValue) {
		return relativeValue + minValue;
	}

	/**
	 * @return value - minValue
	 */
	public int getRelativeValue() {
		return (int) (value - minValue);
	}

	public void setRelativeValue(int relValue) {
		setValue(relValue + minValue);
	}

	public long parse(String text) {
		long value = 0;
		if(text==null || text.trim().isEmpty()){
			error = true;
		}else{
			text = text.toUpperCase().replaceAll("[^\\d.E-]", "");

			if(!text.isEmpty() && Character.isDigit(text.charAt(text.length()-1)))
				try {
					value = Math.round(Double.parseDouble(text)*factor);
					error = false;
				} catch (NumberFormatException e) {
					error = true;
					status = Status.NUMBER_FORMAT_EXEPTION;
					logger.catching(e);
				}
			else
				error = true;

		}
		return value;
	}

	public int getFactor() {
		return factor;
	}

	protected NumberFormat getInstance() {
		return NumberFormat.getIntegerInstance();
	}

	protected void setMinValue(long minValue) {
		this.minValue = minValue;
	}

	protected void setMaxValue(long maxValue) {
		this.maxValue = maxValue;
	}

	public String getPrefix() {
		return prefix;
	}

	public Value setPrefix(String prefix) {
		this.prefix = prefix!= null ? prefix : "";
		return this;
	}

	public void setPrefix() {
		this.prefix = "";
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Value getCopy() {
		Value value;
		switch(getClass().getSimpleName()){
		case "ValueFrequency":
			value = new ValueFrequency(this);
			break;
		case "ValueDouble":
			value = new ValueDouble(this);
			break;
		case "Value":
			value = new ValueDouble(this);
			break;
		default:
			value = new Value(this);
		}
		return value;
	}

	public long getOldValue() {
		return oldValue;
	}

	public Value add(long value) {
		setValue(value+this.value);
		return this;
	}

	public void subtract(long value) {
		setValue(this.value-value);
	}

	public boolean hasChanged() {
		return oldValue!=value;
	}

	public String getExponentialValue() {
		DecimalFormat df = new DecimalFormat("0.00000000000000E000");  
		return df.format((double)value/factor);
	}

	public boolean isError() {
		return error;
	}

	public boolean isDoToStringFofmat() {
		return doStringFormat;
	}

	public void setDoStringFormat(boolean doStringFormat) {
		this.doStringFormat = doStringFormat;
	}

	@Override
	public boolean equals(Object obj) {
		return obj!=null ? obj.hashCode()==hashCode() : false;
	}

	@Override
	public int hashCode() {
		return new Long(value).hashCode();
	}

	public String toString(long value) {
		NumberFormat numberFormat = getInstance();
		double result = (double)value/factor;
		return (doStringFormat ? numberFormat.format(result) : result)+prefix;
	}

	@Override
	public String toString() {
		return toString(value);
	}

	public Status getStatus() {
		return status;
	}
}
