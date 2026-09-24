package utils.CustomList;
// ONE method that handles ALL search types/multiple
public interface SearchCondition<E> {
    boolean matches(E item);
}