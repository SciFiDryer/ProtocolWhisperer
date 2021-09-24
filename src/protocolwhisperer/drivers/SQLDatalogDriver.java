/*
 * Copyright 2021 Matt Jamesson <scifidryer@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package protocolwhisperer.drivers;
import java.sql.*;
import protocolwhisperer.*;
import java.util.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class SQLDatalogDriver extends DatalogDriver {
    Connection connection = null;
    int autoIncrementFlag = 1;
    String monthcode = "";
    public Class getDatalogRecordClass()
    {
        return SQLDatalogRecord.class;
    }
    public Class getDatalogHandlerClass()
    {
        return SQLDatalogHandler.class;
    }
    public boolean monthCodeChanged()
    {
        Calendar now = Calendar.getInstance();
        now.setTime(new java.util.Date());
        String month = (now.get(Calendar.MONTH)+1) + "";
        if (month.length() == 1)
        {
            month = "0" + month;
        }
        String newmonthcode = now.get(Calendar.YEAR) + month;
        if (newmonthcode.equals(monthcode))
        {
            return false;
        }
        monthcode = newmonthcode;
        return true;
    }
    public void logPoints()
    {
        boolean monthcodechanged = monthCodeChanged();
        for (int i = 0; i < recordList.size(); i++)
        {
            SQLDatalogRecord currentRecord = (SQLDatalogRecord)recordList.get(i);
            if (monthcodechanged)
            {
                firstRun(currentRecord);
            }
            logPoint(currentRecord);
        }
    }
    public void firstRun(SQLDatalogRecord currentRecord)
    {
        
        try
        {
            connection = DriverManager.getConnection(currentRecord.getJdbcString());
            Statement statement = connection.createStatement();
            if (currentRecord.driverName.equals("sqlite"))
            {
                statement.execute("CREATE TABLE IF NOT EXISTS points ( tag VARCHAR(30) NOT NULL , pointid INTEGER PRIMARY KEY );");
            }
            else
            {
                statement.execute("CREATE TABLE IF NOT EXISTS points ( tag VARCHAR(30) NOT NULL , pointid INT NOT NULL AUTO_INCREMENT , PRIMARY KEY (pointid))");
            }
            String indexFlag = ", INDEX (pointid)";
            if (currentRecord.driverName.equals("sqlite"))
            {
                indexFlag = "";
            }
            statement.execute("CREATE TABLE IF NOT EXISTS datalog" + monthcode + " (pointid INT NOT NULL , time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP , value FLOAT NOT NULL" + indexFlag + ");");
            connection.close();
            currentRecord.firstRun = false;
        }
        catch (Exception e)
        {
            if (ProtocolWhisperer.debug)
            {
                e.printStackTrace();
            }
        }
    }
    public void logPoint(SQLDatalogRecord currentRecord)
    {
        if (true)
        {
            try
            {
                connection = DriverManager.getConnection(currentRecord.getJdbcString());
                Statement statement = connection.createStatement();
                for (int i = 0; i < currentRecord.points.size(); i++)
                {
                    DatalogPoint point = currentRecord.points.get(i);
                    if (point.lastLogged + currentRecord.logInterval < System.currentTimeMillis())
                    {
                        if (point.pointId == 0)
                        {
                            ResultSet rs = statement.executeQuery("SELECT pointid FROM points WHERE tag = \"" + point.tagRecord.tag + "\";");
                            if (rs.next())
                            {
                                point.pointId = rs.getInt(1);
                            }
                            else
                            {
                                statement.execute("INSERT INTO points (tag) VALUES (\"" + point.tagRecord.tag + "\");");
                                rs = statement.executeQuery("SELECT pointid FROM points WHERE tag = \"" + point.tagRecord.tag + "\";");
                                if (rs.next())
                                {
                                    point.pointId = rs.getInt(1);
                                }
                            }
                        }
                        if (point.pointId != 0)
                        {
                            statement.execute("INSERT INTO datalog" + monthcode + " (pointid, value) VALUES (\"" + point.pointId + "\", \"" + point.tagRecord.getValue() + "\");");
                            point.lastLogged = System.currentTimeMillis();
                        }
                    }
                }
                connection.close();
            }
            catch (Exception e)
            {
                if (ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
