package irt.calibration.beans;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Average{
//	private final static Logger logger = LogManager.getLogger();

	private final List<Double> list = new ArrayList<>();

	public void addValue(Number value) {

		final double doubleValue = value.doubleValue();

		if(doubleValue==Double.NaN)
			return;

		list.add(doubleValue);
	}

	public double getAverageValue() {
		return getAverage(list);
	}

	public int getCount() {
		return list.size();
	}

	private double getAverage(List<Double> list) {

		Boolean filterTopPeaks = null;
		do {
			DoubleSummaryStatistics summary = list.parallelStream().collect(Collectors.summarizingDouble(Number::doubleValue));
			double average = summary.getAverage(); double min = summary.getMin(); double max = summary.getMax();
			double middle = (max-min)/2+min; long difference = Math.round(middle - average);

			if(difference==0)
				return average;

			boolean topPeaks = difference>0;
			if(filterTopPeaks == null)
				filterTopPeaks = topPeaks;

			if(topPeaks!=filterTopPeaks)
				return average;

			Predicate<Double> predicate;
			double target;
			if(topPeaks) {
				target = 2*average - min;
				predicate = topPeaks ? d->d.doubleValue()<=target : d->d.doubleValue()>=target;
			}else {
				target = 2*average - max;
				predicate = topPeaks ? d->d.doubleValue()<=target : d->d.doubleValue()>=target;
			}

			list = list.parallelStream().filter(predicate).collect(Collectors.toList());

		}while(true);
	}

	@Override
	public String toString() {
		return "AverageValue [count=" +getCount() + ", list=" + list + "]";
	}
}
