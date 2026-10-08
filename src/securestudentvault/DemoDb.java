/*
 * Package: securestudentvault
 * File: DemoDb.java
 * Rules covered: N/A
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package src.securestudentvault;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * Stand-in for a JDBC driver using dynamic proxies, so the code demo runs without an external database
 * Only to be used in a static context, cannot be instantiated as an object
 */
public class DemoDb {

    /**
     * Private default constructor to ensure that the class cannot be instantiated as an object
     */
    private DemoDb(){

    }

    /**
     * Returns a JDBC Connection object given a trace
     * 
     * @param trace List<String> 
     * @return Connection
     */
    public static Connection connection(List<String> trace) {
        return proxy(Connection.class, (p, m, a) -> {
            switch (m.getName()) {
                case "prepareStatement":
                    trace.add("prepare: " + a[0]);
                    return statement(trace);
                case "close":
                    return null;
                default:
                    throw new UnsupportedOperationException(m.getName());
            }
        });
    }

    /**
     * Returns a JDBC PreparedStatement object given a trace
     * 
     * @param trace List<String> 
     * @return PreparedStatement
     */
    private static PreparedStatement statement(List<String> trace) {
        final String[] bound = new String[1];
        return proxy(PreparedStatement.class, (p, m, a) -> {
            switch (m.getName()) {
                case "setString":
                    bound[0] = (String) a[1];
                    trace.add("bind ?" + a[0] + " = '" + a[1] + "'  (sent as data, never parsed as SQL)");
                    return null;
                case "executeQuery":
                    return resultSet(bound[0]);
                case "close":
                    return null;
                default:
                    throw new UnsupportedOperationException(m.getName());
            }
        });
    }

    /**
     * Returns a JDBC ResultSet object with given a name
     * Uses a one-element served flag, for the same lambda reason as above
     * 
     * @param name String
     * @return ResultSet that always holds exactly one row
     */
    private static ResultSet resultSet(String name){
        final boolean[] served = new boolean[1];
        return proxy(ResultSet.class, (p, m, a) -> {
            switch (m.getName()) {
                case "next":
                    if (served[0]) {
                        return Boolean.FALSE;
                    }
                    served[0] = true;
                    return Boolean.TRUE;
                case "getInt":
                    return 1001;
                case "getString":
                    return name;
                case "close":
                    return null;
                default:
                    throw new UnsupportedOperationException(m.getName());
            }
        });
    }

    /**
     * Private helper helper method that wraps Proxy.newProxyInstance()
     * It creates an object implementing 'type' that sends every call to 'handler', then casts it to T
     * 
     * @param type The Class<T> type to implement
     * @param handler The InvocationHandler to send method invocations to
     * @return 
     */
    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, InvocationHandler handler){
        return (T) Proxy.newProxyInstance(DemoDb.class.getClassLoader(), new Class<?>[] {type}, handler);
    }
}
