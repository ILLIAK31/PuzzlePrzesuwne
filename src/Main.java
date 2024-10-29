import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main
{
    public static class PuzzlePrzesuwne extends GraphStateImpl
    {
        private byte[][] board;
        private int n , row_puste , col_puste ;

        public PuzzlePrzesuwne(int n)
        {
            this.n = n;
            board = new byte[n][n];
            row_puste = col_puste = 0;
            int liczba = 0;
            for(int i = 0; i < n; i++)
            {
                for(int j = 0; j < n; j++)
                {
                    board[i][j] = (byte)liczba++;
                }
            }
        }

        public PuzzlePrzesuwne(PuzzlePrzesuwne obj)
        {
            this.n = obj.n;
            this.row_puste = obj.row_puste;
            this.col_puste = obj.col_puste;
            this.board = new byte[n][n];
            for(int i = 0; i < this.n; i++)
            {
                for(int j = 0; j < this.n; j++)
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
            if(row_puste == 0)
            {
                return this;
            }
            PuzzlePrzesuwne copy_board = new PuzzlePrzesuwne(this);
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = copy_board.board[copy_board.row_puste+1][copy_board.col_puste];
            ++copy_board.row_puste;
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = 0;
            return copy_board;
        }

        private PuzzlePrzesuwne ruchWgore()
        {
            if(row_puste == board.length-1)
            {
                return this;
            }
            PuzzlePrzesuwne copy_board = new PuzzlePrzesuwne(this);
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = copy_board.board[copy_board.row_puste+1][copy_board.col_puste];
            ++copy_board.row_puste;
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = 0;
            return copy_board;
        }

        private PuzzlePrzesuwne ruchWlewo()
        {
            if(col_puste == board.length-1)
            {
                return this;
            }
            PuzzlePrzesuwne copy_board = new PuzzlePrzesuwne(this);
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = copy_board.board[copy_board.row_puste][copy_board.col_puste+1];
            ++copy_board.col_puste;
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = 0;
            return copy_board;
        }

        private PuzzlePrzesuwne ruchWprawo()
        {
            if(col_puste == 0)
            {
                return this;
            }
            PuzzlePrzesuwne copy_board = new PuzzlePrzesuwne(this);
            copy_board.board[copy_board.row_puste][copy_board.col_puste] = copy_board.board[copy_board.row_puste][copy_board.col_puste-1];
            --copy_board.col_puste;
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
            int num = 1;
            for (int i = 0; i < n; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    if (board[i][j] == num)
                    {
                        num = ((num+1)>((n^2)-1))?0:num+1;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            return toString().hashCode();
        }
    }

    public static void Expand(int d,GraphState s)
    {
        if(d <= 0)
        {
            System.out.println(s);
        }
        else
        {
            for(GraphState t : s.generateChildren())
            {
                if(s!=t)
                {
                    Expand(d - 1, t);
                }
            }
        }
    }

    public static void main(String[] args)
    {
        PuzzlePrzesuwne obj = new PuzzlePrzesuwne(3);
        System.out.println(obj.toString());
        List<GraphState> children = obj.generateChildren();
        for (GraphState child : children)
        {
            System.out.println(child);
        }
        System.out.println(obj.isSolution());
    }
}