package com.strands.interviews.eventsystem;

import com.strands.interviews.eventsystem.events.SimpleEvent;
import com.strands.interviews.eventsystem.events.SubEvent;
import com.strands.interviews.eventsystem.impl.DefaultEventManager;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultEventManagerTest
{
    private EventManager eventManager = new DefaultEventManager();

    @Test
    public void testPublishNullEvent()
    {
        eventManager.publishEvent(null);
    }

    @Test
    public void testRegisterListenerAndPublishEvent()
    {
        EventListenerMock eventListenerMock = new EventListenerMock(new Class[]{SimpleEvent.class});
        eventManager.registerListener("some.key", eventListenerMock);
        eventManager.publishEvent(new SimpleEvent(this));
        assertTrue(eventListenerMock.isCalled());
    }

    @Test
    public void testListenerWithoutMatchingEventClass()
    {
        EventListenerMock eventListenerMock = new EventListenerMock(new Class[]{SubEvent.class});
        eventManager.registerListener("some.key", eventListenerMock);
        eventManager.publishEvent(new SimpleEvent(this));
        assertFalse(eventListenerMock.isCalled());
    }

    @Test
    public void testUnregisterListener()
    {
        EventListenerMock eventListenerMock = new EventListenerMock(new Class[]{SimpleEvent.class});
        EventListenerMock eventListenerMock2 = new EventListenerMock(new Class[]{SimpleEvent.class});

        eventManager.registerListener("some.key", eventListenerMock);
        eventManager.registerListener("another.key", eventListenerMock2);
        eventManager.unregisterListener("some.key");

        eventManager.publishEvent(new SimpleEvent(this));
        assertFalse(eventListenerMock.isCalled());
        assertTrue(eventListenerMock2.isCalled());
    }


    /**
     * Check that registering and unregistering listeners behaves properly.
     */
    @Test
    public void testRemoveNonexistentListener()
    {
        DefaultEventManager dem = (DefaultEventManager)eventManager;
        assertEquals(0, dem.getListeners().size());
        eventManager.registerListener("some.key", new EventListenerMock(new Class[]{SimpleEvent.class}));
        assertEquals(1, dem.getListeners().size());
        eventManager.unregisterListener("this.key.is.not.registered");
        assertEquals(1, dem.getListeners().size());
        eventManager.unregisterListener("some.key");
        assertEquals(0, dem.getListeners().size());
    }

    /**
     * Registering duplicate keys on different listeners should only fire the most recently added.
     */
    @Test
    public void testDuplicateKeysForListeners()
    {
        EventListenerMock eventListenerMock = new EventListenerMock(new Class[]{SimpleEvent.class});
        EventListenerMock eventListenerMock2 = new EventListenerMock(new Class[]{SimpleEvent.class});

        eventManager.registerListener("some.key", eventListenerMock);
        eventManager.registerListener("some.key", eventListenerMock2);

        eventManager.publishEvent(new SimpleEvent(this));

        assertTrue(eventListenerMock2.isCalled());
        assertFalse(eventListenerMock.isCalled());

        eventListenerMock.resetCalled();
        eventListenerMock2.resetCalled();

        eventManager.unregisterListener("some.key");
        eventManager.publishEvent(new SimpleEvent(this));

        assertFalse(eventListenerMock2.isCalled());
        assertFalse(eventListenerMock.isCalled());
    }

    /**
     * Attempting to register a null with a valid key should result in an illegal argument exception
     */
    @Test
    public void testAddValidKeyWithNullListener()
    {
        try
        {
            eventManager.registerListener("bogus.key", null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException ex)
        {
        }
    }

    @Test
    public void testTask1() {
        // Creating event listener for classes parent and child
        EventListenerMock eventListenerMockSimpleEvents = new EventListenerMock(new Class[]{SimpleEvent.class});
        EventListenerMock eventListenerMockSubEvents = new EventListenerMock(new Class[]{SubEvent.class});
        // Registering listeners for both classes
        eventManager.registerListener("simple.events", eventListenerMockSimpleEvents);
        eventManager.registerListener("sub.events", eventListenerMockSubEvents);
        // Publishing event of child class, SubEvent
        eventManager.publishEvent(new SubEvent(this));
        // Verifying that only the listener for SubEvents is called
        assertFalse(eventListenerMockSimpleEvents.isCalled());
        assertTrue(eventListenerMockSubEvents.isCalled());
    }

    @Test
    public void testTask2() {
        // Creating event listener for different classes
        EventListenerMock eventListenerMockSimpleEvents = new EventListenerMock(new Class[]{SimpleEvent.class});
        EventListenerMock eventListenerMockSubEvents = new EventListenerMock(new Class[]{SubEvent.class});

        // Creating special event listener
        EventListenerMock eventListenerAllEvents = new EventListenerMock(new Class[]{});

        // Registering listeners
        eventManager.registerListener("simple.events", eventListenerMockSimpleEvents);
        eventManager.registerListener("sub.events", eventListenerMockSubEvents);
        eventManager.registerListener("object.events", eventListenerAllEvents);

        // Publishing SubEvent and verifying special listener was called
        eventManager.publishEvent(new SubEvent(this));
        assertTrue(eventListenerAllEvents.isCalled());

        // Publishing Simple and verifying special listener was also called
        eventManager.publishEvent(new SimpleEvent(this));
        assertTrue(eventListenerAllEvents.isCalled());
    }

    @Test
    public void testTask3() {
        // Creating event listener for parent class
        EventListenerMock eventListenerMockSimpleEvents = new EventListenerMock(new Class[]{SimpleEvent.class});
        // Registering listeners for parent class
        eventManager.registerListener("simple.events", eventListenerMockSimpleEvents);
        // Publishing event of child class, SubEvent
        eventManager.publishEvent(new SubEvent(this));
        // Verifying that listener for parent class is called
        assertTrue(eventListenerMockSimpleEvents.isCalled());
    }
}
