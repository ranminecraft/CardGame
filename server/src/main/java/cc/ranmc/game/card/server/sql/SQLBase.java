package cc.ranmc.game.card.server.sql;


import cc.ranmc.game.card.server.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLBase {

    protected Connection connection;

    public SQLBase(String path) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+ path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createTable();
    }

    /**
     * 关闭数据库连接
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 新增数据库表
     */
    public void createTable() {}

    /**
     * 新增数据库
     * @param table 表
     * @param data 内容
     */
    public int insert(String table, SQLRow data) {
        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();
        for (String key : data.keySet()) {
            name.append(key);
            name.append(",");
            value.append("?,");
        }
        if (!name.isEmpty()) name.deleteCharAt(name.length() - 1);
        if (!value.isEmpty()) value.deleteCharAt(value.length() - 1);
        String command = "INSERT INTO " + table.toUpperCase() + " ("+name+") VALUES (" + value + ");";
        try {
            PreparedStatement statement = connection.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            for (String key : data.keySet()) {
                statement.setObject(i, data.getObject(key));
                i++;
            }
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            Main.getLogger().error("数据库错误{}\n{}", e.getMessage(), command);
        }
        return -1;
    }

    /**
     * 查询表数据
     * @param table 表
     * @param filter 数据
     * @return 数据
     */
    public SQLRow selectRow(String table, SQLFilter filter) {
        return queryMap("SELECT * FROM " + table.toUpperCase() + filter.getResult());
    }

    public SQLRow selectRow(String table) {
        return queryMap("SELECT * FROM " + table.toUpperCase());
    }

    public List<SQLRow> selectList(String table, SQLFilter filter) {
        return queryList("SELECT * FROM " + table.toUpperCase() + filter.getResult());
    }

    public List<SQLRow> selectList(String table) {
        return queryList("SELECT * FROM " + table.toUpperCase());
    }

    public int selectCount(String table) {
        return queryMap("SELECT COUNT(*) FROM " + table)
                .getInt("COUNT(*)", 0);
    }

    public int selectCount(String table, SQLFilter filter) {
        return queryMap("SELECT COUNT(*) FROM " + table.toUpperCase() + filter.getResult())
                .getInt("COUNT(*)", 0);
    }

    /**
     * 分析数据
     * @param command 命令
     * @return 数据
     */
    protected SQLRow queryMap(String command) {
        SQLRow data = new SQLRow();
        ResultSet rs = null;
        try {
            rs = connection.createStatement().executeQuery(command);
            if (!rs.isClosed()) {
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    if (rs.getString(i) != null) {
                        data.set(md.getColumnName(i), rs.getObject(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    protected List<SQLRow> queryList(String command) {
        List<SQLRow> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = connection.createStatement().executeQuery(command);
            while (rs.next()) {
                if (!rs.isClosed()) {
                    SQLRow data = new SQLRow();
                    ResultSetMetaData md = rs.getMetaData();
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        if (rs.getString(i) != null) {
                            data.set(md.getColumnName(i), rs.getObject(i));
                        }
                    }
                    list.add(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 更新表数据
     * @param table 表
     * @param filter 数据
     */
    public void update(String table, SQLFilter filter) {
        runCommand("UPDATE " + table.toUpperCase() + filter.getResult());
    }

    /**
     * 删除表数据
     * @param table 表
     * @param id 编号
     */
    public void delete(String table, int id) {
        runCommand("DELETE FROM " + table.toUpperCase() + " WHERE ID = " + id);
    }

    public void delete(String table, SQLFilter filter) {
        runCommand("DELETE FROM " + table.toUpperCase() + filter.getResult());
    }

    /**
     * 执行数据库指令
     * @param command 命令
     */
    public void runCommand(String command) {
        try {
            connection.createStatement().executeUpdate(command);
        } catch (SQLException e) {
            if (!command.contains("CREATE TABLE")) {
                Main.getLogger().error("数据库错误{}\n{}", e.getMessage(), command);
            }
        }
    }

}

