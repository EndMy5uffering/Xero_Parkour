package com.parkour.map;

import java.util.List;

public interface CommandUser {

	public void addCommand(Command cmd);
	
	public void addCommandNoSave(Command cmd);
	
	public Command getCommand(int i);
	
	public Command getCommand(String command);
	
	public void removeCommand(Command cmd);
	
	public void removeCommand(int i);
	
	public List<Command> getCommands();
	
	public void setCommands(List<Command> commands);
	
	public void clearCommands();
	
}
