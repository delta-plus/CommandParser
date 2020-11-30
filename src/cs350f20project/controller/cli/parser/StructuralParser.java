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
            A_Command command = new CommandStructuralCommit();
            parserHelper.getActionProcessor().schedule(command);

        } else if (cmd.toUpperCase().startsWith("COUPLE ")) {
            parseStructCouple(cmd.substring(7));
        } else if (cmd.toUpperCase().startsWith("LOCATE ")) {
            parseStructLocate(cmd.substring(7));
        }
    }

    public void parseStructCouple(String cmd) {
        String id1, id2;
        Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*$");
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

    public void parseStructLocate(String cmd) {
        String id1, id2, number;
        Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*$");
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
}
