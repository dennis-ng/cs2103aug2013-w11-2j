package typetodo.logic;

import java.util.Comparator;

import org.joda.time.LocalDate;

public class LocalDateComparator implements Comparator<LocalDate> {

	@Override
	public int compare(LocalDate firstDate, LocalDate secondDate) {
		// TODO Auto-generated method stub
		return (firstDate.isBefore(secondDate) ? -1: (firstDate.isAfter(secondDate) ? 1:0)) ;
	}
}
