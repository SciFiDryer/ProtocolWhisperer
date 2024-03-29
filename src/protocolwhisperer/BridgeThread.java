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
public class BridgeThread extends Thread{
    BridgeManager manager = null;
    public BridgeThread(BridgeManager aManager)
    {
        manager = aManager;
    }
    public synchronized void run()
    {
        manager.isRunning = true;
        if (ProtocolWhisperer.debug)
        {
            System.out.println("Starting Data Acquisition thread");
        }
        while (manager.isRunning)
        {
            try
            {
                manager.runBridge();
                wait(manager.options.restInterval);
            }
            catch (InterruptedException e)
            {
                if (protocolwhisperer.ProtocolWhisperer.debug)
                {
                    e.printStackTrace();
                }
            }
        }
        if (ProtocolWhisperer.debug)
        {
            System.out.println("Data Acquisition thread stopped");
        }
    }
}
