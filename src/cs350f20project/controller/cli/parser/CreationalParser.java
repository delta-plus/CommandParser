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
        } else if (cmd.toUpperCase().startsWith("STOCK ")) {
            parseCreateStockCmds(cmd.substring(6));
        } else if (cmd.toUpperCase().startsWith("TRACK ")) {
            parseCreateTrackCmds(cmd.substring(6));
        } else {
            System.out.println("Bad command near: " + cmd);
            return;
        }
    }

    private void parseCreatePowerCmds(String cmd) {
        String id, id2;
        double number;
        StringTokenizer potentialIds;
        List<String> ids = new ArrayList<String>();
        List<Double> latitude = new ArrayList<Double>();
        List<Double> longitude = new ArrayList<Double>();
        double deltaNum1;
        double deltaNum2;
        Pattern pattern = Pattern.compile("^[_a-zA-Z]+\\w*$");
        Matcher matcher;

        if (cmd.toUpperCase().startsWith("CATENARY ")) {
            // COMMAND 22

            cmd = cmd.substring(9);
            id = cmd.substring(0, cmd.indexOf(" "));
            matcher = pattern.matcher(id);

            if (!matcher.find()) {
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
                    } else {
                        System.out.println("Bad ID.");
                        return;
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
        } else if (cmd.toUpperCase().startsWith("POLE ")) {
            // COMMAND 23
            cmd = cmd.substring(5);
            id = cmd.substring(0, cmd.indexOf(" "));
            matcher = pattern.matcher(id);

            if (!matcher.find()) {
                System.out.println("Bad ID.");
                return;
            }
            cmd = cmd.substring(cmd.indexOf(" ") + 1);

            if (cmd.toUpperCase().startsWith("ON TRACK ")) {
                cmd = cmd.substring(9);
                id2 = cmd.substring(0, cmd.indexOf(" "));
                matcher = pattern.matcher(id2);

                if (!matcher.find()) {
                    System.out.println("Bad ID.");
                    return;
                }
                cmd = cmd.substring(cmd.indexOf(" ") + 1);

                if (cmd.toUpperCase().startsWith("DISTANCE ")) {
                    cmd = cmd.substring(9);
                    number = HelperMethods.parseNumber(cmd.substring(0, cmd.indexOf(" ")));
                    cmd = cmd.substring(cmd.indexOf(" ") + 1);

                    if (cmd.toUpperCase().startsWith("FROM ")) {
                        cmd = cmd.substring(5);

                        if (cmd.toUpperCase().endsWith("START")) {
                            TrackLocator locator = new TrackLocator(id2, number, true);
                            A_Command command = new CommandCreatePowerPole(id, locator);
                            parserHelper.getActionProcessor().schedule(command);
                        } else if (cmd.toUpperCase().endsWith("END")) {
                            TrackLocator locator = new TrackLocator(id2, number, false);
                            A_Command command = new CommandCreatePowerPole(id, locator);
                            parserHelper.getActionProcessor().schedule(command);
                        } else {
                            System.out.println("Bad Command");
                            return;
                        }
                    } else {
                        System.out.println("Bad Command");
                        return;
                    }
                } else {
                    System.out.println("Bad Command");
                    return;
                }
            } else {
                System.out.println("Bad Command");
                return;
            }
        } else if (cmd.toUpperCase().startsWith("STATION ")) {
            // COMMAND 24

            cmd = cmd.substring(8);
            id = cmd.substring(0, cmd.indexOf(" "));
            matcher = pattern.matcher(id);

            if (!matcher.find()) {
                System.out.println("Bad ID.");
                return;
            }

            cmd = cmd.substring(cmd.indexOf(" ") + 1);

            if (cmd.toUpperCase().startsWith("REFERENCE ")) {
                cmd = cmd.substring(10);

                if (cmd.startsWith("$")) {
                    ; // Need to add code to for setting, checking, and retrieving IDs
                } else {
                    latitude = HelperMethods.parseLatOrLong(cmd.substring(0, cmd.indexOf("\"") + 1).replaceAll("\\s+", ""));
                    longitude = HelperMethods.parseLatOrLong(cmd.substring(cmd.indexOf("/") + 1, cmd.indexOf("\"", cmd.indexOf("\"") + 1) + 1).replaceAll("\\s+", ""));
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
                            } else {
                                System.out.println("Bad ID.");
                                return;
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
                            } else {
                                System.out.println("Bad ID.");
                                return;
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
        } else if (cmd.toUpperCase().startsWith("SUBSTATION ")) {
            // COMMAND 25
            cmd = cmd.substring(11);
            id = cmd.substring(0, cmd.indexOf(" "));
            matcher = pattern.matcher(id);

            if (!matcher.find()) {
                System.out.println("Bad ID.");
                return;
            }

            cmd = cmd.substring(cmd.indexOf(" ")+1);

            if(cmd.toUpperCase().startsWith("REFERENCE ")){
                cmd = cmd.substring(10);

                if (cmd.startsWith("$")) {
                    ; // Need to add code to for setting, checking, and retrieving IDs
                } else {
                    latitude = HelperMethods.parseLatOrLong(cmd.substring(0, cmd.indexOf("\"") + 1).replaceAll("\\s+", ""));
                    longitude = HelperMethods.parseLatOrLong(cmd.substring(cmd.indexOf("/") + 1, cmd.indexOf("\"", cmd.indexOf("\"") + 1) + 1).replaceAll("\\s+", ""));
                }

                cmd = cmd.substring(cmd.indexOf("\"", cmd.indexOf("\"") + 1) + 2);

                if (cmd.toUpperCase().startsWith("DELTA ")) {
                    cmd = cmd.substring(6);

                    deltaNum1 = HelperMethods.parseNumber(cmd.substring(0, cmd.indexOf(":")).trim());
                    deltaNum2 = HelperMethods.parseNumber(cmd.substring(cmd.indexOf(":") + 1, cmd.toUpperCase().indexOf("WITH")).trim());

                    cmd = cmd.substring(cmd.toUpperCase().indexOf("WITH "));
                    cmd = cmd.substring(5);

                    if (cmd.toUpperCase().startsWith("CATENARIES ")) {
                        cmd = cmd.substring(11);

                        potentialIds = new StringTokenizer(cmd);

                        while (potentialIds.hasMoreTokens()) {
                            id = potentialIds.nextToken();
                            matcher = pattern.matcher(id);

                            if (matcher.find()) {
                                ids.add(id);
                            } else {
                                System.out.println("Bad ID.");
                                return;
                            }
                        }

                        if (ids.size() > 0) {
                            A_Command command = new CommandCreatePowerSubstation(id,
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
            } else {
                System.out.println("Bad command.");
                return;
            }
        } else {
            System.out.println("Bad command near: " + cmd);
        }
    }

    private void parseCreateStockCmds(String cmd) {
        String id;
        String id2;
        double distance;
        Pattern pattern = Pattern.compile("^[_a-zA-Z]+\\w*$");
        Matcher matcher;

        if (cmd.toUpperCase().startsWith("CAR ")) {
            // COMMANDS 28 and 29

            cmd = cmd.substring(4);
            id = cmd.substring(0, cmd.indexOf(" "));
            matcher = pattern.matcher(id);

            if (!matcher.find()) {
                System.out.println("Bad ID.");
                return;
            }

            cmd = cmd.substring(cmd.indexOf(" ") + 1);

            if (cmd.toUpperCase().endsWith("AS BOX")) {
                A_Command command = new CommandCreateStockCarBox(id);
                parserHelper.getActionProcessor().schedule(command);
            } else if (cmd.toUpperCase().endsWith("AS CABOOSE")) {
                A_Command command = new CommandCreateStockCarCaboose(id);
                parserHelper.getActionProcessor().schedule(command);
            } else {
                System.out.println("Bad command near: " + cmd);
            }
        } else if (cmd.toUpperCase().startsWith("ENGINE ")) {
            // COMMAND 34

            cmd = cmd.substring(7);
            id = cmd.substring(0, cmd.indexOf(" "));
            matcher = pattern.matcher(id);

            if (!matcher.find()) {
                System.out.println("Bad ID.");
                return;
            }

            cmd = cmd.substring(cmd.indexOf(" ") + 1);

            if (cmd.toUpperCase().startsWith("AS DIESEL ON TRACK ")) {
                cmd = cmd.substring(19);
                id2 = cmd.substring(0, cmd.indexOf(" "));
                matcher = pattern.matcher(id2);

                if (!matcher.find()) {
                    System.out.println("Bad ID.");
                    return;
                }

                cmd = cmd.substring(cmd.indexOf(" ") + 1);

                if (cmd.toUpperCase().startsWith("DISTANCE ")) {
                    cmd = cmd.substring(9);
                    distance = HelperMethods.parseNumber(cmd.substring(0, cmd.indexOf(" ")));
                    cmd = cmd.substring(cmd.indexOf(" ") + 1);

                    if (cmd.toUpperCase().endsWith("FROM START FACING START")) {
                        A_Command command = new CommandCreateStockEngineDiesel(id, new TrackLocator(id2, distance, true), true);
                        parserHelper.getActionProcessor().schedule(command);
                    } else if (cmd.toUpperCase().endsWith("FROM START FACING END")) {
                        A_Command command = new CommandCreateStockEngineDiesel(id, new TrackLocator(id2, distance, true), false);
                        parserHelper.getActionProcessor().schedule(command);
                    } else if (cmd.toUpperCase().endsWith("FROM END FACING START")) {
                        A_Command command = new CommandCreateStockEngineDiesel(id, new TrackLocator(id2, distance, false), true);
                        parserHelper.getActionProcessor().schedule(command);
                    } else if (cmd.toUpperCase().endsWith("FROM END FACING END")) {
                        A_Command command = new CommandCreateStockEngineDiesel(id, new TrackLocator(id2, distance, false), false);
                        parserHelper.getActionProcessor().schedule(command);
                    } else {
                        System.out.println("Bad command near: " + cmd);
                    }
                } else {
                    System.out.println("Bad command near: " + cmd);
                }
            } else {
                System.out.println("Bad command near: " + cmd);
            }
        }
    }

    private void parseCreateTrackCmds(String cmd) {
        String id;
        String id2;
        double distance1;
        double distance2;
        StringTokenizer potentialIds;
        List<String> ids = new ArrayList<String>();
        List<Double> latitude = new ArrayList<Double>();
        List<Double> longitude = new ArrayList<Double>();
        double deltaNum1;
        double deltaNum2;
        CoordinatesWorld worldCoords = null;
        CoordinatesDelta deltaCoords1;
        CoordinatesDelta deltaCoords2;
        CoordinatesDelta deltaCoords3;
        CoordinatesDelta deltaCoords4;
        Pattern pattern = Pattern.compile("^[_a-zA-Z]+\\w*$");
        Matcher matcher;

        if (cmd.toUpperCase().startsWith("CURVE REFERENCE ")) {
            // COMMAND 43
            //cmd = cmd.substring(16);

        } else if (cmd.toUpperCase().startsWith("LAYOUT ")) {
            // COMMAND 45

            cmd = cmd.substring(7);
            id = cmd.substring(0, cmd.indexOf(" "));
            matcher = pattern.matcher(id);

            if (!matcher.find()) {
                System.out.println("Bad ID.");
                return;
            }

            cmd = cmd.substring(cmd.indexOf(" ") + 1);

            if (cmd.toUpperCase().startsWith("WITH TRACKS ")) {
                cmd = cmd.substring(12);
                potentialIds = new StringTokenizer(cmd);

                while (potentialIds.hasMoreTokens()) {
                    id = potentialIds.nextToken();
                    matcher = pattern.matcher(id);

                    if (matcher.find()) {
                        ids.add(id);
                    } else {
                        System.out.println("Bad ID.");
                        return;
                    }
                }

                if (ids.size() > 0) {
                    A_Command command = new CommandCreateTrackLayout(id, ids);
                    parserHelper.getActionProcessor().schedule(command);
                } else {
                    System.out.println("Bad ID.");
                }
            } else {
                System.out.println("Bad command near: " + cmd);
            }
        } else if (cmd.toUpperCase().startsWith("STRAIGHT ")) {
            // COMMAND 47

            cmd = cmd.substring(9);
            id = cmd.substring(0, cmd.indexOf(" "));
            matcher = pattern.matcher(id);

            if (!matcher.find()) {
                System.out.println("Bad ID.");
                return;
            }

            cmd = cmd.substring(cmd.indexOf(" ") + 1);

            if (cmd.toUpperCase().startsWith("REFERENCE ")) {
                cmd = cmd.substring(10);

                if (cmd.startsWith("$")) {
                    ; // Need to add code to for setting, checking, and retrieving IDs
                } else {
                    latitude = HelperMethods.parseLatOrLong(cmd.substring(0, cmd.indexOf("\"") + 1).replaceAll("\\s+", ""));
                    longitude = HelperMethods.parseLatOrLong(cmd.substring(cmd.indexOf("/") + 1, cmd.indexOf("\"", cmd.indexOf("\"") + 1) + 1).replaceAll("\\s+", ""));
                    worldCoords = new CoordinatesWorld(
                            new Latitude(latitude.get(0).intValue(), latitude.get(1).intValue(), latitude.get(2)),
                            new Longitude(longitude.get(0).intValue(), longitude.get(1).intValue(), longitude.get(2))
                    );
                }

                cmd = cmd.substring(cmd.indexOf("\"", cmd.indexOf("\"") + 1) + 2);

                if (cmd.toUpperCase().startsWith("DELTA START ")) {
                    cmd = cmd.substring(12);

                    try {
                        deltaNum1 = HelperMethods.parseNumber(cmd.substring(0, cmd.indexOf(":")).trim());
                        deltaNum2 = HelperMethods.parseNumber(cmd.substring(cmd.indexOf(":") + 1, cmd.toUpperCase().indexOf("END")).trim());
                        deltaCoords1 = new CoordinatesDelta(deltaNum1, deltaNum2);
                    } catch (Exception e) {
                        System.out.println(e);
                        return;
                    }

                    cmd = cmd.substring(cmd.toUpperCase().indexOf("END "));
                    cmd = cmd.substring(4);

                    try {
                        deltaNum1 = HelperMethods.parseNumber(cmd.substring(0, cmd.indexOf(":")).trim());
                        deltaNum2 = HelperMethods.parseNumber(cmd.substring(cmd.indexOf(":") + 1).trim());
                        deltaCoords2 = new CoordinatesDelta(deltaNum1, deltaNum2);
                    } catch (Exception e) {
                        System.out.println(e);
                        return;
                    }

                    A_Command command = new CommandCreateTrackStraight(id, new PointLocator(worldCoords, deltaCoords1, deltaCoords2));
                    parserHelper.getActionProcessor().schedule(command);
                } else {
                    System.out.println("Bad command near: " + cmd);
                }
            } else {
                System.out.println("Bad command near: " + cmd);
            }
        } else if (cmd.toUpperCase().startsWith("SWITCH TURNOUT ")) {
            // COMMAND 48
            ;
        } else if (cmd.toUpperCase().startsWith("SWITCH WYE ")) {
            // COMMAND 49

            cmd = cmd.substring(11);
            id = cmd.substring(0, cmd.indexOf(" "));
            matcher = pattern.matcher(id);

            if (!matcher.find()) {
                System.out.println("Bad ID.");
                return;
            }

            cmd = cmd.substring(cmd.indexOf(" ") + 1);

            if (cmd.toUpperCase().startsWith("REFERENCE ")) {
                cmd = cmd.substring(10);

                if (cmd.startsWith("$")) {
                    ; // Need to add code to for setting, checking, and retrieving IDs
                } else {
                    latitude = HelperMethods.parseLatOrLong(cmd.substring(0, cmd.indexOf("\"") + 1).replaceAll("\\s+", ""));
                    longitude = HelperMethods.parseLatOrLong(cmd.substring(cmd.indexOf("/") + 1, cmd.indexOf("\"", cmd.indexOf("\"") + 1) + 1).replaceAll("\\s+", ""));
                    worldCoords = new CoordinatesWorld(
                            new Latitude(latitude.get(0).intValue(), latitude.get(1).intValue(), latitude.get(2)),
                            new Longitude(longitude.get(0).intValue(), longitude.get(1).intValue(), longitude.get(2))
                    );
                }

                cmd = cmd.substring(cmd.indexOf("\"", cmd.indexOf("\"") + 1) + 2);

                if (cmd.toUpperCase().startsWith("DELTA START ")) {
                    cmd = cmd.substring(12);

                    try {
                        deltaNum1 = HelperMethods.parseNumber(cmd.substring(0, cmd.indexOf(":")).trim());
                        deltaNum2 = HelperMethods.parseNumber(cmd.substring(cmd.indexOf(":") + 1, cmd.toUpperCase().indexOf("END")).trim());
                        deltaCoords1 = new CoordinatesDelta(deltaNum1, deltaNum2);
                    } catch (Exception e) {
                        System.out.println(e);
                        return;
                    }

                    cmd = cmd.substring(cmd.toUpperCase().indexOf("END "));
                    cmd = cmd.substring(4);

                    try {
                        deltaNum1 = HelperMethods.parseNumber(cmd.substring(0, cmd.indexOf(":")).trim());
                        deltaNum2 = HelperMethods.parseNumber(cmd.substring(cmd.indexOf(":") + 1).trim());
                        deltaCoords2 = new CoordinatesDelta(deltaNum1, deltaNum2);
                    } catch (Exception e) {
                        System.out.println(e);
                        return;
                    }

                    cmd = cmd.substring(cmd.indexOf(" ") + 1);

                    if (cmd.toUpperCase().startsWith("DISTANCE ORIGIN ")) {
                        cmd = cmd.substring(16);

                        try {
                            distance1 = HelperMethods.parseNumber(cmd.substring(0, cmd.indexOf(" ")));
                        } catch (Exception e) {
                            System.out.println(e);
                            return;
                        }

                        cmd = cmd.substring(cmd.indexOf(" ") + 1);

                        if (cmd.toUpperCase().startsWith("DELTA START ")) {
                            cmd = cmd.substring(12);

                            try {
                                deltaNum1 = HelperMethods.parseNumber(cmd.substring(0, cmd.indexOf(":")).trim());
                                deltaNum2 = HelperMethods.parseNumber(cmd.substring(cmd.indexOf(":") + 1, cmd.toUpperCase().indexOf("END")).trim());
                                deltaCoords3 = new CoordinatesDelta(deltaNum1, deltaNum2);
                            } catch (Exception e) {
                                System.out.println(e);
                                return;
                            }

                            cmd = cmd.substring(cmd.toUpperCase().indexOf("END "));
                            cmd = cmd.substring(4);

                            try {
                                deltaNum1 = HelperMethods.parseNumber(cmd.substring(0, cmd.indexOf(":")).trim());
                                deltaNum2 = HelperMethods.parseNumber(cmd.substring(cmd.indexOf(":") + 1).trim());
                                deltaCoords4 = new CoordinatesDelta(deltaNum1, deltaNum2);
                            } catch (Exception e) {
                                System.out.println(e);
                                return;
                            }

                            cmd = cmd.substring(cmd.indexOf(" ") + 1);

                            if (cmd.toUpperCase().startsWith("DISTANCE ORIGIN ")) {
                                cmd = cmd.substring(16);

                                try {
                                    distance2 = HelperMethods.parseNumber(cmd.substring(0, cmd.indexOf(" ")));
                                } catch (Exception e) {
                                    System.out.println(e);
                                    return;
                                }

                                A_Command command = new CommandCreateTrackSwitchWye(id, worldCoords, deltaCoords1, deltaCoords2, new CoordinatesDelta(distance1, 1.0),
                                        deltaCoords3, deltaCoords4, new CoordinatesDelta(1.0, distance2));
                                parserHelper.getActionProcessor().schedule(command);
                                System.out.println("DEBUG: Got track switch wye!");
                            } else {
                                System.out.println("Bad command near: " + cmd);
                            }
                        } else {
                            System.out.println("Bad command near: " + cmd);
                        }
                    } else {
                        System.out.println("Bad command near: " + cmd);
                    }
                } else {
                    System.out.println("Bad command near: " + cmd);
                }
            } else {
                System.out.println("Bad command near: " + cmd);
            }
        } else {
            System.out.println("Bad command near: " + cmd);
        }
    }
}
