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
        } else if (cmd.toUpperCase().startsWith("SET ")) {
            parseDoSetCmds(cmd.substring(4));
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

            if (!matcher.find()) {
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

    public void parseDoSetCmds(String cmd) {
        String id;
        Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*$");
        Matcher matcher;

        if (cmd.toUpperCase().startsWith("REFERENCE ")) {
            cmd = cmd.substring(10);
            if (cmd.toUpperCase().startsWith("ENGINE ")) {
                cmd = cmd.substring(7);
                id = cmd;
                matcher = pattern.matcher(id);

                if (matcher.find()) {
                    A_Command command = new CommandBehavioralSetReference(id);
                    parserHelper.getActionProcessor().schedule(command);
                } else {
                    System.out.println("Bad ID.");
                }
            }
        } else {
            id = cmd.substring(0, cmd.indexOf(" "));
            cmd = cmd.substring(id.length() + 1);
            matcher = pattern.matcher(id);

            if (!matcher.find()) {
                System.out.println("Bad ID.");
                return;
            }

            if (cmd.toUpperCase().startsWith("DIRECTION ")) {
                cmd = cmd.substring(10);

                if (cmd.toUpperCase().endsWith("FORWARD")) {
                    A_Command command = new CommandBehavioralSetDirection(id, true);
                    parserHelper.getActionProcessor().schedule(command);
                } else if (cmd.toUpperCase().endsWith("BACKWARD")) {
                    A_Command command = new CommandBehavioralSetDirection(id, false);
                    parserHelper.getActionProcessor().schedule(command);
                }
            } else if (cmd.toUpperCase().startsWith("SPEED ")) {
                cmd = cmd.substring(6);
                String number = cmd;
                matcher = pattern.matcher(id);
                if (matcher.find()) {
                    A_Command command = new CommandBehavioralSetSpeed(id, Double.parseDouble(number));
                    parserHelper.getActionProcessor().schedule(command);
                } else {
                    System.out.println("Bad ID");
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
        List<String> ids = new ArrayList<String>();
        Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*$");
        Matcher matcher;

        ;
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
            return parseInteger(str);
        } catch (Exception e) {
            try {
                return parseReal(str);
            } catch (Exception e2) {
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
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad latitude or longitude.");
        }
    }

    public boolean checkId(String id) {
        // Will add later.
        return true;
    }
}