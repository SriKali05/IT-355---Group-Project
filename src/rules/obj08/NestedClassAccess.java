package rules.obj08;

/**
 * Demonstrates OBJ08-J by preventing a nested class from
 * exposing a private member of its outer class.
 */
public class NestedClassAccess {

    /**
     * Demonstrates internal use of the private nested class.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Employee employee = new Employee(65000.00);

        employee.processSalary();

        // Outside classes cannot create Employee.SalaryDetails
        // because the nested class is private.
    }
}