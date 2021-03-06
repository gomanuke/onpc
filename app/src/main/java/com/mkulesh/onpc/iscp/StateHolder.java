/*
 * Copyright (C) 2019. Mikhail Kulesh
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General
 * Public License along with this program.
 */

package com.mkulesh.onpc.iscp;

import com.mkulesh.onpc.utils.Logging;

import java.util.concurrent.atomic.AtomicBoolean;

public class StateHolder
{
    private StateManager stateManager = null;
    private final AtomicBoolean released = new AtomicBoolean();
    private boolean appExit = false;

    public StateHolder()
    {
        released.set(true);
    }

    public void setStateManager(StateManager stateManager)
    {
        this.stateManager = stateManager;
        synchronized (released)
        {
            released.set(stateManager == null);
        }
    }

    public StateManager getStateManager()
    {
        return stateManager;
    }

    public State getState()
    {
        return stateManager == null ? null : stateManager.getState();
    }

    public void release(boolean appExit)
    {
        this.appExit = appExit;
        synchronized (released)
        {
            if (stateManager != null)
            {
                Logging.info(this, "request to release state holder");
                released.set(false);
                stateManager.stop();
            }
            else
            {
                released.set(true);
            }
        }
    }

    public void waitForRelease()
    {
        while (true)
        {
            synchronized (released)
            {
                if (released.get())
                {
                    Logging.info(this, "state holder released");
                    return;
                }
            }
        }
    }

    public boolean isAppExit()
    {
        return appExit;
    }
}
