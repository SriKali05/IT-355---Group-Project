package reccomendations.obj54j;

//working example code explaining recommendation OBJ54-J
//OBJ54-J: do not attempt to help the garbage collector by setting local reference variables to null

public class MainClass {

    public static void main(String[] args) {

        // NONCOMPLIANT: setting a local variable to null is unnecessary
        int[] buffer1 = new int[100];
        buffer1[0] = 42;
        System.out.println("Noncompliant: used buffer, buffer1[0] = " + buffer1[0]);
        buffer1 = null; // pointless: the JIT already knows buffer1 is no longer used
        System.out.println("Noncompliant: set buffer1 to null by hand");

        // COMPLIANT: a block limits the scope, so no null assignment is needed
        { // Limit the scope of buffer2
            int[] buffer2 = new int[100];
            buffer2[0] = 42;
            System.out.println("Compliant: used buffer, buffer2[0] = " + buffer2[0]);
        } // buffer2 goes out of scope here and can be collected
        System.out.println("Compliant: buffer2 is out of scope, nothing extra needed");
    }
}
