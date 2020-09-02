/*
 * Copyright 2020 Matt Jamesson <scifidryer@gmail.com>.
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

package protocolwhisperer;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class ProtocolWhisperer {

    /**
     * @param args the command line arguments
     */
    public static boolean debug = false;
    public static void main(String[] args) {
        boolean startGui = true;
        if (args.length > 0)
        {
            for (int i = 0; i < args.length; i++)
            {
                if (args[i].equals("-debug"))
                {
                    debug = true;
                }
                if (args[i].equals("-bridge"))
                {
                    startGui = false;
                    new BridgeManager(true, args[i+1]);
                }
            }
        }
        if (startGui)
        {
            new BridgeManager(false, "");
        }
    }
    
}
