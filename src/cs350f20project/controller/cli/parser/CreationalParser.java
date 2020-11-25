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

public class CreationalParser {
  private A_ParserHelper parserHelper;

  public CreationalParser(MyParserHelper parserHelper) {
    this.parserHelper = parserHelper;
  }

  public void parseCreateCmds(String cmd) {
    if (cmd.toUpperCase().startsWith("POWER ")) {
      parseCreatePowerCmds(cmd.substring(6));
    }
  }

  private void parseCreatePowerCmds(String cmd) {
    String id;
    StringTokenizer potentialIds;
    List<String> ids = new ArrayList<String>();
    List<Double> latitude = new ArrayList<Double>();
    List<Double> longitude = new ArrayList<Double>();
    double deltaNum1;
    double deltaNum2;
    Pattern pattern = Pattern.compile("^[_a-zA-Z]+\\w*$");
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
          latitude = HelperMethods.parseLatOrLong(cmd.substring(0, cmd.indexOf("\"")).replaceAll("\\s+", ""));
          longitude = HelperMethods.parseLatOrLong(cmd.substring(cmd.indexOf("/") + 1, cmd.indexOf("\"", cmd.indexOf("\"") + 1)).replaceAll("\\s+", ""));
        }

	cmd = cmd.substring(cmd.indexOf("\"", cmd.indexOf("\"") + 1) + 2);

	if (cmd.toUpperCase().startsWith("DELTA ")) {
          cmd = cmd.substring(6);

          deltaNum1 = HelperMethods.parseNumber(cmd.substring(0, cmd.indexOf(":")).trim());
          deltaNum2 = HelperMethods.parseNumber(cmd.substring(cmd.indexOf(":") + 1, cmd.toUpperCase().indexOf("WITH")).trim());

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
}
