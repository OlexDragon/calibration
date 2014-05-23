package irt.gui_callibration.workers;

public class NumberWorker {

	public static int stringToUnsignedInt(String text, int defaultResult){
		if(text!=null && !text.isEmpty()){
			if(!(text = text.replaceAll("\\D", "")).isEmpty()){
				defaultResult = Integer.parseInt(text);
			}
		}
		return defaultResult;
	}

	public static int stringToInt(String text, int defaultResult){
		if(text!=null && !text.isEmpty()){
			boolean negative = (text = text.trim()).charAt(0)=='-';
			if(!(text = text.replaceAll("\\D", "")).isEmpty()){
				defaultResult = Integer.parseInt(text);
				if(negative)
					defaultResult = -defaultResult;
			}
		}
		return defaultResult;
	}
}
