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

import cs350f20project.controller.cli.parser.BehavioralParser;
import cs350f20project.controller.cli.parser.CreationalParser;
import cs350f20project.controller.cli.parser.StructuralParser;
import cs350f20project.controller.cli.parser.MetaParser;

public class CommandParser {
  private A_ParserHelper parserHelper;
  private String text;
  private BehavioralParser behavioralParser;
  private CreationalParser creationalParser;
  private StructuralParser structuralParser;
  private MetaParser metaParser;

  public CommandParser(MyParserHelper parserHelper, String text) {
    this.parserHelper = parserHelper;
    this.text = text;
    this.behavioralParser = new BehavioralParser(parserHelper);
    this.creationalParser = new CreationalParser(parserHelper);
    this.structuralParser = new StructuralParser(parserHelper);
    this.metaParser = new MetaParser(parserHelper);
  }

  public void parse() {
    if (text.length() > 0) {
      for (String cmd : text.split(";")) {
        // Replace all whitespace with single space for easier parsing
        cmd.replaceAll("\\s+", " ").trim();

	// Handle behavioral commands
        if (cmd.toUpperCase().startsWith("DO ")) {
          behavioralParser.parseDoCmds(cmd.substring(3));

        // Handle creational commands
        } else if (cmd.toUpperCase().startsWith("CREATE ")) {
          creationalParser.parseCreateCmds(cmd.substring(7));

	// Handle metacommands
        } else if (cmd.startsWith("@")) {
          metaParser.parseMetaCmds(cmd);

	// Handle structural commands
        } else {
          structuralParser.parseStructCmds(cmd);
        }
      }
    }
  }
}
