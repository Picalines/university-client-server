package practice2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EquationSolver extends Remote {
    EquationRoots solve(double a, double b, double c) throws RemoteException;
}
