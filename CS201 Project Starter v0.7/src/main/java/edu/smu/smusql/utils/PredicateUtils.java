package edu.smu.smusql.utils;

import edu.smu.smusql.ErrorChecks.TypeConverter;
import edu.smu.smusql.model.Record;

import java.util.HashMap;
import java.util.List;
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

    public static Predicate<Record> createPredicateForCondition(String condition, int columnIndex, List<Record> records) {
        String[] individualOperands = condition.split(" ");
        String operator = individualOperands[1];
        Object rightOperand = TypeConverter.parseValue(individualOperands[2]);

        return (Record record) -> {
            Object leftOperand = record.getField(columnIndex);
            return evaluateCondition(operator, leftOperand, rightOperand);
        };
    }

    public static Predicate<Record> combinePredicates(List<Predicate<Record>> predicates, String logicalOperator) {
        Predicate<Record> combinedPredicate = predicates.get(0);

        for (int i = 1; i < predicates.size(); i++) {
            if ("AND".equalsIgnoreCase(logicalOperator)) {
                combinedPredicate = combinedPredicate.and(predicates.get(i));
            }
            else if ("OR".equalsIgnoreCase(logicalOperator)) {
                combinedPredicate = combinedPredicate.or(predicates.get(i));
            }
        }
        return combinedPredicate;
    }
}