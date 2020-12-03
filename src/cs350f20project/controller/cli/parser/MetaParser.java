package cs350f20project.controller.cli.parser;

import com.sun.xml.internal.fastinfoset.util.StringArray;
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

public class MetaParser {
  private A_ParserHelper parserHelper;

  public MetaParser(MyParserHelper parserHelper) {
    this.parserHelper = parserHelper;
  }

  public void parseMetaCmds(String cmd) {

    if (cmd.toUpperCase().startsWith("@RUN ")) {
      cmd = cmd.substring(5);
      A_Command command = new CommandMetaDoRun(cmd);
      parserHelper.getActionProcessor().schedule(command);

    } else if (cmd.toUpperCase().startsWith("OPEN ")){
      cmd = cmd.substring(5);
      parseMetaOpen(cmd);
    }

  }

  public void parseMetaOpen(String cmd) {
    String id1, id2 = "";
    int int1, int2, int3;
    List<Double> latitude = new ArrayList<Double>();
    List<Double> longitude = new ArrayList<Double>();
    CoordinatesWorld cord;
    CoordinatesScreen screen;
    Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*$");
    Pattern numPattern = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
    Matcher matcher;

    if (cmd.toUpperCase().startsWith("VIEW ")) {
      cmd = cmd.substring(5);
      id1 = cmd.substring(0, cmd.indexOf(" "));
      matcher = pattern.matcher(id1);

      if (!matcher.find()) {
        System.out.println("Bad ID");
        return;
      }

      cmd = cmd.substring(cmd.indexOf(" ") + 1);

      if (cmd.toUpperCase().startsWith("ORIGIN ")) {
        cmd = cmd.substring(7);

        if (cmd.toUpperCase().startsWith("$")) {
          id2 = cmd.substring(1, cmd.indexOf(" "));
          matcher = pattern.matcher(id2);

          if (!matcher.find()) {
            System.out.println("Bad ID");
            return;
          }
          cmd = cmd.substring(cmd.indexOf(" ")+1);
        } else {
          latitude = HelperMethods.parseLatOrLong(cmd.substring(0, cmd.indexOf("\"") + 1).replaceAll("\\s+", ""));
          longitude = HelperMethods.parseLatOrLong(cmd.substring(cmd.indexOf("/") + 1, cmd.indexOf("\"", cmd.indexOf("\"") + 1) + 1).replaceAll("\\s+", ""));
          cmd = cmd.substring(cmd.indexOf("\"", cmd.indexOf("\"") + 1) + 2);

        }

        if (cmd.toUpperCase().startsWith("WORLD WIDTH ")) {
          cmd = cmd.substring(12);
          int1 = HelperMethods.parseInteger(cmd.substring(0, cmd.indexOf(" ")));
          cmd = cmd.substring(cmd.indexOf(" ") + 1);

          if (cmd.toUpperCase().startsWith("SCREEN WIDTH ")) {
            cmd = cmd.substring(13);
            int2 = HelperMethods.parseInteger(cmd.substring(0, cmd.indexOf(" ")));
            cmd = cmd.substring(cmd.indexOf(" ") + 1);

            if (cmd.toUpperCase().startsWith("HEIGHT ")) {
              cmd = cmd.substring(7);
              int3 = HelperMethods.parseInteger(cmd);

              if(id2.length() == 0){
                cord = new CoordinatesWorld(
                        new Latitude(latitude.get(0).intValue(), latitude.get(1).intValue(), latitude.get(2)),
                        new Longitude(longitude.get(0).intValue(), longitude.get(1).intValue(), longitude.get(2)));
                screen = new CoordinatesScreen(int2, int3);
                A_Command command = new CommandMetaViewGenerate(id1, cord, int1, screen);
                parserHelper.getActionProcessor().schedule(command);
              } else {
                //Still figuring out the id2
              }

            }
          }
        }
      }
    }
  }
}
