package org.example;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class ClusterPruningTest {

    private ClusterPruning clusterPruning;
    private Map<Integer, Map<String, Double>> vectors;

    @BeforeEach
    void setUp() {
        vectors = new HashMap<>(); // imitation
        vectors.put(1, Map.of("ai", 1.0, "ml", 0.8));
        vectors.put(2, Map.of("ai", 0.9, "ml", 0.9));
        vectors.put(3, Map.of("java", 1.0, "junit", 0.8));
        vectors.put(4, Map.of("java", 0.9, "junit", 0.9));
        vectors.put(5, Map.of("cloud", 1.0, "aws", 0.8));

        clusterPruning = new ClusterPruning(vectors);
    }

    @Test
    @Tag("offline")
    void testPrePruneDoesNotThrow() {
        assertDoesNotThrow(() -> clusterPruning.prePrune());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 100})
    @Tag("online")
    void testPruneWithSingleParameter(int docId) {
        Assumptions.assumeTrue(vectors.containsKey(docId),
                "Document " + docId + " does not exist");

        clusterPruning.prePrune();
        Map<String, Double> query = vectors.get(docId);
        List<Integer> result = clusterPruning.prune(query);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("queryAndExpectedSizeProvider")
    @Tag("online")
    void testPruneWithSetOfParameters(Map<String, Double> query, int minExpectedSize) {
        Assumptions.assumeFalse(vectors.isEmpty());

        clusterPruning.prePrune();
        List<Integer> result = clusterPruning.prune(query);

        assertTrue(result.size() >= minExpectedSize);
    }

    static Stream<Arguments> queryAndExpectedSizeProvider() {
        return Stream.of(
                Arguments.of(Map.of("ai", 1.0), 2),        // Очікуємо кластер 1-2
                Arguments.of(Map.of("java", 1.0), 2),      // Очікуємо кластер 3-4
                Arguments.of(Map.of("unknown", 1.0), 0)    // Нічого не знайдено
        );
    }

    @TestFactory
    @Tag("dynamic")
    Stream<DynamicTest> dynamicTestsForClusters() {
        clusterPruning.prePrune();
        List<Integer> testDocs = List.of(2, 4);

        return testDocs.stream().map(docId ->
                dynamicTest("Dynamic test for searching similar"
                        + " docs for document with id" + docId, () -> {
                    Map<String, Double> query = vectors.get(docId);
                    List<Integer> result = clusterPruning.prune(query);

                    assertFalse(result.isEmpty());
                })
        );
    }
}
