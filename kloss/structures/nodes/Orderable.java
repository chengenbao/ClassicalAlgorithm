package kloss.structures.nodes;

public interface Orderable {

  boolean lessThan(Orderable object);
  boolean greaterThan(Orderable object);
  boolean equal(Orderable object);

}
