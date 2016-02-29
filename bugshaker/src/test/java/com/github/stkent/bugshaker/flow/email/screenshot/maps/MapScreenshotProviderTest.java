package com.github.stkent.bugshaker.flow.email.screenshot.maps;

import android.content.Context;
import android.view.ViewGroup;

import com.github.stkent.bugshaker.utilities.Logger;
import com.google.android.gms.maps.MapView;

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
    public void testThat_concurrentModificationExceptionIsNotThrown_when_viewHierarchyContainsMultipleMapViews() {
        final ViewGroup mockRootView = mock(ViewGroup.class);
        when(mockRootView.getChildCount()).thenReturn(2);
        when(mockRootView.getChildAt(anyInt())).thenReturn(mock(MapView.class));

        mapScreenshotProvider.locateMapViewsInHierarchy(mockRootView);
    }

}
