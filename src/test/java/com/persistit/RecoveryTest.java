/*
 * Copyright (c) 2004 Persistit Corporation. All Rights Reserved.
 *
 * The Java source code is the confidential and proprietary information
 * of Persistit Corporation ("Confidential Information"). You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Persistit Corporation.
 *
 * PERSISTIT CORPORATION MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. PERSISTIT CORPORATION SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * Created on Aug 24, 2004
 */
package com.persistit;

import java.util.Properties;

import com.persistit.exception.PersistitException;
import com.persistit.unit.PersistitUnitTestCase;

public class RecoveryTest extends PersistitUnitTestCase {
    //
    // This class needs to be in com.persistit because Persistit#getLogManager() is
    // package-private.
    //
    private static String[] _args = new String[0];

    private String _volumeName = "persistit";

    public void test1() throws Exception {
        store1();
        _persistit.close();
        final Properties saveProperties = _persistit.getProperties();
        _persistit = new Persistit();
        _persistit.initialize(saveProperties);
        fetch1a();
        fetch1b();
    }
    
    public void test2() throws Exception {
        store1();
        LogManager logMan = _persistit.getLogManager();
        assertTrue(logMan.getPageMapSize() > 0);
        _persistit.checkpoint();
        logMan.copyBack(Long.MAX_VALUE);
        _persistit.close();
        assertEquals(0, logMan.getPageMapSize());
        final Properties saveProperties = _persistit.getProperties();
        _persistit = new Persistit();
        _persistit.initialize(saveProperties);
        logMan = _persistit.getLogManager();
        assertEquals(0, logMan.getPageMapSize());
        fetch1a();
        fetch1b();
    }

    private void store1() throws PersistitException {
        final Exchange exchange = _persistit.getExchange(_volumeName,
                "SimpleTest1", true);
        exchange.removeAll();
        final StringBuffer sb = new StringBuffer();

        for (int i = 1; i < 50000; i++) {
            sb.setLength(0);
            sb.append((char) (i / 20 + 64));
            sb.append((char) (i % 20 + 64));
            exchange.clear().append(sb);
            exchange.getValue().put("Record #" + i);
            exchange.store();
        }
    }

    private void fetch1a() throws PersistitException {
        final Exchange exchange = _persistit.getExchange(_volumeName,
                "SimpleTest1", false);
        final StringBuffer sb = new StringBuffer();

        for (int i = 1; i < 50000; i++) {
            sb.setLength(0);
            sb.append((char) (i / 20 + 64));
            sb.append((char) (i % 20 + 64));
            exchange.clear().append(sb);
            exchange.fetch();
            assertTrue(exchange.getValue().isDefined());
            assertEquals("Record #" + i, exchange.getValue().getString());
        }

    }

    private void fetch1b() throws PersistitException {
        final Exchange exchange = _persistit.getExchange(_volumeName,
                "SimpleTest1", false);
        final StringBuffer sb = new StringBuffer();
        for (int i = 1; i < 400; i++) {
            sb.setLength(0);
            sb.append((char) (i % 20 + 64));
            sb.append((char) (i / 20 + 64));
            exchange.clear().append(sb);
            exchange.fetch();
            final int k = (i / 20) + (i % 20) * 20;
            assertEquals(exchange.getValue().getString(), "Record #" + k);
        }

        System.out.println("- done");
    }


    
    public static void main(final String[] args) throws Exception {
        _args = args;
        new RecoveryTest().initAndRunTest();
    }
    
    public void runAllTests() throws Exception {
        test1();
        test2();
    }
}