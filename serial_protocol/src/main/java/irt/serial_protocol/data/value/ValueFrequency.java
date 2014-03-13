package irt.serial_protocol.data.value;


import java.text.NumberFormat;

public class ValueFrequency extends Value {

	private static final int DEVICE_TYPE_70_TO_L = 1;
	private static final int DEVICE_TYPE_L_TO_KU = 2;

	public ValueFrequency(String value, String minValue, String maxValue) {
		super(value, minValue, maxValue, 0);
	}

	public ValueFrequency(String value, int converterType) {
		setMinMax(converterType);
		setValue(value);
	}

	public ValueFrequency(long value, long minValue, long maxValue) {
		super(value, minValue, maxValue, 0);
	}

	public ValueFrequency(Value value) {
		super(value);
	}

	private void setMinMax(int converterType) {
		super.setType(converterType);

		switch(converterType){
		case DEVICE_TYPE_70_TO_L:
			setMinValue("950 MHz");
			setMaxValue("2.15 GHz");
			setPrefix(" MHz");
			break;
		case DEVICE_TYPE_L_TO_KU:
			setMinValue("12.8 GHz");
			setMaxValue("13.05 GHz");
			setPrefix(" GHz");
		}
		
	}

	@Override
	public long parseLong(String text) {
		logger.entry(text);

		int multiplier = 1;
		text = text.toUpperCase();
		String str = text.replaceAll("[\\d., +]", "");

		if (!str.isEmpty())
			switch(str.charAt(0)){
			case 'K':
				multiplier = 1000;
				break;
			case 'M':
				multiplier = 1000000;
				break;
			case 'G':
				multiplier = 1000000000;
				break;
			case 'E':
				String[] split = text.split("E");
				text = split[0];
				str = split[1].replaceAll("[^\\d.-]", "");
				logger.trace("multiplier={}", multiplier = (int) Math.round(Math.pow(10, Integer.parseInt(str))));
			}

		logger.trace("value={}", text = text.replaceAll("[^\\d.-]", ""));

		return logger.exit(text.isEmpty() ? 0 : Math.round(Double.parseDouble(text)*multiplier));
	}

	@Override
	public String toString(long value) {
		logger.entry(value);
		int prefixIndex = 0;
		
		while(true){
			if(value%1000 > 0 || prefixIndex >= 3)
				break;
			value /= 1000;
			prefixIndex++;
		}

		switch(prefixIndex){
		case 0:
			setPrefix(" Hz");
			break;
		case 1:
			setPrefix(" KHz");
			break;
		case 2:
			setPrefix(" MHz");
			break;
		default:
			setPrefix(" GHz");
		
		}

		NumberFormat numberFormat = getInstance();
		return logger.exit((isDoToStringFofrat() ? numberFormat.format(value) : value)+getPrefix());
	}

	@Override
	public void setType(int type) {
		if(getType() != type)
			setMinMax(type);
	}

}
