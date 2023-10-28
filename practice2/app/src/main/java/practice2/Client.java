package practice2;

import java.rmi.registry.LocateRegistry;

public class Client {
    private Client() {
    }

    public static void main(String[] args) {
        try {
            var registry = LocateRegistry.getRegistry(1025);

            var solverStub = (EquationSolver) registry.lookup("EquationSolver");

            var roots = solverStub.solve(1, 10, 3);
            System.out.println("Root of the equation is: " + roots.toString());
        } catch (Exception e) {
            System.out.println("Exception on client: " + e.toString());
            e.printStackTrace();
        }
    }
}
