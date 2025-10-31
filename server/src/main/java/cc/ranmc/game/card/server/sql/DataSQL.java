package cc.ranmc.game.card.server.sql;

public class DataSQL extends SQLBase {

    public DataSQL(String path) {
        super(path);
    }

    /**
     * 新增数据库表
     */
    @Override
    public void createTable() {
        runCommand("CREATE TABLE Player " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " Name TEXT NOT NULL," +
                " Email INTEGER," +
                " Password TEXT," +
                " Money INTEGER)");
    }

}
