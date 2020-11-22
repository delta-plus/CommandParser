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

public class CommandParser {
  private A_ParserHelper parserHelper;
  private ActionProcessor actionProcessor;
  private String text;

  public CommandParser(MyParserHelper parserHelper, String text) {
    this.parserHelper = parserHelper;
    this.text = text;
  }

  public void parse() {
    if (text.length() > 0) {
      for (String cmd : text.split(";")) {
        StringTokenizer tokens = new StringTokenizer(cmd);
        String keyword;

	if (tokens.hasMoreTokens()) {
          keyword = tokens.nextToken();
	} else {
          continue;
        }
        
	// Handle behavioral commands
        if (keyword.equalsIgnoreCase("DO")) {
          parseDoCmds(tokens);

        // Handle creational commands
        } else if (keyword.equalsIgnoreCase("CREATE")) {
          parseCreateCmds(tokens);

	// Handle metacommands
        } else if (keyword.startsWith("@")) {
          parseMetaCmds(tokens);

	// Handle structural commands
        } else {
          parseStructCmds(tokens);
        }
      }
    }
  }

  public void parseDoCmds(StringTokenizer tokens) {
    String keyword;

    if (tokens.hasMoreTokens()) {
      keyword = tokens.nextToken();
    } else {
      return;
    }

    if (keyword.equalsIgnoreCase("BRAKE")) {
      String id;

      if (tokens.hasMoreTokens()) {
        id = tokens.nextToken();
      } else {
        return;
      }

      Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*$");
      Matcher matcher = pattern.matcher(id);

      if(matcher.find()) {
        A_Command command = new CommandBehavioralBrake(id);
        parserHelper.getActionProcessor().schedule(command);
      } else {
        System.out.println("Bad ID.");
      }
    } else if (keyword.equalsIgnoreCase("SELECT")) {
      parseDoSelectCmds(tokens);
    }
  }

  public void parseDoSelectCmds(StringTokenizer tokens) {
    ;
  }

  public void parseCreateCmds(StringTokenizer tokens) {
    ;
  }

  public void parseMetaCmds(StringTokenizer tokens) {
    ;
  }
  
  public void parseStructCmds(StringTokenizer tokens) {
    ;
  }
}
