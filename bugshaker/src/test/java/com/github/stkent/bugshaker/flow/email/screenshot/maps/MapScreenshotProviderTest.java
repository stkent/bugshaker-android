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
package com.github.stkent.bugshaker.flow.email.screenshot.maps;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.github.stkent.bugshaker.utilities.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class MapScreenshotProviderTest {

    private MapScreenshotProvider mapScreenshotProvider;

    @Mock
    private Context applicationContext;

    @Before
    public void setUp() {
        initMocks(this);
        mapScreenshotProvider = new MapScreenshotProvider(applicationContext, new Logger(false));
    }

    @Test
    public void testThat_noExceptionThrown_when_searchingViewHierarchyThatContainsMultipleViews() {
        // Arrange
        final ViewGroup mockRootView = mock(ViewGroup.class);
        final int numberOfChildViews = 4;
        assert numberOfChildViews > 1;

        when(mockRootView.getChildCount()).thenReturn(numberOfChildViews);
        when(mockRootView.getChildAt(anyInt())).thenReturn(mock(View.class));

        // Act
        mapScreenshotProvider.locateMapViewsInHierarchy(mockRootView);
    }

}
