package cs350f20project.controller.cli.parser;

import cs350f20project.datatype.*;
import cs350f20project.support.*;
import cs350f20project.controller.*;
import cs350f20project.controller.cli.*;
import cs350f20project.controller.command.*;
import cs350f20project.controller.command.creational.*;
import cs350f20project.controller.command.structural.*;
import cs350f20project.controller.command.behavioral.*;
import cs350f20project.controller.command.meta.*;
import cs350f20project.controller.timing.*;

import java.io.*;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs350f20project.controller.cli.parser.HelperMethods;

public class BehavioralParser {
  private A_ParserHelper parserHelper;

  public BehavioralParser(MyParserHelper parserHelper) {
    this.parserHelper = parserHelper;
  }

  public void parseDoCmds(String cmd) {
    String id;
    // Matches ID with nothing after it.
    Pattern pattern = Pattern.compile("^[_a-zA-Z]+\\w*$");
    Matcher matcher;

    if (cmd.toUpperCase().startsWith("BRAKE ")) {
      id = cmd.substring(6);
      matcher = pattern.matcher(id);

      if (matcher.find()) {
        A_Command command = new CommandBehavioralBrake(id);
        parserHelper.getActionProcessor().schedule(command);
      } else {
        System.out.println("Bad ID.");
      }
    } else if (cmd.toUpperCase().startsWith("SELECT ")) {
      parseDoSelectCmds(cmd.substring(7));
    } else if (cmd.toUpperCase().startsWith("SET ")) {
      parseDoSetCmds(cmd.substring(4));
    }
  }

  private void parseDoSelectCmds(String cmd) {
    String id;
    Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*$");
    Matcher matcher;

    if (cmd.toUpperCase().startsWith("SWITCH ")) {
      cmd = cmd.substring(7);
      id = cmd.substring(0, cmd.indexOf(" "));
      matcher = pattern.matcher(id);

      if(!matcher.find()) {
        System.out.println("Bad ID.");
	return;
      }

      cmd = cmd.substring(cmd.indexOf(" ") + 1);

      if (cmd.toUpperCase().startsWith("PATH ")) {
        cmd = cmd.substring(5);

        if (cmd.toUpperCase().endsWith("PRIMARY")) {
          A_Command command = new CommandBehavioralSelectSwitch(id, true);
          parserHelper.getActionProcessor().schedule(command);
	} else if (cmd.toUpperCase().endsWith("SECONDARY")) {
          A_Command command = new CommandBehavioralSelectSwitch(id, false);
          parserHelper.getActionProcessor().schedule(command);
        }
      }
    }
  }

  private void parseDoSetCmds(String cmd) {
    String id;
    Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*$");
    Matcher matcher;

    if (cmd.toUpperCase().startsWith("REFERENCE ")) {
      cmd = cmd.substring(10);
      if (cmd.toUpperCase().startsWith("ENGINE ")) {
         cmd = cmd.substring(7);
         id = cmd;
         matcher = pattern.matcher(id);

         if (matcher.find()) {
           A_Command command = new CommandBehavioralSetReference(id);
           parserHelper.getActionProcessor().schedule(command);
         } else {
           System.out.println("Bad ID.");
         }
      }
    } else {
      id = cmd.substring(0, cmd.indexOf(" "));
      cmd = cmd.substring(id.length() + 1);
      matcher = pattern.matcher(id);

      if (!matcher.find()) {
        System.out.println("Bad ID.");
        return;
      }

      if (cmd.toUpperCase().startsWith("DIRECTION ")) {
        cmd = cmd.substring(10);

        if (cmd.toUpperCase().endsWith("FORWARD")) {
          A_Command command = new CommandBehavioralSetDirection(id, true);
          parserHelper.getActionProcessor().schedule(command);
        } else if (cmd.toUpperCase().endsWith("BACKWARD")) {
          A_Command command = new CommandBehavioralSetDirection(id, false);
          parserHelper.getActionProcessor().schedule(command);
        }
      } else if (cmd.toUpperCase().startsWith("SPEED ")) {
        cmd = cmd.substring(6);
        String number = cmd;
        matcher = pattern.matcher(id);
        if (matcher.find()) {
          A_Command command = new CommandBehavioralSetSpeed(id, Double.parseDouble(number));
          parserHelper.getActionProcessor().schedule(command);
        } else {
          System.out.println("Bad ID");
        }
      }
    }
  }
}
