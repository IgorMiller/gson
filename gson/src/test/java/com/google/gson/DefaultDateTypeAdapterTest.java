/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * A simple unit test for the {@link DefaultDateTypeAdapter} class.
 *
 * @author Joel Leitch
 */
public class DefaultDateTypeAdapterTest extends TestCase {

  public void testFormattingInEnUs() {
    assertFormattingAlwaysEmitsUsLocale(Locale.US);
  }

  public void testFormattingInFr() {
    assertFormattingAlwaysEmitsUsLocale(Locale.FRANCE);
  }

  private void assertFormattingAlwaysEmitsUsLocale(Locale locale) {
    TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(locale);
    try {
      assertFormatted("Jan 1, 1970 12:00:00 AM", new DefaultDateTypeAdapter());
      assertFormatted("1/1/70", new DefaultDateTypeAdapter(DateFormat.SHORT));
      assertFormatted("Jan 1, 1970", new DefaultDateTypeAdapter(DateFormat.MEDIUM));
      assertFormatted("January 1, 1970", new DefaultDateTypeAdapter(DateFormat.LONG));
      assertFormatted("1/1/70 12:00 AM",
          new DefaultDateTypeAdapter(DateFormat.SHORT, DateFormat.SHORT));
      assertFormatted("Jan 1, 1970 12:00:00 AM",
          new DefaultDateTypeAdapter(DateFormat.MEDIUM, DateFormat.MEDIUM));
      assertFormatted("January 1, 1970 12:00:00 AM UTC",
          new DefaultDateTypeAdapter(DateFormat.LONG, DateFormat.LONG));
      assertFormatted("Thursday, January 1, 1970 12:00:00 AM UTC",
          new DefaultDateTypeAdapter(DateFormat.FULL, DateFormat.FULL));
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }
  
  public void testOutputFormattedWithCustomDateFormatType()
  {
  	TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  	DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(dateFormat, DateFormatType.CUSTOM);
    try {
    	assertFormatted("1970-01-01", dateTypeAdapter);
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }
  
  public void testDefaultOutputFormatterIsEnUs()
  {
  	TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  	DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(dateFormat, DateFormatType.DEFAULT);
    try {
    	assertFormatted("Jan 1, 1970 12:00:00 AM", dateTypeAdapter);
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }
  
  public void testUnixDateFormatParsedAndFormatted()
  {
  	DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(DateFormatType.UNIX);
  	assertParsed("0", dateTypeAdapter);
    assertFormatted("0", dateTypeAdapter);
  }
  
  public void testUnixDateFormatParsedAndFormattedAnotherDate()
  {
  	DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(DateFormatType.UNIX);
    Long someMillis = 1448603783413L;
    String someSecondsStr = "1448603783";
    assertEquals(someSecondsStr, adapter.serialize(new Date(someMillis), Date.class, null).getAsString());
    assertEquals(someSecondsStr,
    		new Date(Long.parseLong(someSecondsStr) * 1000),
    		adapter.deserialize(new JsonPrimitive(someSecondsStr), Date.class, null));
  }
  
  public void testMillisecondsDateFormatParsedAndFormatted()
  {
  	DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(DateFormatType.MILLIS);
    Long someMillis = 1448603783413L;
    String someSecondsStr = "1448603783413";
    assertEquals(someSecondsStr, adapter.serialize(new Date(someMillis), Date.class, null).getAsString());
    assertEquals(someSecondsStr,
    		new Date(Long.parseLong(someSecondsStr)),
    		adapter.deserialize(new JsonPrimitive(someSecondsStr), Date.class, null));
  }
  
  public void testMillisecondsDateFormatParsedWithDefaultConfig()
  {
		TimeZone defaultTimeZone = TimeZone.getDefault();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		try {
			DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter();
			Long someMillis = 1448603783413L; // Fri Nov 27 2015 05:56:23 in UTC
			String someSecondsStr = "1448603783413";
			// assert formatted US
			assertEquals("Nov 27, 2015 5:56:23 AM", adapter.serialize(new Date(someMillis), Date.class, null).getAsString());
			// assert parsed millis
			assertEquals(someSecondsStr, new Date(Long.parseLong(someSecondsStr)),
					adapter.deserialize(new JsonPrimitive(someSecondsStr), Date.class, null));
		} finally {
			TimeZone.setDefault(defaultTimeZone);
		}
  }
  
  public void testOutputNotFormattedWithCustomDateFormatAndDefaultOutputType()
  {
  	TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
  	DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(dateFormat);
    try {
    	assertFormatted("Jan 1, 1970 12:00:00 AM", dateTypeAdapter);
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }
  
  public void testOutputFormattedWithIsoFormat()
  {
  	TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    
  	DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(DateFormatType.ISO_8601);
    try {
    	assertFormatted("1970-01-01T00:00:00.000Z", dateTypeAdapter);
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }
  
  public void testOutputFormattedWithLocalOutputDateFormatType()
  {
  	TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.FRANCE);
    
  	DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(DateFormatType.LOCAL);
    try {
    	// Can parse US
    	assertParsed("Jan 1, 1970 12:00:00 AM", dateTypeAdapter);
    	// Can parse FR
    	assertParsed("1 janv. 1970 00:00:00", dateTypeAdapter);
    	// Formats as FR
    	assertFormatted("1 janv. 1970 00:00:00", dateTypeAdapter);
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }
  
  public void testOutputFormattedWithISOOutputDateFormatTypeAndCustomDateFormat()
  {
  	TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.FRANCE);
  	DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(dateFormat, DateFormatType.ISO_8601);
    try {
    	assertFormatted("1970-01-01T00:00:00.000Z", dateTypeAdapter);
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }
  
  public void testOutputFormatWithDateTypeFormatCustomAndNoSpecifiedFormatter()
  {
  	TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    
  	DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(DateFormatType.CUSTOM);
    try {
    	// EN-US output format
    	assertFormatted("Jan 1, 1970 12:00:00 AM", dateTypeAdapter);
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }

  public void testCanParseUsFormatWithCustomDateFormatter()
  {
  	TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
  	DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(dateFormat);
    try {
    	assertParsed("Jan 1, 1970 12:00:00 AM", dateTypeAdapter);
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }

  public void testParsingDatesFormattedWithSystemLocale() {
    TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.FRANCE);
    try {
      assertParsed("1 janv. 1970 00:00:00", new DefaultDateTypeAdapter());
      assertParsed("01/01/70", new DefaultDateTypeAdapter(DateFormat.SHORT));
      assertParsed("1 janv. 1970", new DefaultDateTypeAdapter(DateFormat.MEDIUM));
      assertParsed("1 janvier 1970", new DefaultDateTypeAdapter(DateFormat.LONG));
      assertParsed("01/01/70 00:00",
          new DefaultDateTypeAdapter(DateFormat.SHORT, DateFormat.SHORT));
      assertParsed("1 janv. 1970 00:00:00",
          new DefaultDateTypeAdapter(DateFormat.MEDIUM, DateFormat.MEDIUM));
      assertParsed("1 janvier 1970 00:00:00 UTC",
          new DefaultDateTypeAdapter(DateFormat.LONG, DateFormat.LONG));
      assertParsed("jeudi 1 janvier 1970 00 h 00 UTC",
          new DefaultDateTypeAdapter(DateFormat.FULL, DateFormat.FULL));
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }

  public void testParsingDatesFormattedWithUsLocale() {
    TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    try {
      assertParsed("Jan 1, 1970 0:00:00 AM", new DefaultDateTypeAdapter());
      assertParsed("1/1/70", new DefaultDateTypeAdapter(DateFormat.SHORT));
      assertParsed("Jan 1, 1970", new DefaultDateTypeAdapter(DateFormat.MEDIUM));
      assertParsed("January 1, 1970", new DefaultDateTypeAdapter(DateFormat.LONG));
      assertParsed("1/1/70 0:00 AM",
          new DefaultDateTypeAdapter(DateFormat.SHORT, DateFormat.SHORT));
      assertParsed("Jan 1, 1970 0:00:00 AM",
          new DefaultDateTypeAdapter(DateFormat.MEDIUM, DateFormat.MEDIUM));
      assertParsed("January 1, 1970 0:00:00 AM UTC",
          new DefaultDateTypeAdapter(DateFormat.LONG, DateFormat.LONG));
      assertParsed("Thursday, January 1, 1970 0:00:00 AM UTC",
          new DefaultDateTypeAdapter(DateFormat.FULL, DateFormat.FULL));
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }

  public void testFormatUsesDefaultTimezone() {
    TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    try {
      assertFormatted("Dec 31, 1969 4:00:00 PM", new DefaultDateTypeAdapter());
      assertParsed("Dec 31, 1969 4:00:00 PM", new DefaultDateTypeAdapter());
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }

  public void testDateDeserializationISO8601() throws Exception {
  	DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter();
    assertParsed("1970-01-01T00:00:00.000Z", adapter);
    assertParsed("1970-01-01T00:00Z", adapter);
    assertParsed("1970-01-01T00:00:00+00:00", adapter);
    assertParsed("1970-01-01T01:00:00+01:00", adapter);
  }
  
  public void testDateSerialization() throws Exception {
    int dateStyle = DateFormat.LONG;
    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(dateStyle);
    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
    Date currentDate = new Date();

    String dateString = dateTypeAdapter.serialize(currentDate, Date.class, null).getAsString();
    assertEquals(formatter.format(currentDate), dateString);
  }

  public void testDatePattern() throws Exception {
    String pattern = "yyyy-MM-dd";
    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(pattern);
    DateFormat formatter = new SimpleDateFormat(pattern);
    Date currentDate = new Date();

    String dateString = dateTypeAdapter.serialize(currentDate, Date.class, null).getAsString();
    assertEquals(formatter.format(currentDate), dateString);
  }

  public void testInvalidDatePattern() throws Exception {
    try {
      new DefaultDateTypeAdapter("I am a bad Date pattern....");
      fail("Invalid date pattern should fail.");
    } catch (IllegalArgumentException expected) { }
  }

  private void assertFormatted(String formatted, DefaultDateTypeAdapter adapter) {
    assertEquals(formatted, adapter.serialize(new Date(0), Date.class, null).getAsString());
  }

  private void assertParsed(String date, DefaultDateTypeAdapter  adapter) {
    assertEquals(date, new Date(0), adapter.deserialize(new JsonPrimitive(date), Date.class, null));
    assertEquals("ISO 8601", new Date(0), adapter.deserialize(
        new JsonPrimitive("1970-01-01T00:00:00Z"), Date.class, null));
  }
}
