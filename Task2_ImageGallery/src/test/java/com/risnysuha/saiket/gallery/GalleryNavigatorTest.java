package com.risnysuha.saiket.gallery;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GalleryNavigatorTest {

    @Test
    public void nextMovesForwardAndWrapsToTheFirstImage() {
        GalleryNavigator nav = new GalleryNavigator(12, 10);
        assertEquals(11, nav.next());
        assertEquals(0, nav.next());
    }

    @Test
    public void previousMovesBackAndWrapsToTheLastImage() {
        GalleryNavigator nav = new GalleryNavigator(12, 1);
        assertEquals(0, nav.previous());
        assertEquals(11, nav.previous());
    }

    @Test
    public void labelIsOneBased() {
        assertEquals("1 / 12", new GalleryNavigator(12, 0).label());
        assertEquals("12 / 12", new GalleryNavigator(12, 11).label());
    }

    @Test
    public void startPositionIsKeptInsideTheGallery() {
        assertEquals(11, new GalleryNavigator(12, 99).getPosition());
        assertEquals(0, new GalleryNavigator(12, -4).getPosition());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyGalleryIsRejected() {
        new GalleryNavigator(0, 0);
    }

    @Test
    public void jumpToGoesStraightToAThumbnail() {
        GalleryNavigator nav = new GalleryNavigator(12, 0);
        assertEquals(7, nav.jumpTo(7));
        assertEquals("8 / 12", nav.label());
        assertEquals(11, nav.jumpTo(40));
    }
}
