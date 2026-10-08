package recommendations.err53j;

/** 
 * Demonstrates CERT rule ERR53-J. 
 * Errors should not be ignored when they can prevent important cleanup. 
 * The noncompliant example does not handle the error, so the cleanup code 
 * is never reached. The compliant example uses finally so the cleanup 
 * code still runs when an error occurs. */
public class MainClass {

    /** 
    * Runs the noncompliant and compliant examples. 
    * The noncompliant example runs in a separate thread so the error 
    * does not stop the rest of the demonstration. The compliant example
    * catches the error and still performs its cleanup. 
    * 
    * @param args 
    * @throws InterruptedException if the main thread is interrupted 
    */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Noncompliant:");
        
        // Runs the example in a separate thread so the program can continue.
        Thread worker = new Thread(MainClass::processNoncompliant);
        worker.start();
        worker.join();

        System.out.println("\nCompliant:");
        processCompliant();
        System.out.println("  Program is still running normally");
    }
    
    /**
    * Tries to create an extremely large array.
    * The large allocation causes an OutOfMemoryError, which is an Error
    * rather than a normal exception.
    */
    public static void allocateTooMuch() {
        long[] huge = new long[Integer.MAX_VALUE];
        System.out.println("  Allocated " + huge.length + " elements");
    }

    /**
    * NONCOMPLIANT: Does not handle the error. 
    * If the array allocation fails, the error stops the method before
    * the resource can be released. 
    * */
    public static void processNoncompliant() {
        System.out.println("  Opened resource");
        allocateTooMuch();
        System.out.println("  Released resource"); // never reached
    }

    /**
    * COMPLIANT: Handles the error and always performs cleanup.
    * The catch block logs the error, and the finally block releases the 
    * resource even if an error occurs. 
    * */
    public static void processCompliant() {
        System.out.println("  Opened resource");
        try {
            allocateTooMuch();
        } catch (Throwable t) {
            System.out.println("  Logged error: " + t);
        } finally {
            System.out.println("  Released resource"); // always runs
        }
    }
}
