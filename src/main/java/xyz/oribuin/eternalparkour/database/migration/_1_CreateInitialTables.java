package xyz.oribuin.eternalparkour.database.migration;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class _1_CreateInitialTables extends DataMigration {

    public _1_CreateInitialTables() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {
        final String createTimesTable = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "data (`player` VARCHAR(36), " +
                "`level` TEXT," +
                "`completed` INT, " +
                "`username` TEXT, " +
                "`attempts` INT, " +
                "`bestTime` LONG, " +
                "`bestTimeAchieved` LONG, " +
                "`lastTime` LONG, " +
                "`lastCompletion` LONG, " +
                "`totalTimes` TEXT, " +
                "PRIMARY KEY(`player`, `level`))";

        try (PreparedStatement statement = connection.prepareStatement(createTimesTable)) {
            statement.executeUpdate();
        }

    }

}
