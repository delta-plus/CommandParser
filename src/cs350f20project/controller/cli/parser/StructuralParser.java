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

public class StructuralParser {
    private A_ParserHelper parserHelper;

    public StructuralParser(MyParserHelper parserHelper) {
        this.parserHelper = parserHelper;
    }

    public void parseStructCmds(String cmd) {
      if (cmd.toUpperCase().startsWith("COMMIT")) {
      // COMMAND 60
        A_Command command = new CommandStructuralCommit();
        parserHelper.getActionProcessor().schedule(command);
      } else if (cmd.toUpperCase().startsWith("COUPLE ")) {
        // COMMAND 61
        parseStructCouple(cmd.substring(7));
      } else if (cmd.toUpperCase().startsWith("LOCATE ")) {
      // COMMAND 62
        parseStructLocate(cmd.substring(7));
      } else if (cmd.toUpperCase().startsWith("USE ")) {
      // COMMAND 66
        parseStructUseAsRef(cmd.substring(4));
      } else {
        System.out.println("Bad command near: " + cmd);
      }
  }

  private void parseStructCouple(String cmd) {
    String id1, id2;
    Pattern pattern = Pattern.compile("^[_a-zA-Z]+\\w*$");
    Matcher matcher;

    if (cmd.toUpperCase().startsWith("STOCK ")) {
      cmd = cmd.substring(6);
      id1 = cmd.substring(0, cmd.indexOf(" "));
      matcher = pattern.matcher(id1);

      if (!matcher.find()) {
        System.out.println("Bad ID");
        return;
      }

      cmd = cmd.substring(cmd.indexOf(" ") + 1);

      if (cmd.toUpperCase().startsWith("AND ")) {
        id2 = cmd.substring(4);
        matcher = pattern.matcher(id2);

        if (matcher.find()) {
          A_Command command = new CommandStructuralCouple(id1, id2);
          parserHelper.getActionProcessor().schedule(command);
        } else {
          System.out.println("Bad ID");
          return;
        }
      }
    }
  }

  private void parseStructLocate(String cmd) {
    String id1, id2, number;
    Pattern pattern = Pattern.compile("^[_a-zA-Z]+\\w*$");
    Pattern numPattern = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
    Matcher matcher;

    if (cmd.toUpperCase().startsWith("STOCK ")) {
      cmd = cmd.substring(6);
      id1 = cmd.substring(0, cmd.indexOf(" "));
      matcher = pattern.matcher(id1);

      if (!matcher.find()) {
        System.out.println("Bad ID");
        return;
      }

      cmd = cmd.substring(cmd.indexOf(" ") + 1);

      if (cmd.toUpperCase().startsWith("ON TRACK ")) {
        cmd = cmd.substring(9);
        id2 = cmd.substring(0, cmd.indexOf(" "));
        matcher = pattern.matcher(id2);

        if (!matcher.find()) {
          System.out.println("Bad ID");
          return;
        }

        cmd = cmd.substring(cmd.indexOf(" ") + 1);

        if (cmd.toUpperCase().startsWith("DISTANCE ")) {
          cmd = cmd.substring(9);
          number = cmd.substring(0, cmd.indexOf(" "));
          matcher = numPattern.matcher(number);

          if (!matcher.find()) {
            System.out.println("Bad number");
            return;
          }

          cmd = cmd.substring(cmd.indexOf(" ") + 1);

          if (cmd.toUpperCase().startsWith("FROM ")) {
            cmd = cmd.substring(5);

            if (cmd.toUpperCase().startsWith("START")) {
              TrackLocator trackLoc = new TrackLocator(id2, Double.parseDouble(number), true);
              A_Command command = new CommandStructuralLocate(id1, trackLoc);
              parserHelper.getActionProcessor().schedule(command);
            } else if (cmd.toUpperCase().startsWith("END")) {
              TrackLocator trackLoc = new TrackLocator(id2, Double.parseDouble(number), false);
              A_Command command = new CommandStructuralLocate(id1, trackLoc);
              parserHelper.getActionProcessor().schedule(command);
            }
          }
        }
      }
    }
  }

  private void parseStructUseAsRef(String cmd) {
    String id;
    List<Double> latitude = new ArrayList<Double>();
    List<Double> longitude = new ArrayList<Double>();
    CoordinatesWorld worldCoords = null;
    Pattern pattern = Pattern.compile("^[_a-zA-Z]+\\w*$");
    Matcher matcher;

    id = cmd.substring(0, cmd.indexOf(" "));
    matcher = pattern.matcher(id);

    if (!matcher.find()) {
      System.out.println("Bad ID.");
      return;
    }

    cmd = cmd.substring(cmd.indexOf(" ") + 1);

    if (cmd.toUpperCase().startsWith("AS REFERENCE ")) {
      cmd = cmd.substring(13);

      try {
        latitude = HelperMethods.parseLatOrLong(cmd.substring(0, cmd.indexOf("\"") + 1).replaceAll("\\s+", ""));
        longitude = HelperMethods.parseLatOrLong(cmd.substring(cmd.indexOf("/") + 1, cmd.indexOf("\"", cmd.indexOf("\"") + 1) + 1).replaceAll("\\s+", ""));
        worldCoords = new CoordinatesWorld(
          new Latitude(latitude.get(0).intValue(), latitude.get(1).intValue(), latitude.get(2)), 
          new Longitude(longitude.get(0).intValue(), longitude.get(1).intValue(), longitude.get(2))
        );
      } catch(Exception e) {
        System.out.println(e);
	return;
      }

      parserHelper.addReference(id, worldCoords);
    } else {
      System.out.println("Bad command near: " + cmd);
    }
  }
}
