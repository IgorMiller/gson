package com.google.gson;

import java.text.ParseException;
import java.util.Date;

interface DateFormatter {

	String format(Date date);
	Date parse(String dateAsString) throws ParseException;
}