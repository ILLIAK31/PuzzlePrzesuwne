import sac.State;
import sac.StateFunction;
import sac.graph.AStar;
import sac.graph.BestFirstSearch;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

import java.nio.channels.Pipe;
import java.util.*;

public class Main
{
    public static class PuzzlePrzesuwne extends GraphStateImpl
    {
        private byte[][] board;
        private int n, row_puste, col_puste;

        public PuzzlePrzesuwne(int n)
        {
            this.n = n;
            board = new byte[n][n];
            row_puste = col_puste = 0;
            int liczba = 0;
            for (int i = 0; i < n; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    board[i][j] = (byte) liczba++;
                }
            }
        }

        public PuzzlePrzesuwne(PuzzlePrzesuwne obj)
        {
            this.n = obj.n;
            this.row_puste = obj.row_puste;
            this.col_puste = obj.col_puste;
            this.board = new byte[n][n];
            for (int i = 0; i < this.n; i++)
            {
                for (int j = 0; j < this.n; j++)
                {
                    this.board[i][j] = obj.board[i][j];
                }
            }
        }

        @Override
        public String toString()
        {
            StringBuilder s = new StringBuilder();
            for (byte[] bytes : board)
            {
                for (byte aByte : bytes)
                {
                    s.append(aByte);
                }
                s.append("\n");
            }
            return s.toString();
        }

        private PuzzlePrzesuwne ruchWdol() //
        {
            if (row_puste == 0)
            {
                return this;
            }
            PuzzlePrzesuwne copy_board = new PuzzlePrzesuwne(this);
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = copy_board.board[copy_board.row_puste - 1][copy_board.col_puste];
            --copy_board.row_puste;
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = 0;
            return copy_board;
        }

        private PuzzlePrzesuwne ruchWgore()
        {
            if (row_puste == board.length - 1)
            {
                return this;
            }
            PuzzlePrzesuwne copy_board = new PuzzlePrzesuwne(this);
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = copy_board.board[copy_board.row_puste + 1][copy_board.col_puste];
            ++copy_board.row_puste;
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = 0;
            return copy_board;
        }

        private PuzzlePrzesuwne ruchWlewo()
        {
            if (col_puste == 0)
            {
                return this;
            }
            PuzzlePrzesuwne copy_board = new PuzzlePrzesuwne(this);
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = copy_board.board[copy_board.row_puste][copy_board.col_puste - 1];
            --copy_board.col_puste;
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = 0;
            return copy_board;
        }

        private PuzzlePrzesuwne ruchWprawo()
        {
            if (col_puste == board.length - 1)
            {
                return this;
            }
            PuzzlePrzesuwne copy_board = new PuzzlePrzesuwne(this);
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = copy_board.board[copy_board.row_puste][copy_board.col_puste + 1];
            ++copy_board.col_puste;
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = 0;
            return copy_board;
        }

        @Override
        public List<GraphState> generateChildren()
        {
            List<GraphState> children = new ArrayList<>();
            if (ruchWgore() != null)
                children.add(ruchWgore());
            if (ruchWdol() != null)
                children.add(ruchWdol());
            if (ruchWlewo() != null)
                children.add(ruchWlewo());
            if (ruchWprawo() != null)
                children.add(ruchWprawo());
            return children;
        }
            @Override
            public boolean isSolution()
            {
                byte val = 0;
                for (byte[] row : board)
                {
                    for (byte tile : row)
                    {
                        if (tile != val)
                        {
                            return false;
                        }
                        val++;
                    }
                }
                return true;
            }

            @Override
            public int hashCode ()
            {
                return toString().hashCode();
            }

            public int getValue()
            {
                int num = 0, res = 0;
                for (int i = 0; i < n; i++)
                {
                    for (int j = 0; j < n; j++)
                    {
                        if (board[i][j] != num++ && board[i][j] != 0)
                        {
                            ++res;
                        }
                    }
                }
                return res;
            }

