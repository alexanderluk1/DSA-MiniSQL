package edu.smu.smusql.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class PredicateUtils {
    private static final Map<String, BiPredicate<Object, Object>> operatorMap = new HashMap<>();

    static {
        operatorMap.put(">", (a, b) -> Double.parseDouble(a.toString()) > Double.parseDouble(b.toString()));
        operatorMap.put("<", (a, b) -> Double.parseDouble(a.toString()) < Double.parseDouble(b.toString()));
        operatorMap.put("=", Object::equals);
    }

    public static boolean evaluateCondition(String operator, Object leftOperand, Object rightOperand) {
        // Fetch the predicate from the map based on the operator
        BiPredicate<Object, Object> predicate = operatorMap.get(operator);

        // Apply the predicate to the operands
        return predicate != null && predicate.test(leftOperand, rightOperand);
    }

    public static Predicate<Object> createPredicateForCondition(String operator, Object leftOperand, Object rightOperand) {
        // Fetch the BiPredicate based on the operator and return a Predicate
        return (obj) -> evaluateCondition(operator, leftOperand, rightOperand);
    }

    /**
     * Combines two predicates with AND logic.
     */
    public static Predicate<Object> combineWithAnd(Predicate<Object> first, Predicate<Object> second) {
        return first.and(second);
    }

    /**
     * Combines two predicates with OR logic.
     */
    public static Predicate<Object> combineWithOr(Predicate<Object> first, Predicate<Object> second) {
        return first.or(second);
    }
}