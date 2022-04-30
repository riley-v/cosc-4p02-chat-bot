/**
 * This interface defines a table representative class.
 * They must implement the method getValue in order for
 * the toList method in Main to work.
 */

public interface TableRep {
    /**
     * This method is used by toList in Main
     * @param attrNum the number of the attribute to return (eg. 0 = 1st attribute)
     * @return the attribute at position attrNum
     */
    public Object getValue(int attrNum);
}