            public GraphState mieszaj(int n)
            {
                Random rand = new Random();
                GraphState s = this;

                for (int i = 0; i < n; i++)
                {
                    List<GraphState> l = s.generateChildren();
                    if (!l.isEmpty())
                    {
                        s = l.get(rand.nextInt(l.size()));
                    }
                }
                return s;
            }
    }
    public static class HeurystykaPusteKomorki extends StateFunction
    {
        @Override
        public double calculate(State s)
        {
            if (s instanceof PuzzlePrzesuwne) {
                PuzzlePrzesuwne ss = (PuzzlePrzesuwne) s;
                return ss.getValue();
            } else {
                return Double.NaN;
            }
        }
    }
    public static class Manhattan extends StateFunction
    {
        @Override
        public double calculate(State s)
        {
            PuzzlePrzesuwne puzzle = (PuzzlePrzesuwne) s;
            double totalDistance = 0.0;
            int n = puzzle.board.length;
            for (int i = 0; i < n; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    int value = puzzle.board[i][j];
                    if (value != 0)
                    {
                        int targetRow = (value - 1) / n;
                        int targetCol = (value - 1) % n;
                        totalDistance += Math.abs(i - targetRow) + Math.abs(j - targetCol);
                    }
                }
            }
            return totalDistance;
        }
    }
    public static void Expand(int d, GraphState s)
    {
        if (d <= 0)
        {
            System.out.println(s);
        }
        else
        {
            for (GraphState t : s.generateChildren())
            {
                if (s != t)
                {
                    Expand(d - 1, t);
                }
            }
        }
    }
    public static void main(String[] args)
    {
        int problems = 100;
        int mix = 3;

        int astar_dur = 0, astar_dur_m = 0 , bfs_dur = 0 , bfs_dur_m = 0;
        int astar_opened = 0 , astar_opened_m = 0 , bfs_opened = 0 , bfs_opened_m = 0;
        int astar_closed = 0 , astar_closed_m = 0 , bfs_closed = 0 , bfs_closed_m = 0;

        for (int i = 0; i < problems; i++)
        {
            PuzzlePrzesuwne puzzle = new PuzzlePrzesuwne(3);
            GraphState s = puzzle.mieszaj(mix);

            AStar aStar = new AStar();

            PuzzlePrzesuwne.setHFunction(new HeurystykaPusteKomorki());
            aStar.setInitial(s);
            aStar.execute();
            astar_dur += aStar.getDurationTime();
            astar_opened += aStar.getOpenSet().size();
            astar_closed += aStar.getClosedStatesCount();

            PuzzlePrzesuwne.setHFunction(new Manhattan());
            aStar.setInitial(s);
            aStar.execute();
            astar_dur_m += aStar.getDurationTime();
            astar_opened_m += aStar.getOpenSet().size();
            astar_closed_m += aStar.getClosedStatesCount();

            BestFirstSearch bfs = new BestFirstSearch();

            PuzzlePrzesuwne.setHFunction(new HeurystykaPusteKomorki());
            bfs.setInitial(s);
            bfs.execute();
            bfs_dur += bfs.getDurationTime();
            bfs_opened += bfs.getOpenSet().size();
            bfs_closed += bfs.getClosedStatesCount();

            PuzzlePrzesuwne.setHFunction(new Manhattan());
            bfs.setInitial(s);
            bfs.execute();
            bfs_dur_m += bfs.getDurationTime();
            bfs_closed_m += bfs.getClosedStatesCount();
            bfs_opened_m += bfs.getOpenSet().size();
        }

        System.out.println("A* MTH :");
        System.out.println("Duration : " + (astar_dur / problems));
        System.out.println("Opened : " + (astar_opened / problems));
        System.out.println("Closed : " + (astar_closed / problems));


        System.out.println("A* M :");
        System.out.println("Duration : " + (astar_dur_m / problems));
        System.out.println("Opened : " + (astar_opened_m / problems));
        System.out.println("Closed : " + (astar_closed_m / problems));

        System.out.println("BFS MTH :");
        System.out.println("Duration : " + (bfs_dur / problems));
        System.out.println("Opened : " + (bfs_opened / problems));
        System.out.println("Closed : " + (bfs_closed / problems));

        System.out.println("BFS M : ");
        System.out.println("Duration : " + (bfs_dur_m / problems));
        System.out.println("Opened : " + (bfs_opened_m / problems));
        System.out.println("Closed : " + (bfs_closed_m / problems));

    }
}