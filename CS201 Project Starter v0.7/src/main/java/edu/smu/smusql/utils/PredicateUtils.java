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
        // Ensure that the condition contains at least 3 parts: left operand, operator, right operand
        String[] individualOperands = condition.split("\\s*(=|>|<|>=|<=)\\s*");

        if (individualOperands.length < 2) {
            System.err.println("Invalid condition format: " + condition);
            return record -> false; // Return a predicate that always returns false
        }

        String leftOperand = individualOperands[0].trim();  // This should be the column name
        String operator = condition.replace(leftOperand, "").trim().substring(0, 1);  // Extract operator (first non-whitespace)
        String rightOperandRaw = individualOperands[1].trim();  // The value to compare
        Object rightOperand = TypeConverter.parseValue(rightOperandRaw.replaceAll("^'(.*)'$", "$1"));  // Remove quotes if necessary

        return (Record record) -> {
            try {
                // Ensure the field exists and is accessed correctly based on column name
                Object fieldValue = record.getField(columnIndex);  // This should now correctly match the index for the column name
                return evaluateCondition(operator, fieldValue, rightOperand);
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Index out of bounds while evaluating: " + condition);
                return false; // Fail-safe to prevent crashes
            }
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