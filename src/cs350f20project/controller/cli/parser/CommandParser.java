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
      StringTokenizer tokens = new StringTokenizer(text);

      if (tokens.nextToken().equalsIgnoreCase("DO")) {
        parseDoCmds(tokens);
      }
    }
  }

  public void parseDoCmds(StringTokenizer tokens) {
    if (tokens.nextToken().equalsIgnoreCase("BRAKE")) {
       String id = tokens.nextToken();
       Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*$");
       Matcher matcher = pattern.matcher(id);

      if(matcher.find()) {
        A_Command command = new CommandBehavioralBrake(id);
        parserHelper.getActionProcessor().schedule(command);
      } else {
        System.out.println("Bad ID.");
      }
    }
  }
}
