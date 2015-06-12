/**
 * Copyright 2012 Akiban Technologies, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.persistit;

import com.persistit.exception.PersistitException;

import org.junit.Test;

/**
 * Exchange keeps cached a LongRecordHelper. If the volume changes between usages, the LongRecordHelper doesn't
 * get updated with the new volume, resulting in a NPE.
 * 
 */
public class BugLongRecords extends PersistitUnitTestCase {

  @Test
  public void test() throws PersistitException {
    String longRecord = super.createString(1*1000*1000);
    
    cycle(longRecord);
    cycle(longRecord);
  }
  
  
  private void cycle(String longRecord) throws PersistitException {
    Volume vol = _persistit.createTemporaryVolume();
    final Exchange exchange = _persistit.getExchange(vol, "BugLongRecord", true);
    
    exchange.clear();
    exchange.append("key");
    exchange.getValue().put(longRecord);
    exchange.store();
    
    _persistit.releaseExchange(exchange);
    vol.close();
    vol.delete();
  }
}
