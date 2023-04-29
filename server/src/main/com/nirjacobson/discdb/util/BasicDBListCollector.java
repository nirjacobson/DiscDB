package com.nirjacobson.discdb.util;

import com.mongodb.BasicDBList;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class BasicDBListCollector<T> implements Collector<T, BasicDBList, BasicDBList> {

  @Override
  public Supplier<BasicDBList> supplier() {
    return BasicDBList::new;
  }

  @Override
  public BiConsumer<BasicDBList, T> accumulator() {
    return BasicDBList::add;
  }

  @Override
  public BinaryOperator<BasicDBList> combiner() {
    return (left, right) -> {
      left.addAll(right);
      return left;
    };
  }

  @Override
  public Function<BasicDBList, BasicDBList> finisher() {
    return Function.identity();
  }

  @Override
  public Set<Characteristics> characteristics() {
    return EnumSet.of(Characteristics.IDENTITY_FINISH);
  }
}
