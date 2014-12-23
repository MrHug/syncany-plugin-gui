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
package org.syncany.operations.daemon;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.syncany.config.LocalEventBus;
import org.syncany.operations.daemon.messages.ConfirmUserInteractionExternalEvent;
import org.syncany.operations.daemon.messages.ConfirmUserInteractionExternalManagementRequest;
import org.syncany.operations.daemon.messages.GetPasswordUserInteractionExternalEvent;
import org.syncany.operations.daemon.messages.GetPasswordUserInteractionExternalManagementRequest;
import org.syncany.plugins.UserInteractionListener;

import com.google.common.eventbus.Subscribe;

public class EventUserInteractionListener implements UserInteractionListener {
	private static final Logger logger = Logger.getLogger(EventUserInteractionListener.class.getSimpleName());

	private LocalEventBus eventBus;
	private Object waitObject;
	private Object userResponse;
	
	public EventUserInteractionListener() {
		this.eventBus = LocalEventBus.getInstance();
		this.eventBus.register(this);
		this.waitObject = new Object();
	}
	
	@Override
	public boolean onUserConfirm(String header, String message, String question) {
		logger.log(Level.INFO, "User confirmation needed for '" + header + "'. Sending message.");
		eventBus.post(new ConfirmUserInteractionExternalEvent(header, message, question));
		
		ConfirmUserInteractionExternalManagementRequest userConfirmation = (ConfirmUserInteractionExternalManagementRequest) waitForUserResponse();
		return userConfirmation.getResult();
	}

	@Override
	public void onShowMessage(String message) {
		// Ignore this!
	}

	@Override
	public String onUserPassword(String header, String message) {
		logger.log(Level.INFO, "User password needed. Sending message.");
		eventBus.post(new GetPasswordUserInteractionExternalEvent());
		
		GetPasswordUserInteractionExternalManagementRequest userConfirmation = (GetPasswordUserInteractionExternalManagementRequest) waitForUserResponse();
		return userConfirmation.getPassword();
	}

	@Override
	public String onUserNewPassword() {
		throw new RuntimeException("onUserNewPassword() not implemented for WebSocket init/connect.");
	}		
	
	@Subscribe
	public void onConfirmUserInteractionExternalManagementRequest(ConfirmUserInteractionExternalManagementRequest response) {
		userResponse = response;		
		fireUserResponseReady();			
	}
	
	@Subscribe
	public void onGetPasswordUserInteractionExternalManagementRequest(GetPasswordUserInteractionExternalManagementRequest response) {
		userResponse = response;		
		fireUserResponseReady();			
	}
	
	private Object waitForUserResponse() {
		try {
			synchronized (waitObject) {
				waitObject.wait();	
			}
			
			return userResponse;
		}
		catch (InterruptedException e) {
			logger.log(Level.SEVERE, "User interaction listener interrupted.", e);
			return null;
		}
	}
	
	private void fireUserResponseReady() {
		synchronized (waitObject) {
			waitObject.notify();	
		}
	}
}