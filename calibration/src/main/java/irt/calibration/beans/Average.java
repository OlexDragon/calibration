package irt.calibration.beans;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Average{
//	private final static Logger logger = LogManager.getLogger();

	private final List<Double> list;
	private final int listinitialCapacity;
	private final int precision;

	/**
	 * @param initialCapacity - value buffer size
	 * @param precision - 1 is the minimum value. All values less than 1 will be changed to 1
	 */
	public Average(int initialCapacity, int precision) {
		list = new ArrayList<>(initialCapacity);
		this.listinitialCapacity = initialCapacity;
		this.precision = Optional.of(precision).filter(p->p>0).orElse(1);
	}

	public void addValue(Number value) {

		if(isFilled())
			list.remove(0);

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
			DoubleSummaryStatistics summary = list.parallelStream().collect(Collectors.summarizingDouble(Double::doubleValue));
			double average = summary.getAverage(); double min = summary.getMin(); double max = summary.getMax();
			double middle = (max+min)/2;// Find middle point
			double difference = middle - average;

			if(Math.abs(difference) <= precision)
				return average;

			boolean topPeaks = difference>0;
			if(filterTopPeaks == null)
				filterTopPeaks = topPeaks;

			if(topPeaks!=filterTopPeaks)
				return average;

			Predicate<Double> predicate;
			if(topPeaks) {
				final double target = 2*average - min;
				predicate = topPeaks ? d->d.doubleValue()<=target : d->d.doubleValue()>=target;
			}else {
				final double target = 2*average - max;
				predicate = topPeaks ? d->d.doubleValue()<=target : d->d.doubleValue()>=target;
			}

			list = list.parallelStream().filter(predicate).collect(Collectors.toList());

		}while(true);
	}

	@Override
	public String toString() {
		return "AverageValue [count=" +getCount() + ", list=" + list + "]";
	}

	public boolean isFilled() {
		return list.size()>=listinitialCapacity;
	}
}
