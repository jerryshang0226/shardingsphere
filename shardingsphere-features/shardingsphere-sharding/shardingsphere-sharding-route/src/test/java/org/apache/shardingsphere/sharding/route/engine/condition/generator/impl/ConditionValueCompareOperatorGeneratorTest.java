/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.sharding.route.engine.condition.generator.impl;

import com.google.common.collect.Range;
import org.apache.shardingsphere.sharding.strategy.value.ListRouteValue;
import org.apache.shardingsphere.sharding.strategy.value.RangeRouteValue;
import org.apache.shardingsphere.sharding.strategy.value.RouteValue;
import org.apache.shardingsphere.sharding.route.engine.condition.Column;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.complex.CommonExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.simple.LiteralExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.value.PredicateCompareRightValue;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class ConditionValueCompareOperatorGeneratorTest {
    
    private final ConditionValueCompareOperatorGenerator generator = new ConditionValueCompareOperatorGenerator();
    
    private final Column column = new Column("id", "tbl");
    
    @SuppressWarnings("unchecked")
    @Test
    public void assertGenerateConditionValue() {
        int value = 1;
        PredicateCompareRightValue rightValue = new PredicateCompareRightValue("=", new LiteralExpressionSegment(0, 0, value));
        Optional<RouteValue> routeValue = generator.generate(rightValue, column, new LinkedList<>());
        assertTrue(routeValue.isPresent());
        assertTrue(((ListRouteValue<Integer>) routeValue.get()).getValues().contains(value));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void assertGenerateConditionValueWithLessThanOperator() {
        PredicateCompareRightValue rightValue = new PredicateCompareRightValue("<", new LiteralExpressionSegment(0, 0, 1));
        Optional<RouteValue> routeValue = generator.generate(rightValue, column, new LinkedList<>());
        assertTrue(routeValue.isPresent());
        assertTrue(Range.lessThan(1).encloses(((RangeRouteValue<Integer>) routeValue.get()).getValueRange()));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void assertGenerateConditionValueWithGreaterThanOperator() {
        PredicateCompareRightValue rightValue = new PredicateCompareRightValue(">", new LiteralExpressionSegment(0, 0, 1));
        Optional<RouteValue> routeValue = generator.generate(rightValue, column, new LinkedList<>());
        assertTrue(routeValue.isPresent());
        assertTrue(Range.greaterThan(1).encloses(((RangeRouteValue<Integer>) routeValue.get()).getValueRange()));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void assertGenerateConditionValueWithAtMostOperator() {
        PredicateCompareRightValue rightValue = new PredicateCompareRightValue("<=", new LiteralExpressionSegment(0, 0, 1));
        Optional<RouteValue> routeValue = generator.generate(rightValue, column, new LinkedList<>());
        assertTrue(routeValue.isPresent());
        assertTrue(Range.atMost(1).encloses(((RangeRouteValue<Integer>) routeValue.get()).getValueRange()));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void assertGenerateConditionValueWithAtLeastOperator() {
        PredicateCompareRightValue rightValue = new PredicateCompareRightValue(">=", new LiteralExpressionSegment(0, 0, 1));
        Optional<RouteValue> routeValue = generator.generate(rightValue, column, new LinkedList<>());
        assertTrue(routeValue.isPresent());
        assertTrue(Range.atLeast(1).encloses(((RangeRouteValue<Integer>) routeValue.get()).getValueRange()));
    }
    
    @Test
    public void assertGenerateConditionValueWithErrorOperator() {
        PredicateCompareRightValue rightValue = new PredicateCompareRightValue("!=", new LiteralExpressionSegment(0, 0, 1));
        assertFalse(generator.generate(rightValue, column, new LinkedList<>()).isPresent());
    }
    
    @Test
    public void assertGenerateConditionValueWithoutNowExpression() {
        PredicateCompareRightValue rightValue = new PredicateCompareRightValue("=", new CommonExpressionSegment(0, 0, "value"));
        assertFalse(generator.generate(rightValue, column, new LinkedList<>()).isPresent());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void assertGenerateConditionValueWithNowExpression() {
        PredicateCompareRightValue rightValue = new PredicateCompareRightValue("=", new CommonExpressionSegment(0, 0, "now()"));
        Optional<RouteValue> routeValue = generator.generate(rightValue, column, new LinkedList<>());
        assertTrue(routeValue.isPresent());
        assertFalse(((ListRouteValue<Integer>) routeValue.get()).getValues().isEmpty());
    }
}
