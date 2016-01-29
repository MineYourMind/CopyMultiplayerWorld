package de.promolitor.copymultiplayerworld;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandHandler extends CommandBase {
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	};

	@Override
	public String getCommandName() {
		return "cmw";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/cmw dl savename";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 2) {
			if (args[0].equals("dl")) {
				Download.getMCAFiles(5, args[1]);
				return;

			}

		}
		throw new WrongUsageException("/cmw dl savename");
	}

}
