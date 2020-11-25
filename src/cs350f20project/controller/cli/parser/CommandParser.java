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
        // Replace all whitespace with single space for easier parsing
        cmd.replaceAll("\\s+", " ").trim();

	// Handle behavioral commands
        if (cmd.toUpperCase().startsWith("DO ")) {
          parseDoCmds(cmd.substring(3));

        // Handle creational commands
        } else if (cmd.toUpperCase().startsWith("CREATE ")) {
          parseCreateCmds(cmd.substring(7));

	// Handle metacommands
        } else if (cmd.startsWith("@")) {
          parseMetaCmds(cmd);

	// Handle structural commands
        } else {
          parseStructCmds(cmd);
        }
      }
    }
  }

  public void parseDoCmds(String cmd) {
    String id;
    // Matches ID with nothing after it.
    Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*$");
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
    }
  }

  public void parseDoSelectCmds(String cmd) {
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

  public void parseCreateCmds(String cmd) {
    if (cmd.toUpperCase().startsWith("POWER ")) {
      parseCreatePowerCmds(cmd.substring(6));
    }
  }

  public void parseCreatePowerCmds(String cmd) {
    String id;
    StringTokenizer potentialIds;
    List<String> ids = new ArrayList<String>();
    List<Double> latitude = new ArrayList<Double>();
    List<Double> longitude = new ArrayList<Double>();
    double deltaNum1;
    double deltaNum2;
    Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*$");
    Matcher matcher;

    if (cmd.toUpperCase().startsWith("CATENARY ")) {
      cmd = cmd.substring(9);
      id = cmd.substring(0, cmd.indexOf(" "));
      matcher = pattern.matcher(id);

      if(!matcher.find()) {
        System.out.println("Bad ID.");
	return;
      }

      cmd = cmd.substring(cmd.indexOf(" ") + 1);

      if (cmd.toUpperCase().startsWith("WITH POLES ")) {
        cmd = cmd.substring(11);
        potentialIds = new StringTokenizer(cmd);

	while (potentialIds.hasMoreTokens()) {
          id = potentialIds.nextToken();
          matcher = pattern.matcher(id);

	  if (matcher.find()) {
            ids.add(id);
          }
        }

	if (ids.size() > 0) {
          A_Command command = new CommandCreatePowerCatenary(id, ids);
          parserHelper.getActionProcessor().schedule(command);         
        } else {
          System.out.println("Bad ID.");
	  return;
        }
      }
    } else if (cmd.toUpperCase().startsWith("STATION ")) {
      cmd = cmd.substring(8);
      id = cmd.substring(0, cmd.indexOf(" "));
      matcher = pattern.matcher(id);

      if(!matcher.find()) {
        System.out.println("Bad ID.");
	return;
      }

      cmd = cmd.substring(cmd.indexOf(" ") + 1);

      if (cmd.toUpperCase().startsWith("REFERENCE ")) {
        cmd = cmd.substring(10);

	if (cmd.startsWith("$")) {
          ; // Need to add code to for setting, checking, and retrieving IDs
        } else {
          latitude = parseLatOrLong(cmd.substring(0, cmd.indexOf("\"")).replaceAll("\\s+", ""));
          longitude = parseLatOrLong(cmd.substring(cmd.indexOf("/") + 1, cmd.indexOf("\"", cmd.indexOf("\"") + 1)).replaceAll("\\s+", ""));
        }

	cmd = cmd.substring(cmd.indexOf("\"", cmd.indexOf("\"") + 1) + 2);

	if (cmd.toUpperCase().startsWith("DELTA ")) {
          cmd = cmd.substring(6);

          deltaNum1 = parseNumber(cmd.substring(0, cmd.indexOf(":")).trim());
          deltaNum2 = parseNumber(cmd.substring(cmd.indexOf(":") + 1, cmd.toUpperCase().indexOf("WITH")).trim());

	  cmd = cmd.substring(cmd.toUpperCase().indexOf("WITH "));
	  cmd = cmd.substring(5);

	  if (cmd.toUpperCase().startsWith("SUBSTATION ")) {
            cmd = cmd.substring(11);

            potentialIds = new StringTokenizer(cmd);

	    while (potentialIds.hasMoreTokens()) {
              id = potentialIds.nextToken();
              matcher = pattern.matcher(id);

	      if (matcher.find()) {
                ids.add(id);
              }
            }

	    if (ids.size() > 0) {
              A_Command command = new CommandCreatePowerStation(id, 
			            new CoordinatesWorld(
			              new Latitude(latitude.get(0).intValue(), latitude.get(1).intValue(), latitude.get(2)), 
			              new Longitude(longitude.get(0).intValue(), longitude.get(1).intValue(), longitude.get(2))), 
				    new CoordinatesDelta(deltaNum1, deltaNum2), 
				    ids);
              parserHelper.getActionProcessor().schedule(command);         
            } else {
              System.out.println("Bad ID.");
	      return;
            }
          } else if (cmd.toUpperCase().startsWith("SUBSTATIONS ")) {
            cmd = cmd.substring(12);

            potentialIds = new StringTokenizer(cmd);

	    while (potentialIds.hasMoreTokens()) {
              id = potentialIds.nextToken();
              matcher = pattern.matcher(id);

	      if (matcher.find()) {
                ids.add(id);
              }
            }

	    if (ids.size() > 0) {
              A_Command command = new CommandCreatePowerStation(id, 
			            new CoordinatesWorld(
			              new Latitude(latitude.get(0).intValue(), latitude.get(1).intValue(), latitude.get(2)), 
			              new Longitude(longitude.get(0).intValue(), longitude.get(1).intValue(), longitude.get(2))), 
				    new CoordinatesDelta(deltaNum1, deltaNum2), 
				    ids);
              parserHelper.getActionProcessor().schedule(command);         
            } else {
              System.out.println("Bad ID.");
	      return;
            }
          } else {
            System.out.println("Bad command.");
	    return;
          }
        } else {
          System.out.println("Bad command.");
	  return;
        }
      }
    }
  }

  public void parseMetaCmds(String cmd) {
    ;
  }

  public void parseStructCmds(String cmd) {
    ;
  }

  public int parseInteger(String str) {
    Pattern pattern = Pattern.compile("^[+-]{0,1}\\d+$");
    Matcher matcher = pattern.matcher(str);

    if (matcher.find()) {
      return Integer.parseInt(str);
    } else {
      throw new IllegalArgumentException("Bad integer.");
    }
  }

  public double parseReal(String str) {
    Pattern pattern = Pattern.compile("^[+-]{0,1}0+\\d*\\.\\d+$");
    Matcher matcher = pattern.matcher(str);

    if (matcher.find()) {
      return Double.parseDouble(str);
    } else {
      throw new IllegalArgumentException("Bad real.");
    }
  }

  public double parseNumber(String str) {
    try {
      return Double.valueOf(parseInteger(str));
    } catch(Exception e) {
      try {
        return parseReal(str);
      } catch(Exception e2) {
        throw new IllegalArgumentException("Bad number.");
      }
    }
  }

  public List<Double> parseLatOrLong(String str) {
    List<Double> result = new ArrayList<Double>();

    try {
      result.add(Double.valueOf(parseInteger(str.substring(0, str.indexOf("*")))));
      result.add(Double.valueOf(parseInteger(str.substring(str.indexOf("*") + 1, str.indexOf("'")))));
      result.add(parseNumber(str.substring(str.indexOf("'") + 1, str.indexOf("\""))));

      return result;
    } catch(Exception e) {
      throw new IllegalArgumentException("Bad latitude or longitude.");
    }
  }

  public boolean checkId(String id) {
    // Will add later.
    return true;
  }
}
