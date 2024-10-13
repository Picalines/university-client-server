package practice2;

import java.rmi.RemoteException;

public class EquationSolverImpl implements EquationSolver {
    @Override
    public EquationRoots solve(double a, double b, double c) throws RemoteException {
        double Dsqrt = Math.sqrt(b * b - a * c * 4);

        if (Double.isNaN(Dsqrt)) {
            return null;
        }

        return new EquationRoots(
            (-b + Dsqrt) / (a * 2),
            (-b - Dsqrt) / (a * 2)
        );
    }
}
