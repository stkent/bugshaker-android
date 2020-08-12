/*
 * Copyright 2016 Stuart Kent
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.stkent.bugshaker;

import android.app.Application;

import com.github.stkent.bugshaker.flow.dialog.AlertDialogType;
import com.github.stkent.bugshaker.flow.dialog.DialogProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Random;

@RunWith(MockitoJUnitRunner.class)
public class BugShakerTest {

    @Mock
    private Application mockApplication;

    @Mock
    private DialogProvider mockDialogProvider;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        BugShaker.clearSharedInstance();
    }

    @SuppressWarnings("MethodName")
    @Test
    public void testThat_exceptionThrown_when_settingDialogTypeAfterCustomDialogProvider() {
        // Arrange
        AlertDialogType[] allTypes = AlertDialogType.values();
        AlertDialogType anyType = allTypes[new Random().nextInt(allTypes.length)];

        BugShaker bugShaker = BugShaker
                .get(mockApplication)
                .setCustomDialogProvider(mockDialogProvider);

        // Assert (pending)
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(
                "You may call either setAlertDialogType or setCustomDialogProvider, but not both."
        );

        // Act
        bugShaker.setAlertDialogType(anyType);
    }

    @SuppressWarnings("MethodName")
    @Test
    public void testThat_exceptionThrown_when_settingCustomDialogProviderAfterDialogType() {
        // Arrange
        AlertDialogType[] allTypes = AlertDialogType.values();
        AlertDialogType anyType = allTypes[new Random().nextInt(allTypes.length)];

        BugShaker bugShaker = BugShaker
                .get(mockApplication)
                .setAlertDialogType(anyType);

        // Assert (pending)
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(
                "You may call either setAlertDialogType or setCustomDialogProvider, but not both."
        );

        // Act
        bugShaker.setCustomDialogProvider(mockDialogProvider);
    }

}
