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

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class SQLDatalogRecord extends DatalogRecord {
    public String driverName = "";
    public String sqlHost = "";
    public String databaseName = "";
    public String sqlUser = "";
    public String sqlPassword = "";
    public String filePath = "";
    public transient boolean firstRun = true;
    public SQLDatalogRecord(String aSelectedItem)
    {
        selectedItem = aSelectedItem;
        if (selectedItem.equals("MySQL"))
        {
            driverName = "mysql";
        }
        if (selectedItem.equals("SQLite"))
        {
            driverName = "sqlite";
        }
    }
    public SQLDatalogRecord()
    {
    }
    public String getJdbcString()
    {
        if (driverName.equals("mysql"))
        {
            return "jdbc:" + driverName + "://" + sqlHost + "/" + databaseName + "?user=" + sqlUser + "&password=" + sqlPassword;
        }
        if (driverName.equals("sqlite"))
        {
            return "jdbc:" + driverName + ":" + filePath;
        }
        return "";
    }
    public Class getDatalogHandlerClass()
    {
        return SQLDatalogHandler.class;
    }
}
