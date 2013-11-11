//@author: A0090941E
package typetodo.logic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import typetodo.exception.InvalidDateTimeException;

public class DateTimeParserTest {

	@Test
	public void testDateTime() throws Exception {

		assertEquals("deadline tomorrow with full date/time",
				"2013-11-12T04:00:00.000+08:00", getDates("tomorrow 4am"));
		assertEquals("deadline in the past with full date/time",
				"2013-11-08T13:00:00.000+08:00", getDates("3 days ago 1pm"));
		assertEquals("deadline in the future with full date/time",
				"2013-11-22T04:00:00.000+08:00", getDates("next Friday 4am"));
		assertEquals("deadline with only time",
				"2013-11-11T04:00:00.000+08:00", getDates("4am"));
		assertEquals("deadline with day abbreviation",
				"2013-11-12T01:00:00.000+08:00", getDates("tue 1am"));
		assertEquals("deadline with month abbreviation",
				"2013-12-08T04:00:00.000+08:00", getDates("8th dec 4am"));
		assertEquals("deadline with abbreviation tmr",
				"2013-11-12T04:00:00.000+08:00", getDates("tmr 4am"));
		assertEquals("deadline with standard datetime mm/dd",
				"2014-01-13T04:00:00.000+08:00", getDates("1/13/2014 4am"));

		/**
		 * deadline with only date will give current time with respective given
		 * date. but JUnit is hard to test since the operation to get system
		 * time will lag thus not exactly equal.
		 */

		assertEquals("timed with full date/time start and end",
				"2013-11-15T01:00:00.000+08:00|2013-11-15T02:00:00.000+08:00",
				getDates("from friday 1am to friday 2am"));
		assertEquals("timed with no from",
				"2013-11-15T01:00:00.000+08:00|2013-11-15T02:00:00.000+08:00",
				getDates("friday 1am to friday 2am"));
		assertEquals("timed with -",
				"2013-11-15T01:00:00.000+08:00|2013-11-15T02:00:00.000+08:00",
				getDates("friday 1am-friday 2am"));
		assertEquals("timed with no am/pm 1",
				"2013-11-15T01:00:00.000+08:00|2013-11-15T02:00:00.000+08:00",
				getDates("friday 1-2"));
		assertEquals("timed with no am/pm 2",
				"2013-11-15T13:00:00.000+08:00|2013-11-15T14:00:00.000+08:00",
				getDates("friday 13-14"));
		assertEquals("timed with no am/pm 3",
				"2013-11-15T13:00:00.000+08:00|2013-11-15T14:00:00.000+08:00",
				getDates("friday 1-2pm"));
	}

	@Test(expected = InvalidDateTimeException.class)
	public void testDateTimeException() throws Exception {
		getDates("this is a test string.");
		getDates("1-2 friday");
	}

	private String getDates(String dateInput) throws Exception {
		String dateField;
		String result = "";
		dateField = modifyDate(dateInput);

		List<java.util.Date> javaDates = new PrettyTimeParser()
				.parse(dateField);
		ArrayList<DateTime> jodaDates = new ArrayList<DateTime>();

		while (!javaDates.isEmpty()) {
			DateTime validDates = new DateTime(javaDates.remove(0));
			jodaDates.add(validDates);
		}

		if (dateField.contains(" to ") && jodaDates.size() == 1) {
			throw new InvalidDateTimeException(
					"Please specify both date and time in all fields. Please use 'mm/dd' format if you want to type standard date.");
		} else if (jodaDates.size() == 0 && containsNumeric(dateField)) {
			throw new InvalidDateTimeException(
					"Please specify both date and time in all fields. Please use 'mm/dd' format if you want to type standard date.");
		} else {
			if (jodaDates.size() == 1) {
				result = jodaDates.get(0).toString();
			} else if (jodaDates.size() == 2) {
				result = jodaDates.get(0).toString() + "|"
						+ jodaDates.get(1).toString();
			} else {
				assert jodaDates.size() != 1 || jodaDates.size() != 2;
				throw new InvalidDateTimeException(
						"Please specify both date and time in all fields. Please use 'dd/mm' format if you want to type standard date.");
			}
		}

		return result;
	}

	private String modifyDate(String dateInput) {
		String result, startAmPm, endAmPm;
		result = dateInput.toLowerCase().replaceAll("-", " to ");
		result = result.toLowerCase().replaceAll("tmr", "tomorrow");

		int indexOfTo = result.indexOf(" to ");
		if (indexOfTo != -1) {
			endAmPm = result.substring(result.trim().length() - 2)
					.toLowerCase().trim();
			startAmPm = result.substring(indexOfTo - 2, indexOfTo).trim()
					.toLowerCase();

			if (endAmPm.equals("pm")
					&& (!startAmPm.equals("am") || !startAmPm.equals("pm"))) {
				startAmPm = startAmPm + "pm";
			}
			result = result.substring(0, indexOfTo - 2) + " " + startAmPm
					+ result.substring(indexOfTo);
		}

		return result;
	}

	private boolean containsNumeric(String userInput) {
		boolean doesContain = false;
		int len = userInput.length();
		for (int i = 0; i < len; i++) {
			if (Character.isDigit(userInput.charAt(i))) {
				doesContain = true;
			}
		}
		return doesContain;
	}
}
