package com.distrokv;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.*; import java.util.*;
class SortedIndexTest { @Test void preservesNewestVersionAndSortedRange() { var i=new SortedIndex(); i.put("b",new ValueRecord("old",new VectorClock(Map.of("a",1L)))); i.put("b",new ValueRecord("new",new VectorClock(Map.of("a",2L)))); i.put("a",new ValueRecord("first",new VectorClock(Map.of()))); assertEquals("new",i.get("b").orElseThrow().value()); assertEquals(List.of("a","b"),new ArrayList<>(i.range("a","z").keySet())); } }
