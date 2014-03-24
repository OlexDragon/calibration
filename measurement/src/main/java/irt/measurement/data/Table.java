package irt.measurement.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class Table {

	private final Logger logger = (Logger) LogManager.getLogger();

	private Map<Double, Double> mapTable = new TreeMap<>();

	private String lutSizeName;
	private String lutValueName;

	private double accuracy;

	public Double add(double key, double value){
		return mapTable.put(key, value);
	}

	public String getLutSizeName() {
		return lutSizeName;
	}

	public String getLutValueName() {
		return lutValueName;
	}

	public void setLutSizeName(String lutSizeName) {
		this.lutSizeName = lutSizeName;
	}

	public void setLutValueName(String lutValueName) {
		this.lutValueName = lutValueName;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	private Map<Double, Double> toAverage() {
		Map<Double, Double> mapTableToShow = new TreeMap<>();
		int size = mapTable.size();

		if(accuracy>0 && size>2){

			Iterator<Double> keys = mapTable.keySet().iterator();
			Queue<Double> factors = new LinkedList<>();

			Double key 		= null;
			Double value 	= null;
			Double oldKey 	= null;
			Double oldValue = null;
			do{
				oldKey = key;
				oldValue = value;

				key = keys.next();
				value = mapTable.get(key);

				if(oldKey!=null)
					factors.add((value-oldValue)/(key-oldKey));

				if(mapTableToShow.isEmpty())
					mapTableToShow.put(key, value);
				else{
					if(isLessOrEquals(mapTableToShow, key, value))

						if(mapTableToShow.size()<=3){
							factors.clear();
							mapTableToShow.clear();
							mapTableToShow.put(key, value);
						}else
							break;

					if(factors.size()>=2 && inRange(factors))
						mapTableToShow.remove(oldKey);

					mapTableToShow.put(key, value);
				}

			}while(keys.hasNext());

		}else
			mapTableToShow = mapTable;

		return mapTableToShow;
	}

	private boolean isLessOrEquals(Map<Double, Double> mapTable, Double key, Double value) {
		logger.entry(mapTable, key, value);
		boolean isLess = false;

		Iterator<Double> iterator = mapTable.keySet().iterator();
		while(iterator.hasNext()){
			Double next = iterator.next();
			if(Double.compare(key, next)<=0 || Double.compare(value, mapTable.get(next))<=0){
				isLess = true;
				break;
			}
		}
		return logger.exit(isLess);
		
	}

	private boolean inRange(Queue<Double> factors) {

		// (100% - factor2/(factor1/100%)) >= 0
		Double poll = factors.poll();
		Double peek = factors.peek();
		double abs = Math.abs(100 - peek*100/poll);
		logger.trace("factors={}, abs={}, accuracy={}, poll={}, peek={}", factors, abs, accuracy, poll, peek);

		return logger.exit(Double.compare(abs, accuracy)<=0);
	}

	@Override
	public String toString() {

		Map<Double, Double> mapTableToShow = toAverage();
		String table = (lutSizeName!=null ? lutSizeName+"\t" : "")+mapTableToShow.size()+"\n";

		for(Double l:mapTableToShow.keySet())
			table += (lutValueName!=null ? lutValueName+"\t" : "")+Math.round(l)+"\t"+mapTableToShow.get(l)+"\n";

		return table;
	}

	public void clear() {
		mapTable.clear();
	}
}
