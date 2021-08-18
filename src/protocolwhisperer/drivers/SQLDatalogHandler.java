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
import java.sql.DriverManager;
import java.util.*;
import protocolwhisperer.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class SQLDatalogHandler extends DatalogHandler{
    ArrayList<String> menuNames = new ArrayList<String>();
    public String[] getMenuNames()
    {
        
        addDriverToMenu("jdbc:mysql://", "MySQL");
        addDriverToMenu("jdbc:sqlite:", "SQLite");
        String[] returnArr = new String[menuNames.size()];
        for (int i = 0; i < menuNames.size(); i++)
        {
            returnArr[i] = menuNames.get(i);
        }
        return returnArr;
    }
    public void addDriverToMenu(String url, String name)
    {
        try
        {
            DriverManager.getDriver(url);
            menuNames.add(name);
        }
        catch (Exception e)
        {
            if (ProtocolWhisperer.debug)
            {
                System.err.println("No driver found for " + name);
                e.printStackTrace();
            }
        }
    }
    public SQLDatalogRecord getNewDatalogRecord(String selectedItem)
    {
        return new SQLDatalogRecord(selectedItem);
    }
    public void configure(DatalogRecord currentRecord, BridgeManager manager)
    {
        new SQLConfigFrame((SQLDatalogRecord)currentRecord, manager).setVisible(true);
    }
}
