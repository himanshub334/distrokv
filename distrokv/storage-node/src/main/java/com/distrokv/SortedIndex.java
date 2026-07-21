package com.distrokv;
import java.util.*;
import java.util.concurrent.*;
/** Concurrent sorted index: log(n) point lookup and ordered range scans. */
public final class SortedIndex {
  private final ConcurrentSkipListMap<String, ValueRecord> values = new ConcurrentSkipListMap<>();
  public void put(String key, ValueRecord value) { values.compute(key, (k, old) -> old == null || value.clock().compare(old.clock()) >= 0 ? value : old); }
  public Optional<ValueRecord> get(String key) { return Optional.ofNullable(values.get(key)); }
  public Map<String, ValueRecord> range(String from, String to) { return Map.copyOf(values.subMap(from, true, to, true)); }
  public int size() { return values.size(); }
}
