package rip.diamond.practice.util;

public class InsertUtil {

    public static InsertType check(String str) {
        if (Checker.isUUID(str)) {
            return InsertType.UUID;
        }
        return InsertType.STRING;
    }

    public enum InsertType {
        STRING,
        UUID
    }

}
