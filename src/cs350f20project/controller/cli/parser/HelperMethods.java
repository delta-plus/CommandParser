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

  public void checkId(String id) {
    // Disgusting hack to check for unique IDs
    CoordinatesWorld fakeWorldCoords;

    fakeWorldCoords = new CoordinatesWorld(
                      new Latitude(0, 0, 0.0),
                      new Longitude(0, 0, 0.0)
    );

    if (!parserHelper.hasReference(id)) {
      parserHelper.addReference(id, fakeWorldCoords);
    } else {
      throw new IllegalArgumentException("ID already exists.");
    }
  }
}
