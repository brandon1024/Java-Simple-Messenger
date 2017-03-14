package unit;

import static org.junit.Assert.*;

import org.junit.Test;
import util.DynamicQueue;

public class DynamicQueueTest
{
    @Test
    public void testIsEmpty()
    {
        DynamicQueue<Integer> dqi = new DynamicQueue<Integer>();
        for(int i = 0; i < 10; i++)
            dqi.enqueue(i);

        for(int i = 0; i < 10; i++)
            dqi.dequeue();

        assertTrue("DynamicQueue should be empty but has one or more items", dqi.isEmpty());
    }

    @Test
    public void testSize()
    {
        DynamicQueue<Integer> dqi = new DynamicQueue<Integer>();
        for(int i = 1; i <= 20; i++)
        {
            dqi.enqueue(i);
            assertEquals("Incorrect size of queue", i, dqi.size());
        }

        for(int i = 19; i >= 5; i--)
        {
            dqi.dequeue();
            assertEquals("Incorrect size of queue", i, dqi.size());
        }
    }

    @Test
    public void testEnqueue()
    {
        DynamicQueue<Integer> dqi = new DynamicQueue<Integer>();
        for(int i = 1; i <= 20; i++)
        {
            dqi.enqueue(i);
            assertEquals("Incorrect size of queue", i, dqi.size());
        }
    }

    @Test
    public void testDequeue()
    {
        DynamicQueue<Integer> dqi = new DynamicQueue<Integer>();
        for(int i = 1; i <= 20; i++)
            dqi.enqueue(i);

        for(int i = 1; i <= 20; i++)
            assertEquals("incorrect value returned", new Integer(i), dqi.dequeue());

        assertEquals("Unexpected queue size", 0, dqi.size());

        assertNull("Dequeue object from an empty queue should return null", dqi.dequeue());
    }

    @Test
    public void testRemoveAll()
    {
        DynamicQueue<Integer> dqi = new DynamicQueue<Integer>();
        for(int i = 1; i <= 50; i++)
            dqi.enqueue(i);

        dqi.removeAll();

        assertEquals("Unexpected queue size", 0, dqi.size());
    }
}