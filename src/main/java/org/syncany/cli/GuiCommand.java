/*
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2014 Philipp C. Heckel <philipp.heckel@gmail.com> 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.cli;

import org.syncany.operations.OperationOptions;
import org.syncany.operations.OperationResult;
import org.syncany.operations.gui.GuiOperation;


/**
 * @author vwiencek
 *
 */
public class GuiCommand extends Command {

	@Override
	public int execute(String[] operationArgs) throws Exception {
		GuiOperation operation = new GuiOperation(null);
		operation.execute();
		return 0;
	}

	@Override
	public CommandScope getRequiredCommandScope() {
		return CommandScope.ANY;
	}

	@Override
	public boolean canExecuteInDaemonScope() {
		return false;
	}

	@Override
	public OperationOptions parseOptions(String[] operationArgs) throws Exception {
		return null;
	}

	@Override
	public void printResults(OperationResult result) {
		// TODO Auto-generated method stub
	}
}
