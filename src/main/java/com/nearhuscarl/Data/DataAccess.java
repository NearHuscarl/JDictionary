package com.nearhuscarl.Data;

import com.nearhuscarl.Helpers.JsonUtil;
import com.nearhuscarl.Helpers.Result;
import com.nearhuscarl.Helpers.ResultInfo;
import com.nearhuscarl.Helpers.Status;
import com.nearhuscarl.Models.History;
import com.nearhuscarl.Models.Word;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DataAccess {
    private Connection connection = null;

    private void openConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:src\\main\\java\\com\\nearhuscarl\\Data\\words.sqlite");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Result selectIDs() {
        ArrayList<String> ids = new ArrayList<>();

        try
        {
            openConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(5); // seconds

            var result = statement.executeQuery("" +
                    "SELECT [ID]" +
                    "FROM [Dictionary]" +
                    "ORDER BY [ID] ASC");

            while(result.next())
            {
                ids.add(result.getString("ID"));
            }
        }
        catch(SQLException e)
        {
            System.err.println(e.getMessage());
            return new Result<>(new ResultInfo(Status.Failed, e.getMessage(), e), ids);
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                System.err.println(e.getMessage());
                return new Result<>(new ResultInfo(Status.Failed, e.getMessage(), e), ids);
            }
        }
        return new Result<>(new ResultInfo(Status.Success), ids);
    }

    public Result selectNames() {
        ArrayList<String> wordList = new ArrayList<>();

        try
        {
            openConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(5); // seconds

            var result = statement.executeQuery("" +
                    "SELECT [Name]" +
                    "FROM [Dictionary]" +
                    "ORDER BY [ID] ASC");

            while(result.next())
            {
                wordList.add(result.getString("Name"));
            }
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
            return new Result<>(new ResultInfo(Status.Failed, e.getMessage(), e), wordList);
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
                return new Result<>(new ResultInfo(Status.Failed, e.getMessage(), e), wordList);
            }
        }
        return new Result<>(new ResultInfo(Status.Success), wordList);
    }

    public Result selectDefinitionFrom(String id) {
        ArrayList<Word> words = new ArrayList<>();

        try
        {
            openConnection();
            // create a database connection
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(5); // seconds

            var result = statement.executeQuery("" +
                    "SELECT *" +
                    "FROM [Dictionary] WHERE" +
                    "[ID] = '" + id + "' OR [ID] LIKE '" + id + "\\__' ESCAPE '\\'");

            while(result.next())
            {
                Word word = JsonUtil.fromJson(result.getString("Definition"), Word.class);
                words.add(word);
            }
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
            return new Result<>(new ResultInfo(Status.Failed, e.getMessage(), e), words);
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
                return new Result<>(new ResultInfo(Status.Failed, e.getMessage(), e), words);
            }
        }
        return new Result<>(new ResultInfo(Status.Success), words);
    }
}
