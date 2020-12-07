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

public class HelperMethods {
  private A_ParserHelper parserHelper;

  public HelperMethods(MyParserHelper parserHelper) {
    this.parserHelper = parserHelper;
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
      throw new IllegalArgumentException("Bad latitude or longitude: " + e);
    }
  }

  // Disgusting hack to check for unique IDs
  public void checkIds(List<String> ids) {
    CoordinatesWorld fakeWorldCoords;
    List<String> tempList;

    fakeWorldCoords = new CoordinatesWorld(
                      new Latitude(0, 0, 0.0),
                      new Longitude(0, 0, 0.0)
    );

    // Check against IDs in current command string
    for (int i = 0; i < ids.size(); i++) {
      tempList = ids;

      for (int j = 0; j < tempList.size(); j++) {
        if (ids.get(i).equals(tempList.get(j)) && i != j) {
          throw new IllegalArgumentException("ID already exists: " + ids.get(i));
        }
      }
    }

    // Check against previously saved IDs
    for (String id : ids) {
      if (parserHelper.hasReference(id)) {
        throw new IllegalArgumentException("ID already exists: " + id);
      }
    }

    // Only save IDs if all are good.
    for (String id : ids) {
      parserHelper.addReference(id, fakeWorldCoords);
    }
  }
}
