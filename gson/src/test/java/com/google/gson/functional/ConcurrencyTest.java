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
package com.google.gson.functional;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;

import com.google.gson.Gson;

/**
 * Tests for ensuring Gson thread-safety.
 * 
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class ConcurrencyTest extends TestCase {
  private Gson gson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    gson = new Gson();
  }

  /**
   * Source-code based on
   * http://groups.google.com/group/google-gson/browse_thread/thread/563bb51ee2495081
   */
  public void testSingleThreadSerialization() { 
    MyObject myObj = new MyObject(); 
    for (int i = 0; i < 10; i++) { 
      gson.toJson(myObj); 
    } 
  } 

  /**
   * Source-code based on
   * http://groups.google.com/group/google-gson/browse_thread/thread/563bb51ee2495081
   */
  public void testSingleThreadDeserialization() { 
    for (int i = 0; i < 10; i++) { 
      gson.fromJson(MyObjectAsJson, MyObject.class); 
    } 
  } 

  
  public void testMultiThreadSerializationMyObject() throws InterruptedException
  {
  	testMultiThreadSerialization(new MyObject());
  }
  
  public void testMultiThreadSerializationMyObjectWithDate() throws InterruptedException
  {
  	testMultiThreadSerialization(new MyObjectWithDate());
  }
  /**
   * Source-code based on
   * http://groups.google.com/group/google-gson/browse_thread/thread/563bb51ee2495081
   */
  private void testMultiThreadSerialization(final Object serializeObject) throws InterruptedException {
    final CountDownLatch startLatch = new CountDownLatch(1);
    final CountDownLatch finishedLatch = new CountDownLatch(10);
    final AtomicBoolean failed = new AtomicBoolean(false);
    ExecutorService executor = Executors.newFixedThreadPool(10);
    for (int taskCount = 0; taskCount < 10; taskCount++) {
      executor.execute(new Runnable() {
        public void run() {
          try {
            startLatch.await();
            for (int i = 0; i < 10; i++) {
              gson.toJson(serializeObject);
            }
          } catch (Throwable t) {
            failed.set(true);
          } finally {
            finishedLatch.countDown();
          }
        }
      });
    }
    startLatch.countDown();
    finishedLatch.await();
    assertFalse(failed.get());
  }

  public void testMultiThreadDeserializationMyObject() throws InterruptedException
  {
  	testMultiThreadDeserialization(MyObjectAsJson, MyObject.class);
  }
  
  public void testMultiThreadDeserializationMyObjectWithDate() throws InterruptedException
  {
  	testMultiThreadDeserialization(MyObjectWithDateAsJson, MyObjectWithDate.class);
  }
  /**
   * Source-code based on
   * http://groups.google.com/group/google-gson/browse_thread/thread/563bb51ee2495081
   */
  private <T> void testMultiThreadDeserialization(final String json, final Class<T> objectClass) throws InterruptedException {
    final CountDownLatch startLatch = new CountDownLatch(1);
    final CountDownLatch finishedLatch = new CountDownLatch(10);
    final AtomicBoolean failed = new AtomicBoolean(false);
    ExecutorService executor = Executors.newFixedThreadPool(10);
    for (int taskCount = 0; taskCount < 10; taskCount++) {
      executor.execute(new Runnable() {
        public void run() {
          try {
            startLatch.await();
            for (int i = 0; i < 10; i++) {
              gson.fromJson(json, objectClass); 
            }
          } catch (Throwable t) {
            failed.set(true);
          } finally {
            finishedLatch.countDown();
          }
        }
      });
    }
    startLatch.countDown();
    finishedLatch.await();
    assertFalse(failed.get());
  }
  
  private static final String MyObjectAsJson = "{'a':'hello','b':'world','i':1}";
  
  @SuppressWarnings("unused")
  private static class MyObject {
    String a;
    String b;
    int i;

    MyObject() {
      this("hello", "world", 42);
    }

    public MyObject(String a, String b, int i) {
      this.a = a;
      this.b = b;
      this.i = i;
    }
  }
  
  // ISO and US formats
  private static final String MyObjectWithDateAsJson = "{'a':'1970-01-01T00:00:00.000Z','b':'Jan 1, 1970 12:00:00 AM','i':'Jan 1, 1970 12:00:00 AM'}";
  
  private static class MyObjectWithDate {
    Date a;
    Date b;
    Date i;

    MyObjectWithDate() {
      this(new Date(0), new Date(0), new Date(0));
    }

    public MyObjectWithDate(Date a, Date b, Date i) {
      this.a = a;
      this.b = b;
      this.i = i;
    }
  }
}
