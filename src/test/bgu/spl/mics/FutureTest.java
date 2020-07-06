package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    private Future future;

    @Before
    public void setUp() throws Exception {
        future = new Future<String>();
    }

    @After
    public void tearDown() throws Exception {
        future = null;
    }

    @Test
    public void get() {
        //check if got future
        future.resolve("resolved");
        assertEquals(future.get(),"resolved");
        assertNotNull(future.get());
    }

    @Test
    public void resolve() {
        //check if not resolved yet
        assertFalse(future.isDone());
        future.resolve("resolved");
        //check that resolved
        assertTrue(future.isDone());
    }

    @Test
    public void isDone() {
        assertFalse(future.isDone());
        future.resolve("done");
        assertTrue(future.isDone());
    }

    @Test
    public void get1() {
        TimeUnit unit = TimeUnit.MILLISECONDS;
        long timeout = 2;
        //check if get the resolved
        future.resolve("check");
        assertEquals(future.get(timeout, unit), "check");
    }
}