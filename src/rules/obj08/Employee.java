/**
 * Demonstrates OBJ08-J by preventing a nested class from
 * exposing a private member of its outer class.
 */
public class Employee {

    private final double salary;

    /**
     * Creates an employee with a private salary value.
     *
     * @param salary employee's salary
     */
    public Employee(double salary) {
        this.salary = salary;
    }

    /**
     * Private nested class used only for internal salary processing.
     */
    private class SalaryDetails {

        /**
         * Returns salary information for internal use only.
         *
         * @return the employee's private salary
         */
        private double getSalary() {
            return salary;
        }
    }

    /**
     * Uses the nested class internally without exposing it.
     */
    private void processSalaryInternally() {
        SalaryDetails details = new SalaryDetails();

        System.out.println(
            "Salary processed internally: $" + details.getSalary()
        );
    }

    /**
     * Demonstrates internal use of the private nested class.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Employee employee = new Employee(65000.00);

        employee.processSalaryInternally();

        // Outside classes cannot create Employee.SalaryDetails
        // because the nested class is private.
    }
}