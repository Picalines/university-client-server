package practice2;

import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private Server() {
    }

    public static void main(String[] args) {
        try {
            var registry = LocateRegistry.createRegistry(1025);

            var solver = new EquationSolverImpl();
            var solverStub = (EquationSolver) UnicastRemoteObject.exportObject(solver, 0);
            registry.bind("EquationSolver", solverStub);

            System.out.println("Server is ready");
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            System.out.println("Exception on the server: " + e.toString());
            e.printStackTrace();
        }
    }
}
