import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QueueInterface Conformance Tests")
public class ParameterizedQueueTest {

    // Providers for ANY queue implementation (bounded or unbounded)
    static Stream<Arguments> queueProviders() {
        return Stream.of(
                Arguments.of("ArrayBoundedQueue", (Supplier<QueueInterface<Integer>>) () -> new ArrayBoundedQueue<>(10)),
                Arguments.of("LinkedQueue", (Supplier<QueueInterface<Integer>>) LinkedQueue::new)
        );
    }

    @Nested
    @DisplayName("Common behavior (all implementations)")
    class CommonBehavior {
        @ParameterizedTest(name = "{0} — create empty")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_create(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            assertTrue(queue.isEmpty());
        }

        @ParameterizedTest(name = "{0} — enqueue")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_enqueue(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            queue.enqueue(42);
            assertFalse(queue.isEmpty());
            assertEquals(1, queue.size());
        }

        @ParameterizedTest(name = "{0} — dequeue")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_dequeue(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            queue.enqueue(99);
            Integer val = queue.dequeue();
            assertEquals(99, val);
            assertTrue(queue.isEmpty());
        }

        @ParameterizedTest(name = "{0} — isFull")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_isFull(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();

            if (queue instanceof ArrayBoundedQueue) {
                for (int i = 0; i < 10; i++) {
                    queue.enqueue(i);
                }
                assertTrue(queue.isFull());
            } else {
                assertFalse(queue.isFull());
            }
        }

        @ParameterizedTest(name = "{0} — isEmpty")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_isEmpty(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            assertTrue(queue.isEmpty());
            queue.enqueue(1);
            assertFalse(queue.isEmpty());
        }

        @ParameterizedTest(name = "{0} — size")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_size(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            assertEquals(0, queue.size());
            queue.enqueue(1);
            assertEquals(1, queue.size());
            queue.enqueue(2);
            queue.enqueue(3);
            assertEquals(3, queue.size());
            queue.dequeue();
            assertEquals(2, queue.size());
        }

        @ParameterizedTest(name = "{0} — enqueue/dequeue")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_enqueue_dequeue(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            queue.enqueue(1);
            assertFalse(queue.isEmpty());
            assertEquals(1, queue.dequeue());
            assertTrue(queue.isEmpty());
        }

        @ParameterizedTest(name = "{0} — underflow on empty dequeue")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_underflow(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            assertThrows(QueueUnderflowException.class, queue::dequeue);
        }
    }

    @Nested
    @DisplayName("ArrayBoundedQueue-specific behavior")
    class ArrayBoundedQueueSpecific {
        @Test
        @DisplayName("ArrayBoundedQueue — overflow exception")
        void test_overflow() {
            ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(2);
            queue.enqueue(1);
            queue.enqueue(2);
            assertThrows(QueueOverflowException.class, () -> queue.enqueue(3));
        }
    }

    @Nested
    @DisplayName("LinkedQueue-specific behavior")
    class LinkedQueueSpecific {
        @Test
        @DisplayName("LinkedQueue — test specific feature")
        void test_linkedQueueSpecific() {
            LinkedQueue<Integer> queue = new LinkedQueue<>();
            for (int i = 0; i < 100; i++) {
                queue.enqueue(i);
            }
            assertEquals(100, queue.size());
            assertFalse(queue.isFull());
        }
        // Add more LinkedQueue-specific tests here
    }
}
