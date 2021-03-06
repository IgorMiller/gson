/*
 * Copyright (C) 2015 Google Inc.
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

package com.google.gson.internal.bind.dateformatter;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;

import com.google.gson.internal.bind.util.ISO8601Utils;

public final class ISO8601DateFormatter implements DateFormatter {
	private ISO8601DateFormatter() {
	};

	private final static ISO8601DateFormatter INSTANCE = new ISO8601DateFormatter();

	@Override
	public String format(Date date) {
		return ISO8601Utils.format(date, true);
	}

	@Override
	public Date parse(String dateAsString) throws ParseException {
		return ISO8601Utils.parse(dateAsString, new ParsePosition(0));
	}

	public static ISO8601DateFormatter getInstance() {
		return INSTANCE;
	}

}
